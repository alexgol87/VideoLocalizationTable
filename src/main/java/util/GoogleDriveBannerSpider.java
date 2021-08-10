package util;

import com.google.api.services.drive.Drive;
import com.google.api.services.sheets.v4.Sheets;
import dao.InMemoryBannerAndLocaleRepository;
import dao.InMemoryCreativeRepository;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static util.GeneralUtil.endTimeFixing;
import static util.GeneralUtil.startTimeFixing;

public class GoogleDriveBannerSpider implements Runnable {


    static final InMemoryBannerAndLocaleRepository bannerAndLocaleRepository = new InMemoryBannerAndLocaleRepository();
    static final InMemoryCreativeRepository bannerRepository = new InMemoryCreativeRepository();
    static final String DropboxBannerPreviewFolderCE = "/CE/BannerPreviewFolder";
    static final String DropboxBannerPreviewFolderCM = "/CM/BannerPreviewFolder";
    static final Map<String, String> folderDictionary = new HashMap<>();

    public GoogleDriveBannerSpider() {

        Instant start = startTimeFixing();

        DropboxApiUtil dropboxApiUtil = new DropboxApiUtil();
        Drive serviceDrive = GoogleDriveApiUtil.buildDriveApiClientService();
        Sheets serviceSheets = GoogleDriveApiUtil.buildSheetsApiClientService();

        folderDictionary.clear();
        GeneralUtil.getFolderIdNameDictionaryFromGoogleDrive(serviceDrive, folderDictionary);

        //CE
        bannerRepository.clear();
        bannerAndLocaleRepository.clear();

        GeneralUtil.bannerAndLocaleRepositoryFilling(serviceDrive, "name contains 'ce_' and (mimeType = 'image/jpeg' or mimeType = 'image/png') and trashed = false");
        GeneralUtil.bannerRepositoryFilling(bannerRepository);

        dropboxApiUtil.getDropboxFilesAndLinks(bannerRepository, DropboxBannerPreviewFolderCE);
        dropboxApiUtil.newPreviewUploadingToDropbox(bannerRepository, DropboxBannerPreviewFolderCE);
        dropboxApiUtil.getDropboxFilesAndLinks(bannerRepository, DropboxBannerPreviewFolderCE);

        GeneralUtil.getFolderLinksFromGoogleDrive(serviceDrive, bannerRepository, "b", "1w_aLc_CIy3RBRycNoG5QcbHnK6ORsowy");

        GoogleDriveApiUtil.clearAndPublishNewTableOnSpreadsheet(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED", bannerRepository, "banners COEm!A2:L");
        GoogleDriveApiUtil.publishModifiedTime(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED", "banners COEm!T1:T1");

        GeneralUtil.execTime = endTimeFixing(start);

        //CM
        bannerRepository.clear();
        bannerAndLocaleRepository.clear();

        GeneralUtil.bannerAndLocaleRepositoryFilling(serviceDrive, "name contains 'cm_' and (mimeType = 'image/jpeg' or mimeType = 'image/png') and trashed = false");
        GeneralUtil.bannerRepositoryFilling(bannerRepository);

        dropboxApiUtil.getDropboxFilesAndLinks(bannerRepository, DropboxBannerPreviewFolderCM);
        dropboxApiUtil.newPreviewUploadingToDropbox(bannerRepository, DropboxBannerPreviewFolderCM);
        dropboxApiUtil.getDropboxFilesAndLinks(bannerRepository, DropboxBannerPreviewFolderCM);

        GeneralUtil.getFolderLinksFromGoogleDrive(serviceDrive, bannerRepository, "b", "13fM6d2AxYbZSfBfhtF9YF2PPi3eDEAPt");

        GoogleDriveApiUtil.clearAndPublishNewTableOnSpreadsheet(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED", bannerRepository, "banners COM!A2:L");
        GoogleDriveApiUtil.publishModifiedTime(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED", "banners COM!T1:T1");

        GeneralUtil.execTime = endTimeFixing(start);
    }

    @Override
    public void run() {

    }
}

