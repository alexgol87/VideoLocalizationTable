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

// класс для работы с Dropbox
public class DropboxApiUtil {
    private String ACCESS_TOKEN = System.getenv("dropbox_key");
    private static final DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/AwemVideoCreativesPreview").build();
    private DbxClientV2 client;
    // временная папка, в которую изначально копируется preview-картинка креатива
    private static final String DIRECTORY_FOR_PREVIEW = "tmp/";
    // updatePreview - переменная показывает, был ли с веб-страницы запрос на обновление preview
    private static boolean updatePreview = false;

    public DropboxApiUtil() {
        //InputStream inputStream = getClass().getClassLoader().getResourceAsStream("dropbox.key");
        //BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        // this.ACCESS_TOKEN = reader.readLine();
        this.client = new DbxClientV2(config, ACCESS_TOKEN);
    }

    // метод удаляет папку с preview и пересоздает ее при необходимости обновления preview
    void createDropboxPreviewFolder(String directory) {
        try {
            if (updatePreview) {
                client.files().delete(directory);
                stopUpdatePreview();
            }
            client.files().createFolder(directory);
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }

    public static void startUpdatePreview() {
        updatePreview = true;
    }

    public static void stopUpdatePreview() {
        updatePreview = false;
    }

    // Get all Dropbox uploaded files and links
    void getDropboxFilesAndLinks(InMemoryCreativeRepository repository, String previewDirectory) {
        try {
            createDropboxPreviewFolder(previewDirectory);
            // берем все файлы, лежащие в папке с preview
            ListFolderResult result = client.files().listFolder(previewDirectory);
            while (true) {
                for (Metadata metadata : result.getEntries()) {
                    // вытаскиваем номер креатива из имени файла
                    int creativeNumber = parseIntSafely(metadata.getName().replace(".jpg", ""));
                    // для каждого экземпляра класса Creative проверяем наличие информации о креативе в репозитории и отсутствие сформированной ссылки на dropbox в поле thumbnailLink
                    if (repository.ifContainsCreative(creativeNumber) && !repository.getByCreativeNumber(creativeNumber).getThumbnailLink().contains("dropbox.com")) {
                        // запрашиваем все имеющиеся сформированные ссылки у файлов с папки preview
                        ListSharedLinksResult result2 = client.sharing().listSharedLinksBuilder()
                                .withPath(metadata.getPathLower()).withDirectOnly(true).start();
                        if (!result2.getLinks().isEmpty()) {
                            for (SharedLinkMetadata metadata2 : result2.getLinks()) {
                                // для каждого экземпляра класса Creative, у которого есть созданная ссылка на dropbox, обновляем поле thumbnailLink
                                repository.updateThumbnailLinkToDropboxLink(creativeNumber, metadata2.getUrl());
                            }
                        } else {
                            // // для каждого экземпляра класса Creative, у которого нет  созданной ссылки на dropbox, формируем эту ссылку и обновляем поле thumbnailLink
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

    // загружаем новые превью в папку на Dropbox
    void newPreviewUploadingToDropbox(InMemoryCreativeRepository repository, String previewDirectory) {
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
                            client.files().uploadBuilder(previewDirectory + "/" + v.getCreativeNumber() + ".jpg").uploadAndFinish(in2);
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
