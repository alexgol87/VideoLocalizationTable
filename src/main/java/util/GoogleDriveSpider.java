package util;

import com.google.api.services.drive.Drive;
import com.google.api.services.sheets.v4.Sheets;
import dao.InMemoryVideoAndLocaleRepository;
import dao.InMemoryVideoRepository;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class GoogleDriveSpider implements Runnable {

    static final InMemoryVideoAndLocaleRepository videoAndLocaleRepository = new InMemoryVideoAndLocaleRepository();
    static final InMemoryVideoRepository videoRepository = new InMemoryVideoRepository();
    public static String execTime;
    public static Set<String> videoErrors = new LinkedHashSet<>();
    public static Map<String, String> folderDictionary = new HashMap<>();

    public GoogleDriveSpider() {

        videoRepository.clear();
        videoAndLocaleRepository.clear();
        videoErrors.clear();
        folderDictionary.clear();

        Instant start = GeneralUtil.startTimeFixing();

        Drive serviceDrive = GoogleDriveApiUtil.buildDriveApiClientService();
        GeneralUtil.getFolderIdNameDictionaryFromGoogleDrive(serviceDrive);
        GeneralUtil.videoAndLocaleRepositoryFilling(serviceDrive);
        GeneralUtil.videoRepositoryFilling();

        DropboxApiUtil dropboxApiUtil = new DropboxApiUtil();
        dropboxApiUtil.getDropboxFilesAndLinks();
        // 100s
        dropboxApiUtil.newPreviewUploadingToDropbox();
        dropboxApiUtil.getDropboxFilesAndLinks();

        GeneralUtil.getFolderLinksFromGoogleDrive(serviceDrive);

        Sheets serviceSheets = GoogleDriveApiUtil.buildSheetsApiClientService();
        GoogleDriveApiUtil.clearAndPublishNewTableOnSpreadsheet(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED");
        GoogleDriveApiUtil.clearAndPublishErrorLogOnSpreadsheet(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED");

        GoogleDriveApiUtil.publishModifiedTime(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED");

        execTime = GeneralUtil.endTimeFixing(start);
    }

    @Override
    public void run() {

    }
}

