package util;

import com.google.api.services.drive.Drive;
import com.google.api.services.sheets.v4.Sheets;
import dao.InMemoryVideoAndLocaleRepository;
import dao.InMemoryVideoRepository;

public class GoogleDriveSpider {

    static final InMemoryVideoAndLocaleRepository videoAndLocaleRepository = new InMemoryVideoAndLocaleRepository();
    static final InMemoryVideoRepository videoRepository = new InMemoryVideoRepository();
    private static volatile GoogleDriveSpider Instance;

    public GoogleDriveSpider() {
        Drive serviceDrive = GoogleDriveApiUtil.buildDriveApiClientService();

        GeneralUtil.videoAndLocaleRepositoryFilling(serviceDrive);

        GeneralUtil.videoRepositoryFilling();

        DropboxApiUtil dropboxApiUtil = new DropboxApiUtil();
        dropboxApiUtil.getDropboxFilesAndLinks();
        dropboxApiUtil.newPreviewUploadingToDropbox();
        dropboxApiUtil.getDropboxFilesAndLinks();

        GeneralUtil.getFolderLinksFromGoogleDrive(serviceDrive);

        Sheets serviceSheets = GoogleDriveApiUtil.buildSheetsApiClientService();

        GoogleDriveApiUtil.clearAndPublishNewTableOnSpreadsheet(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED");

        GoogleDriveApiUtil.publishModifiedTime(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED");
    }

    public static GoogleDriveSpider getInstance() {
        GoogleDriveSpider result = Instance;
        if (result != null) {
            return result;
        }
        synchronized (GoogleDriveSpider.class) {
            if (Instance == null) {
                Instance = new GoogleDriveSpider();
            }
            return Instance;
        }
    }

    public static boolean checkInstance() {
        if (Instance != null) {
            return true;
        } else return false;
    }

}

