package util;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.sharing.ListSharedLinksResult;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import dao.InMemoryCreativeRepository;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static util.GeneralUtil.parseIntSafely;

public class DropboxApiUtil {
    private String ACCESS_TOKEN = System.getenv("dropbox_key");
    private static final DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/AwemVideoCreativesPreview").build();
    private DbxClientV2 client;
    private static final String DIRECTORY_FOR_PREVIEW = "tmp/";
    private static final String DROPBOX_DIRECTORY_FOR_VIDEO_PREVIEW = "/VideoPreviewFolder";
    private static final String DROPBOX_DIRECTORY_FOR_BANNER_PREVIEW = "/BannerPreviewFolder";
    private static boolean updateVideoPreview = false;
    private static boolean updateBannerPreview = false;

    public DropboxApiUtil() {
        //InputStream inputStream = getClass().getClassLoader().getResourceAsStream("dropbox.key");
        //BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        // this.ACCESS_TOKEN = reader.readLine();
        this.client = new DbxClientV2(config, ACCESS_TOKEN);
    }

    void createDropboxPreviewFolder(String directory, String creativeType) {
        try {
            if (creativeType.equals("b") && updateBannerPreview) client.files().delete(directory);
            else if (updateVideoPreview) client.files().delete(directory);
            client.files().createFolder(directory);
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }

    public static void startUpdateVideoPreview() {
        updateVideoPreview = true;
    }

    public static void stopUpdateVideoPreview() {
        updateVideoPreview = false;
    }

    public static void startUpdateBannerPreview() {
        updateBannerPreview = true;
    }

    public static void stopUpdateBannerPreview() {
        updateBannerPreview = false;
    }

    // Get all Dropbox uploaded files and links
    void getDropboxFilesAndLinks(InMemoryCreativeRepository repository, String creativeType) {
        String directory;
        try {
            if (creativeType.equals("b")) directory = DROPBOX_DIRECTORY_FOR_BANNER_PREVIEW;
            else directory = DROPBOX_DIRECTORY_FOR_VIDEO_PREVIEW;
            createDropboxPreviewFolder(directory, creativeType);
            ListFolderResult result = client.files().listFolder(directory);
            while (true) {
                for (Metadata metadata : result.getEntries()) {
                    int creativeNumber = parseIntSafely(metadata.getName().replace(".jpg", ""));
                    if (repository.ifContainsCreative(creativeNumber) && !repository.getByCreativeNumber(creativeNumber).getThumbnailLink().contains("dropbox.com")) {
                        ListSharedLinksResult result2 = client.sharing().listSharedLinksBuilder()
                                .withPath(metadata.getPathLower()).withDirectOnly(true).start();
                        if (!result2.getLinks().isEmpty()) {
                            for (SharedLinkMetadata metadata2 : result2.getLinks()) {
                                repository.updateThumbnailLinkToDropboxLink(creativeNumber, metadata2.getUrl());
                            }
                        } else {
                            SharedLinkMetadata sharedLink = client.sharing().createSharedLinkWithSettings(metadata.getPathLower());
                            repository.updateThumbnailLinkToDropboxLink(creativeNumber, sharedLink.getUrl());
                        }
                    }
                }
                if (!result.getHasMore()) {
                    break;
                }
                result = client.files().listFolderContinue(result.getCursor());
            }
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }

    void newPreviewUploadingToDropbox(InMemoryCreativeRepository repository, String creativeType) {
        String directory;
        if (creativeType.equals("b")) directory = DROPBOX_DIRECTORY_FOR_BANNER_PREVIEW;
        else directory = DROPBOX_DIRECTORY_FOR_VIDEO_PREVIEW;
        //create tmp folder
        File previewFolder = new File(DIRECTORY_FOR_PREVIEW);
        previewFolder.mkdir();

        repository.getAll()
                .stream()
                .forEach(v -> {
                    // upload to Dropbox
                    if (!v.getThumbnailLink().contains("dropbox.com") && !v.getThumbnailLink().isEmpty())
                        try {
                            InputStream in = new URL(v.getThumbnailLink()).openStream();
                            Files.copy(in, Paths.get(DIRECTORY_FOR_PREVIEW + v.getCreativeNumber() + ".jpg"), StandardCopyOption.REPLACE_EXISTING);
                            InputStream in2 = new FileInputStream(String.valueOf(Paths.get(DIRECTORY_FOR_PREVIEW + v.getCreativeNumber() + ".jpg")));
                            client.files().uploadBuilder(directory + "/" + v.getCreativeNumber() + ".jpg").uploadAndFinish(in2);
                            in.close();
                            in2.close();
                        } catch (IOException | DbxException ex) {
                            ex.printStackTrace();
                        }
                });
        // delete tmp folder
        String[] entries = previewFolder.list();
        for (String s : entries) {
            File currentFile = new File(previewFolder.getPath(), s);
            try {
                Files.deleteIfExists(currentFile.getAbsoluteFile().toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        previewFolder.delete();
    }
}
