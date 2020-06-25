package util;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import dao.InMemoryCreativeRepository;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static util.GoogleDriveApiUtil.getFileListFromDriveAPI;
import static util.GoogleDriveBannerSpider.bannerAndLocaleRepository;
import static util.GoogleDriveSpider.videoAndLocaleRepository;
import static util.GoogleDriveSpider.videoRepository;


public class GeneralUtil {
    static int parseIntSafely(String s) {
        return (s.matches("\\d+") && s.length() <= 9) ? Integer.parseInt(s) : -1;
    }

    static void creativeRepositoryFilling(InMemoryCreativeRepository repository) {
        videoAndLocaleRepository.getAll()
                .stream()
                .forEach(v -> {
                    if (!repository.ifContainsCreative(v.getVideoNumber()))
                        repository.add(v.getVideoNumber());
                    repository.update(v.getVideoNumber(), v.getLocale(), v.isVideoAll(), v.isVideoDSP(), v.isVideo1080x1080(), v.isVideo1080x1350(), v.isVideo1920x1080(), v.isVideo1080x1920(), false, v.getThumbnailLink());
                });
    }

    public static void videoAndLocaleRepositoryFilling(Drive service, String query) {
        String pageToken = null;
        while (true) {
            FileList result = getFileListFromDriveAPI(service, pageToken, query, "nextPageToken, files(id, name, webViewLink, lastModifyingUser, createdTime, thumbnailLink)");
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
                    }
                }
                pageToken = result.getNextPageToken();
                if (pageToken == null) break;
            }
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
                    if (videoRepository.ifContainsCreative(parseIntSafely(videoNumberString))) {
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

    public static void bannerAndLocaleRepositoryFilling(Drive service, String query) {
        String pageToken = null;
        while (true) {
            FileList result = getFileListFromDriveAPI(service, pageToken, query, "nextPageToken, files(id, name, webViewLink, lastModifyingUser, createdTime, thumbnailLink)");
            List<File> files = result.getFiles();
            if (files == null || files.isEmpty()) {
                System.out.println("No files found.");
            } else {
                for (File file : files) {
                    if (file.getName().toLowerCase().contains("_b") && (file.getName().split("_").length == 4 || file.getName().split("_").length == 5) && file.getName().toLowerCase().split("_")[1].matches("b\\d+") && file.getName().split("_")[2].length() >= 2 && parseIntSafely(file.getName().toLowerCase().split("_")[1].replace("b", "")) > 600) {
                        String[] fileNameParsedArray = file.getName().toLowerCase().split("_");
                        System.out.println(file.getName().toLowerCase());
                        int bannerNumber = parseIntSafely(fileNameParsedArray[1].replace("b", ""));
                        if (!bannerAndLocaleRepository.ifContainsCreativeAndLocale(bannerNumber + "_" + fileNameParsedArray[2]))
                            bannerAndLocaleRepository.add(bannerNumber, fileNameParsedArray[2]);
                        bannerAndLocaleRepository.update(bannerNumber + "_" + fileNameParsedArray[2], fileNameParsedArray[0], file.getThumbnailLink());
                    }
                }
                pageToken = result.getNextPageToken();
                if (pageToken == null) break;
            }
        }
    }
}
