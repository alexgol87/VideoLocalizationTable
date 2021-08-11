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
    static final String DropboxVideoPreviewFolderCE = "/CE/VideoPreviewFolder";
    static final String DropboxVideoPreviewFolderCM = "/CM/VideoPreviewFolder";

    public static final Set<String> videoErrorsCE = new LinkedHashSet<>();
    public static final Set<String> videoErrorsCM = new LinkedHashSet<>();
    static final Map<String, String> folderDictionary = new HashMap<>();

    public GoogleDriveSpider() {

        Instant start = GeneralUtil.startTimeFixing();

        DropboxApiUtil dropboxApiUtil = new DropboxApiUtil();
        Drive serviceDrive = GoogleDriveApiUtil.buildDriveApiClientService();
        Sheets serviceSheets = GoogleDriveApiUtil.buildSheetsApiClientService();

        folderDictionary.clear();
        GeneralUtil.getFolderIdNameDictionaryFromGoogleDrive(serviceDrive, folderDictionary);

        //CE
        videoErrorsCE.clear();
        videoRepository.clear();
        videoAndLocaleRepository.clear();

        GeneralUtil.videoAndLocaleRepositoryFilling(serviceDrive, "name contains 'ce_' and mimeType = 'video/mp4' and trashed = false", "ce", videoErrorsCE);
        GeneralUtil.videoRepositoryFilling(videoRepository);

        dropboxApiUtil.getDropboxFilesAndLinks(videoRepository, DropboxVideoPreviewFolderCE);
        dropboxApiUtil.newPreviewUploadingToDropbox(videoRepository, DropboxVideoPreviewFolderCE);
        dropboxApiUtil.getDropboxFilesAndLinks(videoRepository, DropboxVideoPreviewFolderCE);

        GeneralUtil.getFolderLinksFromGoogleDrive(serviceDrive, videoRepository, "v", "1RginzgJMxnxyc9BOHZcqsJaEBrg4Dwv6");

        GoogleDriveApiUtil.clearAndPublishNewTableOnSpreadsheet(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED", videoRepository, "video COEm!A2:L");
        GoogleDriveApiUtil.clearAndPublishErrorLogOnSpreadsheet(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED", "video COEm!W2:W500", videoErrorsCE);

        GoogleDriveApiUtil.publishModifiedTime(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED", "video COEm!R1:R1");

        // CM
        videoErrorsCM.clear();
        videoRepository.clear();
        videoAndLocaleRepository.clear();

        GeneralUtil.videoAndLocaleRepositoryFilling(serviceDrive, "name contains 'cm_' and mimeType = 'video/mp4' and trashed = false", "cm", videoErrorsCM);
        GeneralUtil.videoRepositoryFilling(videoRepository);

        dropboxApiUtil.getDropboxFilesAndLinks(videoRepository, DropboxVideoPreviewFolderCM);
        dropboxApiUtil.newPreviewUploadingToDropbox(videoRepository, DropboxVideoPreviewFolderCM);
        dropboxApiUtil.getDropboxFilesAndLinks(videoRepository, DropboxVideoPreviewFolderCM);

        GeneralUtil.getFolderLinksFromGoogleDrive(serviceDrive, videoRepository, "v", "1CXP7zG49GsMimpWKVbzzzyHzkZEhZGYf");

        GoogleDriveApiUtil.clearAndPublishNewTableOnSpreadsheet(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED", videoRepository, "video COM!A2:L");
        GoogleDriveApiUtil.clearAndPublishErrorLogOnSpreadsheet(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED", "video COM!W2:W500", videoErrorsCM);

        GoogleDriveApiUtil.publishModifiedTime(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED", "video COM!R1:R1");


        GeneralUtil.execTime = GeneralUtil.endTimeFixing(start);
    }

    @Override
    public void run() {

    }
}

