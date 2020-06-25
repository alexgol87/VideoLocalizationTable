package util;

import com.google.api.services.drive.Drive;
import dao.InMemoryBannerAndLocaleRepository;
import dao.InMemoryCreativeRepository;

import java.time.Instant;

import static util.GeneralUtil.endTimeFixing;
import static util.GeneralUtil.startTimeFixing;

public class GoogleDriveBannerSpider implements Runnable {


    static final InMemoryBannerAndLocaleRepository bannerAndLocaleRepository = new InMemoryBannerAndLocaleRepository();
    static final InMemoryCreativeRepository bannerRepository = new InMemoryCreativeRepository();
    public static String execTime;

    public GoogleDriveBannerSpider() {

        Instant start = startTimeFixing();

        Drive serviceDrive = GoogleDriveApiUtil.buildDriveApiClientService();
        GeneralUtil.bannerAndLocaleRepositoryFilling(serviceDrive, "(mimeType = 'image/jpeg' or mimeType = 'image/png') and trashed = false");
        GeneralUtil.creativeRepositoryFilling(bannerRepository);
        // 19s

        //DropboxApiUtil dropboxApiUtil = new DropboxApiUtil();
        // dropboxApiUtil.getDropboxFilesAndLinks();
        // 100s
        //dropboxApiUtil.newPreviewUploadingToDropbox();
        //dropboxApiUtil.getDropboxFilesAndLinks();

        //GeneralUtil.getFolderLinksFromGoogleDrive(serviceDrive);

        // Sheets serviceSheets = GoogleDriveApiUtil.buildSheetsApiClientService();
        // GoogleDriveApiUtil.clearAndPublishNewTableOnSpreadsheet(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED");

        //GoogleDriveApiUtil.publishModifiedTime(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED");

        execTime = endTimeFixing(start);
    }

    @Override
    public void run() {

    }
}

