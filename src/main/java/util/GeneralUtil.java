package util;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GeneralUtil {
    static int parseIntSafely(String s) {
        return (s.matches("\\d+") && s.length() <= 9) ? Integer.parseInt(s) : -1;
    }

    static void videoRepositoryFilling() {
        GoogleDriveSpider.videoAndLocaleRepository.getAll()
                .stream()
                .forEach(v -> {
                    if (!GoogleDriveSpider.videoRepository.ifContainsVideo(v.getVideoNumber()))
                        GoogleDriveSpider.videoRepository.add(v.getVideoNumber());
                    GoogleDriveSpider.videoRepository.update(v.getVideoNumber(), v.getLocale(), v.getAllData(), v.getDspData(), v.getLandscapeData(), v.getPortraitData(), v.getSquareData(), v.getFbfData(), v.getThumbnailLink());
                });
    }

    public static void videoAndLocaleRepositoryFilling(Drive service) {
        String pageToken = null;
        while (true) {
            FileList result = GoogleDriveApiUtil.getFileListFromDriveAPI(service, pageToken, "mimeType = 'video/mp4' and trashed = false", "nextPageToken, files(id, name, thumbnailLink, videoMediaMetadata, modifiedTime, lastModifyingUser, parents, size)");
            List<File> files = result.getFiles();
            if (files == null || files.isEmpty()) {
                System.out.println("No files found.");
            } else {
                for (File file : files) {
                    if (file.getName().toLowerCase().contains("_v") && file.getName().toLowerCase().contains("s") && (file.getName().split("_").length == 5 || file.getName().split("_").length == 6) && file.getName().toLowerCase().split("_")[1].matches("v\\d+") && file.getName().split("_")[2].length() >= 2 && !GoogleDriveSpider.folderDictionary.get(file.getParents().get(0)).equalsIgnoreCase("source") && !GoogleDriveSpider.folderDictionary.get(file.getParents().get(0)).equalsIgnoreCase("(footage)") && !GoogleDriveSpider.folderDictionary.get(file.getParents().get(0)).equalsIgnoreCase("asset") && !GoogleDriveSpider.folderDictionary.get(file.getParents().get(0)).equalsIgnoreCase("(видеоряд)") && parseIntSafely(file.getName().toLowerCase().split("_")[1].replace("v", "")) > 100) {
                        String[] fileNameParsedArray = file.getName().toLowerCase().split("_");
                        int videoNumber = parseIntSafely(fileNameParsedArray[1].replace("v", ""));
                        if (!GoogleDriveSpider.videoAndLocaleRepository.ifContainsVideoAndLocale(videoNumber + "_" + fileNameParsedArray[2]))
                            GoogleDriveSpider.videoAndLocaleRepository.add(videoNumber, fileNameParsedArray[2]);
                        GoogleDriveSpider.videoAndLocaleRepository.update(videoNumber + "_" + fileNameParsedArray[2], fileNameParsedArray[0], file.getThumbnailLink());
                        checkNameAndSizeOfCreative(file, fileNameParsedArray);
                    }
                }
                pageToken = result.getNextPageToken();
                if (pageToken == null) break;
            }
        }
    }

    public static void checkNameAndSizeOfCreative(File file, String[] fileNameParsedArray) {
        try {
            int videoWidth = file.getVideoMediaMetadata().getWidth();
            int videoHeight = file.getVideoMediaMetadata().getHeight();
            if (videoWidth != Integer.parseInt(fileNameParsedArray[0].split("x")[0]) || videoHeight != Integer.parseInt(fileNameParsedArray[0].split("x")[1])) {
                String error = String.format("=HYPERLINK(\"https://drive.google.com/drive/u/1/folders/%s\";\"File %s has wrong size: %s. lastModifyingUser: %s\")", file.getParents().get(0), file.getName().toLowerCase(), file.getVideoMediaMetadata().getWidth() + "x" + file.getVideoMediaMetadata().getHeight(), file.getLastModifyingUser().getDisplayName());
                //System.out.println(error);
                GoogleDriveSpider.videoErrors.add(error);
            }
            if (file.getName().contains("mp4.mp4")) {
                String error = String.format("=HYPERLINK(\"https://drive.google.com/drive/u/1/folders/%s\";\"File %s has wrong name. lastModifyingUser: %s\")", file.getParents().get(0), file.getName().toLowerCase(), file.getLastModifyingUser().getDisplayName());
                //System.out.println(error);
                GoogleDriveSpider.videoErrors.add(error);
            }
            if (file.getSize() > 52428800) {
                String error = String.format("=HYPERLINK(\"https://drive.google.com/drive/u/1/folders/%s\";\"File %s exceeds size of 50 MB. lastModifyingUser: %s\")", file.getParents().get(0), file.getName().toLowerCase(), file.getLastModifyingUser().getDisplayName());
                //System.out.println(error);
                GoogleDriveSpider.videoErrors.add(error);
            }
        } catch (NullPointerException e) {
            String error = String.format("=HYPERLINK(\"https://drive.google.com/drive/u/1/folders/%s\";\"File %s is corrupted. lastModifyingUser: %s\")", file.getParents().get(0), file.getName().toLowerCase(), file.getLastModifyingUser().getDisplayName());
            //System.out.println(error);
            GoogleDriveSpider.videoErrors.add(error);
        } catch (NumberFormatException e) {
            String error = String.format("=HYPERLINK(\"https://drive.google.com/drive/u/1/folders/%s\";\"File %s contains the Russian letter 'x'. lastModifyingUser: %s\")", file.getParents().get(0), file.getName().toLowerCase(), file.getLastModifyingUser().getDisplayName());
            //System.out.println(error);
            GoogleDriveSpider.videoErrors.add(error);
        }

    }

    public static void getFolderLinksFromGoogleDrive(Drive service) {
        String pageToken = null;
        while (true) {
            FileList result = GoogleDriveApiUtil.getFileListFromDriveAPI(service, pageToken, "'1RginzgJMxnxyc9BOHZcqsJaEBrg4Dwv6' in parents and mimeType='application/vnd.google-apps.folder' and trashed = false", "nextPageToken, files(name, webViewLink)");
            List<File> files = result.getFiles();
            if (files == null || files.isEmpty()) {
                System.out.println("No files found.");
            } else {
                for (File file : files) {
                    String videoNumberString = file.getName().split(" ")[0].replace("v", "");
                    if (GoogleDriveSpider.videoRepository.ifContainsVideo(parseIntSafely(videoNumberString))) {
                        GoogleDriveSpider.videoRepository.updateFolderLink(parseIntSafely(videoNumberString), file.getWebViewLink());
                    }
                }
            }
            pageToken = result.getNextPageToken();
            if (pageToken == null) break;
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
