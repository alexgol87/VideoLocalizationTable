package util;

import com.google.api.services.drive.Drive;
import com.google.api.services.sheets.v4.Sheets;
import dao.InMemoryVideoAndLocaleRepository;
import dao.InMemoryCreativeRepository;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class GoogleDriveSpider implements Runnable {

    static final InMemoryVideoAndLocaleRepository videoAndLocaleRepository = new InMemoryVideoAndLocaleRepository();
    static final InMemoryCreativeRepository videoRepository = new InMemoryCreativeRepository();

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
         GeneralUtil.videoAndLocaleRepositoryFilling(serviceDrive, "mimeType = 'video/mp4' and trashed = false");
         GeneralUtil.videoRepositoryFilling(videoRepository);
        // 19s

        //  DropboxApiUtil dropboxApiUtil = new DropboxApiUtil();
        //   dropboxApiUtil.getDropboxFilesAndLinks(videoRepository, "v");
        // 100s
        //  dropboxApiUtil.newPreviewUploadingToDropbox(videoRepository, "v");
        //  dropboxApiUtil.getDropboxFilesAndLinks(videoRepository, "v");

        //  GeneralUtil.getFolderLinksFromGoogleDrive(serviceDrive, videoRepository, "v");

        Sheets serviceSheets = GoogleDriveApiUtil.buildSheetsApiClientService();
        //   GoogleDriveApiUtil.clearAndPublishNewTableOnSpreadsheet(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED", videoRepository, "v");
        //  GoogleDriveApiUtil.clearAndPublishErrorLogOnSpreadsheet(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED");

        GoogleDriveApiUtil.publishModifiedTime(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED", "v");

        GeneralUtil.execTime = GeneralUtil.endTimeFixing(start);
    }

    @Override
    public void run() {

    }
}

