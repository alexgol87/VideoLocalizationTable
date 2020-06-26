package util;

import com.google.api.services.drive.Drive;
import com.google.api.services.sheets.v4.Sheets;
import dao.InMemoryVideoAndLocaleRepository;
import dao.InMemoryVideoRepository;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

import static util.GeneralUtil.endTimeFixing;
import static util.GeneralUtil.startTimeFixing;

public class GoogleDriveSpider implements Runnable {

    static final InMemoryVideoAndLocaleRepository videoAndLocaleRepository = new InMemoryVideoAndLocaleRepository();
    static final InMemoryVideoRepository videoRepository = new InMemoryVideoRepository();
    public static String execTime;
    public static Set<String> videoErrors = new LinkedHashSet<>();

    public GoogleDriveSpider() {

        Instant start = startTimeFixing();

        Drive serviceDrive = GoogleDriveApiUtil.buildDriveApiClientService();
        GeneralUtil.videoAndLocaleRepositoryFilling(serviceDrive);
        GeneralUtil.videoRepositoryFilling();
        // 19s

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

        execTime = endTimeFixing(start);
    }

    @Override
    public void run() {

    }
}

