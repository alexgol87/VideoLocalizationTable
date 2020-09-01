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

    public static final Set<String> videoErrors = new LinkedHashSet<>();
    public static final Map<String, String> folderDictionary = new HashMap<>();

    public GoogleDriveSpider() {

        Instant start = GeneralUtil.startTimeFixing();

        DropboxApiUtil dropboxApiUtil = new DropboxApiUtil();
        Drive serviceDrive = GoogleDriveApiUtil.buildDriveApiClientService();
        Sheets serviceSheets = GoogleDriveApiUtil.buildSheetsApiClientService();

        folderDictionary.clear();
        GeneralUtil.getFolderIdNameDictionaryFromGoogleDrive(serviceDrive);

        //CE
        videoErrors.clear();
        videoRepository.clear();
        videoAndLocaleRepository.clear();

        GeneralUtil.videoAndLocaleRepositoryFilling(serviceDrive, "mimeType = 'video/mp4' and trashed = false", "ce");
        GeneralUtil.videoRepositoryFilling(videoRepository);

        dropboxApiUtil.getDropboxFilesAndLinks(videoRepository, "/CE/VideoPreviewFolder");
        dropboxApiUtil.newPreviewUploadingToDropbox(videoRepository, "/CE/VideoPreviewFolder");
        dropboxApiUtil.getDropboxFilesAndLinks(videoRepository, "/CE/VideoPreviewFolder");

        GeneralUtil.getFolderLinksFromGoogleDrive(serviceDrive, videoRepository, "v", "1RginzgJMxnxyc9BOHZcqsJaEBrg4Dwv6");

        GoogleDriveApiUtil.clearAndPublishNewTableOnSpreadsheet(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED", videoRepository, "video COEm!A2:I");
        GoogleDriveApiUtil.clearAndPublishErrorLogOnSpreadsheet(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED", "video COEm!V2:V200");

        GoogleDriveApiUtil.publishModifiedTime(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED", "video COEm!Q1:Q1");

        // CM
        videoErrors.clear();
        videoRepository.clear();
        videoAndLocaleRepository.clear();

        GeneralUtil.videoAndLocaleRepositoryFilling(serviceDrive, "name contains 'cm_' and mimeType = 'video/mp4' and trashed = false", "cm");
        GeneralUtil.videoRepositoryFilling(videoRepository);

        dropboxApiUtil.getDropboxFilesAndLinks(videoRepository, "/CM/VideoPreviewFolder");
        dropboxApiUtil.newPreviewUploadingToDropbox(videoRepository, "/CM/VideoPreviewFolder");
        dropboxApiUtil.getDropboxFilesAndLinks(videoRepository, "/CM/VideoPreviewFolder");

        GeneralUtil.getFolderLinksFromGoogleDrive(serviceDrive, videoRepository, "v", "1CXP7zG49GsMimpWKVbzzzyHzkZEhZGYf");

        GoogleDriveApiUtil.clearAndPublishNewTableOnSpreadsheet(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED", videoRepository, "video COM!A2:I");
        GoogleDriveApiUtil.clearAndPublishErrorLogOnSpreadsheet(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED", "video COM!V2:V200");

        GoogleDriveApiUtil.publishModifiedTime(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED", "video COM!Q1:Q1");


        GeneralUtil.execTime = GeneralUtil.endTimeFixing(start);
    }

    @Override
    public void run() {

    }
}

