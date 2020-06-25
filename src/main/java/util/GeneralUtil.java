package util;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static util.GoogleDriveApiUtil.getFileListFromDriveAPI;
import static util.GoogleDriveSpider.*;

public class GeneralUtil {
    static int parseIntSafely(String s) {
        return (s.matches("\\d+") && s.length() <= 9) ? Integer.parseInt(s) : -1;
    }

    static void videoRepositoryFilling() {
        videoAndLocaleRepository.getAll()
                .stream()
                .forEach(v -> {
                    if (!videoRepository.ifContainsVideo(v.getVideoNumber()))
                        videoRepository.add(v.getVideoNumber());
                    videoRepository.update(v.getVideoNumber(), v.getLocale(), v.getAllData(), v.getDspData(), v.getLandscapeData(), v.getPortraitData(), v.getSquareData(), v.getFbfData(), v.getThumbnailLink());
                });
    }

    public static void videoAndLocaleRepositoryFilling(Drive service) {
        String pageToken = null;
        while (true) {
            FileList result = getFileListFromDriveAPI(service, pageToken, "mimeType = 'video/mp4' and trashed = false", "nextPageToken, files(id, name, thumbnailLink, videoMediaMetadata, modifiedTime)");
            List<File> files = result.getFiles();
            if (files == null || files.isEmpty()) {
                System.out.println("No files found.");
            } else {
                for (File file : files) {
                    if (file.getName().toLowerCase().contains("_v") && file.getName().toLowerCase().contains("s") && (file.getName().split("_").length == 5 || file.getName().split("_").length == 6) && file.getName().toLowerCase().split("_")[1].matches("v\\d+") && file.getName().split("_")[2].length() >= 2 && parseIntSafely(file.getName().toLowerCase().split("_")[1].replace("v", "")) > 100) {
                        String[] fileNameParsedArray = file.getName().toLowerCase().split("_");
                        int videoNumber = parseIntSafely(fileNameParsedArray[1].replace("v", ""));
                        if (!videoAndLocaleRepository.ifContainsVideoAndLocale(videoNumber + "_" + fileNameParsedArray[2]))
                            videoAndLocaleRepository.add(videoNumber, fileNameParsedArray[2]);
                        videoAndLocaleRepository.update(videoNumber + "_" + fileNameParsedArray[2], fileNameParsedArray[0], file.getThumbnailLink());
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
                String error = String.format("File %s has wrong size. Size from name: %s, size from file: %s", file.getName().toLowerCase(), fileNameParsedArray[0], file.getVideoMediaMetadata().getWidth() + "x" + file.getVideoMediaMetadata().getHeight());
                //System.out.println(error);
                videoErrors.add(error);
            }
        } catch (NullPointerException e) {
            String error = String.format("File %s is corrupted", file.getName().toLowerCase());
            //System.out.println(error);
            videoErrors.add(error);
        } catch (NumberFormatException e) {
            String error = String.format("File %s contains the Russian letter 'x'", file.getName().toLowerCase());
            //System.out.println(error);
            videoErrors.add(error);
        }

    }

    public static void getFolderLinksFromGoogleDrive(Drive service) {
        String pageToken = null;
        while (true) {
            FileList result = getFileListFromDriveAPI(service, pageToken, "'1RginzgJMxnxyc9BOHZcqsJaEBrg4Dwv6' in parents and mimeType='application/vnd.google-apps.folder' and trashed = false", "nextPageToken, files(name, webViewLink)");
            List<File> files = result.getFiles();
            if (files == null || files.isEmpty()) {
                System.out.println("No files found.");
            } else {
                for (File file : files) {
                    String videoNumberString = file.getName().split(" ")[0].replace("v", "");
                    if (videoRepository.ifContainsVideo(parseIntSafely(videoNumberString))) {
                        videoRepository.updateFolderLink(parseIntSafely(videoNumberString), file.getWebViewLink());
                    }
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
