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

// класс для работы с поиском баннерных креативов Community на Google Drive
public class GoogleDriveCommunitySpider implements Runnable {

    static final InMemoryBannerAndLocaleRepository communityBannerAndLocaleRepository = new InMemoryBannerAndLocaleRepository();
    static final InMemoryCreativeRepository communityBannerRepository = new InMemoryCreativeRepository();
    static final String DropboxCommunityBannerPreviewFolderCE = "/CE/CommunityBannerPreviewFolder";
    static final String DropboxCommunityBannerPreviewFolderCM = "/CM/CommunityBannerPreviewFolder";
    static final Map<String, String> folderDictionary = new HashMap<>();
    static final String marketingCommunityTeamDrive = "0AKtTKKJJOgywUk9PVA";

    public GoogleDriveCommunitySpider() {

        Instant start = startTimeFixing();

        DropboxApiUtil dropboxApiUtil = new DropboxApiUtil();
        Drive serviceDrive = GoogleDriveApiUtil.buildDriveApiClientService();
        Sheets serviceSheets = GoogleDriveApiUtil.buildSheetsApiClientService();

        folderDictionary.clear();

        communityBannerAndLocaleRepository.clear();
        communityBannerRepository.clear();

        GeneralUtil.communityBannerRepositoryFilling(serviceDrive, "(mimeType = 'image/jpeg' or mimeType = 'image/png') and trashed = false and name contains 'ce_bc'", marketingCommunityTeamDrive);
        GeneralUtil.bannerRepositoryFilling(communityBannerRepository);

        dropboxApiUtil.getDropboxFilesAndLinks(communityBannerRepository, DropboxCommunityBannerPreviewFolderCE);
        dropboxApiUtil.newPreviewUploadingToDropbox(communityBannerRepository, DropboxCommunityBannerPreviewFolderCE);
        dropboxApiUtil.getDropboxFilesAndLinks(communityBannerRepository, DropboxCommunityBannerPreviewFolderCE);

        GeneralUtil.getFolderLinksFromGoogleDrive(serviceDrive, communityBannerRepository, "bc", "1J7x22CRa13DMe-7Kg9fIxvkyQdl2ZR2x", marketingCommunityTeamDrive);

        GoogleDriveApiUtil.clearAndPublishNewTableOnSpreadsheet(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED", communityBannerRepository, "BC_COEm!A2:D");
        GoogleDriveApiUtil.publishModifiedTime(serviceSheets, "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "USER_ENTERED", "BC_COEm!K1:K1");

        GeneralUtil.execTime = endTimeFixing(start);
    }

    @Override
    public void run() {

    }
}

