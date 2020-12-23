package util;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import dao.InMemoryCreativeRepository;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneralUtil {

    public static String execTime;

    static int parseIntSafely(String s) {
        return (s.matches("\\d+") && s.length() <= 9) ? Integer.parseInt(s) : -1;
    }

    static void videoRepositoryFilling(InMemoryCreativeRepository repository) {
        GoogleDriveSpider.videoAndLocaleRepository.getAll()
                .stream()
                .forEach(v -> {
                    if (!repository.ifContainsCreative(v.getCreativeNumber()))
                        repository.add(v.getCreativeNumber());
                    repository.update(v.getCreativeNumber(), v.getLocale(), v.getAllData(), v.getSquareData(), v.getLandscapeData(), v.getPortraitData(), v.getDspData(), v.getFbfData(), false, v.getThumbnailLink());
                });
    }

    static void bannerRepositoryFilling(InMemoryCreativeRepository repository) {
        GoogleDriveBannerSpider.bannerAndLocaleRepository.getAll()
                .stream()
                .forEach(v -> {
                    if (!repository.ifContainsCreative(v.getCreativeNumber()))
                        repository.add(v.getCreativeNumber());
                    repository.update(v.getCreativeNumber(), v.getLocale(), v.getAllData(), v.getSquareData(), v.getLandscapeData(), v.getPortraitData(), false, false, v.getEtcData(), v.getThumbnailLink());
                });
    }

    public static void videoAndLocaleRepositoryFilling(Drive service, String query, String project, Set<String> videoErrors) {
        String pageToken = null;
        while (true) {
            FileList result = GoogleDriveApiUtil.getFileListFromDriveAPI(service, pageToken, query, "nextPageToken, files(id, name, thumbnailLink, videoMediaMetadata, modifiedTime, lastModifyingUser, parents, size)");
            List<File> files = result.getFiles();
            if (files == null || files.isEmpty()) {
                System.out.println("No files found.");
            } else {
                for (File file : files) {
                    String[] fileNameParsedArray = file.getName().toLowerCase().split("_");
                    String filename = file.getName().toLowerCase();
                    if (filename.matches("(.*)_\\d+s(.*)") && filename.matches("(.*)_v\\d+_(.*)") && !GoogleDriveSpider.folderDictionary.get(file.getParents().get(0)).equalsIgnoreCase("source") && !GoogleDriveSpider.folderDictionary.get(file.getParents().get(0)).equalsIgnoreCase("(footage)") && !GoogleDriveSpider.folderDictionary.get(file.getParents().get(0)).equalsIgnoreCase("asset") && !GoogleDriveSpider.folderDictionary.get(file.getParents().get(0)).equalsIgnoreCase("(видеоряд)")) {
                        String regexVideoNumber = "_v\\d+";
                        int videoNumber = 0;
                        Pattern pattern = Pattern.compile(regexVideoNumber);
                        Matcher matcher = pattern.matcher(filename);
                        while (matcher.find()) {
                            videoNumber = parseIntSafely(matcher.group(0).replace("_v", ""));
                        }
                        String regexLocale = "v\\d+_[a-z]{2,3}";
                        String videoLocale = "";
                        pattern = Pattern.compile(regexLocale);
                        matcher = pattern.matcher(filename);
                        while (matcher.find()) {
                            videoLocale = matcher.group(0).split("_")[1];
                        }
                        String regexVideoSize = "\\d+x\\d+";
                        String videoSize = "";
                        pattern = Pattern.compile(regexVideoSize);
                        matcher = pattern.matcher(filename.replace("х", "x"));
                        System.out.println(filename);
                        while (matcher.find()) {
                            videoSize = matcher.group(0);
                        }
                        //int videoNumber = parseIntSafely(fileNameParsedArray[2].replace("v", ""));
                        if (((project.equals("ce") && videoNumber > 100) || project.equals("cm"))) {
                            if (!GoogleDriveSpider.videoAndLocaleRepository.ifContainsVideoAndLocale(videoNumber + "_" + videoLocale))
                                GoogleDriveSpider.videoAndLocaleRepository.add(videoNumber, videoLocale);
                            GoogleDriveSpider.videoAndLocaleRepository.update(videoNumber + "_" + videoLocale, fileNameParsedArray[1], file.getThumbnailLink());
                            checkNameAndSizeOfCreative(file, fileNameParsedArray, videoErrors);
                        }
                    }
                }
                pageToken = result.getNextPageToken();
                if (pageToken == null) break;
            }
        }
    }

    public static void checkNameAndSizeOfCreative(File file, String[] fileNameParsedArray, Set<String> videoErrors) {
        try {
            int videoWidth = file.getVideoMediaMetadata().getWidth();
            int videoHeight = file.getVideoMediaMetadata().getHeight();
            if (fileNameParsedArray[1].matches("(.*)\\d+x\\d+(.*)"))
                if (videoWidth != Integer.parseInt(fileNameParsedArray[1].replace("х", "x").split("x")[0]) || videoHeight != Integer.parseInt(fileNameParsedArray[1].replace("х", "x").split("x")[1])) {
                    String error = String.format("=HYPERLINK(\"https://drive.google.com/drive/u/1/folders/%s\";\"%s has wrong size: %s. lastModifyingUser: %s\")", file.getParents().get(0), file.getName().toLowerCase(), file.getVideoMediaMetadata().getWidth() + "x" + file.getVideoMediaMetadata().getHeight(), file.getLastModifyingUser().getDisplayName());
                    //System.out.println(error);
                    videoErrors.add(error);
                }
            if (!fileNameParsedArray[0].equals("ce") && !fileNameParsedArray[0].equals("cm")) {
                String error = String.format("=HYPERLINK(\"https://drive.google.com/drive/u/1/folders/%s\";\"%s has wrong name: NO PROJECT PREFIX. lastModifyingUser: %s\")", file.getParents().get(0), file.getName().toLowerCase(), file.getLastModifyingUser().getDisplayName());
                //System.out.println(error);
                videoErrors.add(error);
            }
            if (file.getName().contains("mp4.mp4")) {
                String error = String.format("=HYPERLINK(\"https://drive.google.com/drive/u/1/folders/%s\";\"%s has wrong name. lastModifyingUser: %s\")", file.getParents().get(0), file.getName().toLowerCase(), file.getLastModifyingUser().getDisplayName());
                //System.out.println(error);
                videoErrors.add(error);
            }
            if (file.getName().contains("m4v")) {
                String error = String.format("=HYPERLINK(\"https://drive.google.com/drive/u/1/folders/%s\";\"%s has wrong extension M4V. lastModifyingUser: %s\")", file.getParents().get(0), file.getName().toLowerCase(), file.getLastModifyingUser().getDisplayName());
                //System.out.println(error);
                videoErrors.add(error);
            }
            if (file.getSize() > 41943040 && parseIntSafely(fileNameParsedArray[2].replace("v", "")) > 600) {
                String error = String.format("=HYPERLINK(\"https://drive.google.com/drive/u/1/folders/%s\";\"%s exceeds size of 40 MB. lastModifyingUser: %s\")", file.getParents().get(0), file.getName().toLowerCase(), file.getLastModifyingUser().getDisplayName());
                //System.out.println(error);
                videoErrors.add(error);
            }
        } catch (NullPointerException e) {
            String error = String.format("=HYPERLINK(\"https://drive.google.com/drive/u/1/folders/%s\";\"%s is corrupted. lastModifyingUser: %s\")", file.getParents().get(0), file.getName().toLowerCase(), file.getLastModifyingUser().getDisplayName());
            //System.out.println(error);
            videoErrors.add(error);
        } catch (NumberFormatException e) {
            String error = String.format("=HYPERLINK(\"https://drive.google.com/drive/u/1/folders/%s\";\"%s contains the Russian letter 'x'. lastModifyingUser: %s\")", file.getParents().get(0), file.getName().toLowerCase(), file.getLastModifyingUser().getDisplayName());
            //System.out.println(error);
            videoErrors.add(error);
        }

    }

    public static void getFolderLinksFromGoogleDrive(Drive service, InMemoryCreativeRepository repository, String creativeType, String directory) {
        String pageToken = null;
        while (true) {
            FileList result = GoogleDriveApiUtil.getFileListFromDriveAPI(service, pageToken, "'" + directory + "' in parents and mimeType='application/vnd.google-apps.folder' and trashed = false", "nextPageToken, files(name, webViewLink)");
            List<File> files = result.getFiles();
            if (files == null || files.isEmpty()) {
                System.out.println("No files found.");
            } else {
                for (File file : files) {
                    String creativeNumberString = file.getName().split(" ")[0].replace(creativeType, "");
                    if (repository.ifContainsCreative(parseIntSafely(creativeNumberString))) {
                        repository.updateFolderLink(parseIntSafely(creativeNumberString), file.getWebViewLink());
                    }
                }
            }
            pageToken = result.getNextPageToken();
            if (pageToken == null) break;
        }
    }

    public static void bannerAndLocaleRepositoryFilling(Drive service, String query) {
        String pageToken = null;
        while (true) {
            FileList result = GoogleDriveApiUtil.getFileListFromDriveAPI(service, pageToken, query, "nextPageToken, files(id, name, webViewLink, lastModifyingUser, createdTime, thumbnailLink)");
            List<File> files = result.getFiles();
            if (files == null || files.isEmpty()) {
                System.out.println("No files found.");
            } else {
                for (File file : files) {
                    String[] fileNameParsedArray = file.getName().toLowerCase().split("_");
                    String filename = file.getName().toLowerCase();
                    int bannerNumber = parseIntSafely(fileNameParsedArray[1].replace("b", ""));
                    if (filename.contains("_b") && (fileNameParsedArray.length == 4 || fileNameParsedArray.length == 5) && fileNameParsedArray[1].matches("b\\d+") && fileNameParsedArray[2].length() >= 2 && bannerNumber > 0) {
                        //System.out.println(file.getName().toLowerCase());
                        if (!GoogleDriveBannerSpider.bannerAndLocaleRepository.ifContainsCreativeAndLocale(bannerNumber + "_" + fileNameParsedArray[2]))
                            GoogleDriveBannerSpider.bannerAndLocaleRepository.add(bannerNumber, fileNameParsedArray[2]);
                        GoogleDriveBannerSpider.bannerAndLocaleRepository.update(bannerNumber + "_" + fileNameParsedArray[2], fileNameParsedArray[0], file.getThumbnailLink());
                    }
                }
                pageToken = result.getNextPageToken();
                if (pageToken == null) break;
            }
        }
    }

    public static void getFolderIdNameDictionaryFromGoogleDrive(Drive service) {
        String pageToken = null;
        while (true) {
            FileList result = GoogleDriveApiUtil.getFileListFromDriveAPI(service, pageToken, "mimeType='application/vnd.google-apps.folder' and trashed = false", "nextPageToken, files(id, name)");
            List<File> files = result.getFiles();
            if (files == null || files.isEmpty()) {
                System.out.println("No files found.");
            } else {
                for (File file : files) {
                    GoogleDriveSpider.folderDictionary.put(file.getId(), file.getName().toLowerCase());
                }
            }
            pageToken = result.getNextPageToken();
            if (pageToken == null) break;
        }
    }

    public static Instant startTimeFixing() {
        return Instant.now();
    }

    public static String endTimeFixing(Instant start) {
        Instant end = Instant.now();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("HH:mm:ss");
        Duration diff = Duration.between(start, end);
        LocalTime fTime = LocalTime.ofNanoOfDay(diff.toNanos());
        return fTime.format(df);
    }
}
