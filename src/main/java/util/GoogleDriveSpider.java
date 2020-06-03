package util;

import com.google.api.services.drive.Drive;
import com.google.api.services.sheets.v4.Sheets;
import dao.InMemoryVideoAndLocaleRepository;
import dao.InMemoryVideoRepository;

public class GoogleDriveSpider extends Thread {

    static final InMemoryVideoAndLocaleRepository videoAndLocaleRepository = new InMemoryVideoAndLocaleRepository();
    static final InMemoryVideoRepository videoRepository = new InMemoryVideoRepository();

    @Override
    public void run() {

        Drive serviceDrive = GoogleDriveApiUtil.buildDriveApiClientService();

        GeneralUtil.videoAndLocaleRepositoryFilling(serviceDrive);

        GeneralUtil.videoRepositoryFilling();

        DropboxApiUtil.getDropboxFilesAndLinks();

        DropboxApiUtil.newPreviewUploadingToDropbox();

        DropboxApiUtil.getDropboxFilesAndLinks();

        GeneralUtil.getFolderLinksFromGoogleDrive(serviceDrive);

        Sheets serviceSheets = GoogleDriveApiUtil.buildSheetsApiClientService();

        GoogleDriveApiUtil.clearAndPublishNewTableOnSpreadsheet(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED");

        GoogleDriveApiUtil.publishModifiedTime(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED");
    }
}

