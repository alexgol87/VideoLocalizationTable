package util;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import dao.InMemoryCreativeRepository;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

public class GeneralUtil {

    public static String execTime;

    static int parseIntSafely(String s) {
        return (s.matches("\\d+") && s.length() <= 9) ? Integer.parseInt(s) : -1;
    }

    static void videoRepositoryFilling(InMemoryCreativeRepository repository) {
        GoogleDriveSpider.videoAndLocaleRepository.getAll()
                .stream()
                .forEach(v -> {
                    if (!repository.ifContainsCreative(v.getCreativeNumber()))
                        repository.add(v.getCreativeNumber());
                    repository.update(v.getCreativeNumber(), v.getLocale(), v.getAllData(), v.getSquareData(), v.getLandscapeData(), v.getPortraitData(), v.getDspData(), v.getFbfData(), false, v.getThumbnailLink());
                });
    }

    static void bannerRepositoryFilling(InMemoryCreativeRepository repository) {
        GoogleDriveBannerSpider.bannerAndLocaleRepository.getAll()
                .stream()
                .forEach(v -> {
                    if (!repository.ifContainsCreative(v.getCreativeNumber()))
                        repository.add(v.getCreativeNumber());
                    repository.update(v.getCreativeNumber(), v.getLocale(), v.getAllData(), v.getSquareData(), v.getLandscapeData(), v.getPortraitData(), false, false, v.getEtcData(), v.getThumbnailLink());
                });
    }

    public static void videoAndLocaleRepositoryFilling(Drive service, String query, String project, Set<String> videoErrors) {
        String pageToken = null;
        while (true) {
            FileList result = GoogleDriveApiUtil.getFileListFromDriveAPI(service, pageToken, query, "nextPageToken, files(id, name, thumbnailLink, videoMediaMetadata, modifiedTime, lastModifyingUser, parents, size)");
            List<File> files = result.getFiles();
            if (files == null || files.isEmpty()) {
                System.out.println("No files found.");
            } else {
                for (File file : files) {
                    String[] fileNameParsedArray = file.getName().toLowerCase().split("_");
                    String filename = file.getName().toLowerCase();
                    if (filename.contains("_v") && filename.contains("s") && (fileNameParsedArray.length == 6 || fileNameParsedArray.length == 7 || fileNameParsedArray.length == 8) && fileNameParsedArray[2].matches("v\\d+") && !GoogleDriveSpider.folderDictionary.get(file.getParents().get(0)).equalsIgnoreCase("source") && !GoogleDriveSpider.folderDictionary.get(file.getParents().get(0)).equalsIgnoreCase("(footage)") && !GoogleDriveSpider.folderDictionary.get(file.getParents().get(0)).equalsIgnoreCase("asset") && !GoogleDriveSpider.folderDictionary.get(file.getParents().get(0)).equalsIgnoreCase("(видеоряд)")) {
                        int videoNumber = parseIntSafely(fileNameParsedArray[2].replace("v", ""));
                        if (project.equals("ce") && videoNumber > 100) {
                            if (!GoogleDriveSpider.videoAndLocaleRepository.ifContainsVideoAndLocale(videoNumber + "_" + fileNameParsedArray[3]))
                                GoogleDriveSpider.videoAndLocaleRepository.add(videoNumber, fileNameParsedArray[3]);
                            GoogleDriveSpider.videoAndLocaleRepository.update(videoNumber + "_" + fileNameParsedArray[3], fileNameParsedArray[1], file.getThumbnailLink());
                            checkNameAndSizeOfCreative(file, fileNameParsedArray, videoErrors);
                        } else if (project.equals("cm")) {
                            if (!GoogleDriveSpider.videoAndLocaleRepository.ifContainsVideoAndLocale(videoNumber + "_" + fileNameParsedArray[3]))
                                GoogleDriveSpider.videoAndLocaleRepository.add(videoNumber, fileNameParsedArray[3]);
                            GoogleDriveSpider.videoAndLocaleRepository.update(videoNumber + "_" + fileNameParsedArray[3], fileNameParsedArray[1], file.getThumbnailLink());
                            checkNameAndSizeOfCreative(file, fileNameParsedArray, videoErrors);
                        }
                    }
                }
                pageToken = result.getNextPageToken();
                if (pageToken == null) break;
            }
        }
    }

    public static void checkNameAndSizeOfCreative(File file, String[] fileNameParsedArray, Set<String> videoErrors) {
        try {
            int videoWidth = file.getVideoMediaMetadata().getWidth();
            int videoHeight = file.getVideoMediaMetadata().getHeight();
            if (videoWidth != Integer.parseInt(fileNameParsedArray[1].split("x")[0]) || videoHeight != Integer.parseInt(fileNameParsedArray[1].split("x")[1])) {
                String error = String.format("=HYPERLINK(\"https://drive.google.com/drive/u/1/folders/%s\";\"File %s has wrong size: %s. lastModifyingUser: %s\")", file.getParents().get(0), file.getName().toLowerCase(), file.getVideoMediaMetadata().getWidth() + "x" + file.getVideoMediaMetadata().getHeight(), file.getLastModifyingUser().getDisplayName());
                //System.out.println(error);
                videoErrors.add(error);
            }
            if (file.getName().contains("mp4.mp4")) {
                String error = String.format("=HYPERLINK(\"https://drive.google.com/drive/u/1/folders/%s\";\"File %s has wrong name. lastModifyingUser: %s\")", file.getParents().get(0), file.getName().toLowerCase(), file.getLastModifyingUser().getDisplayName());
                //System.out.println(error);
                videoErrors.add(error);
            }
            if (file.getSize() > 41943040 && parseIntSafely(fileNameParsedArray[2].replace("v", "")) > 600) {
                String error = String.format("=HYPERLINK(\"https://drive.google.com/drive/u/1/folders/%s\";\"File %s exceeds size of 40 MB. lastModifyingUser: %s\")", file.getParents().get(0), file.getName().toLowerCase(), file.getLastModifyingUser().getDisplayName());
                //System.out.println(error);
                videoErrors.add(error);
            }
        } catch (NullPointerException e) {
            String error = String.format("=HYPERLINK(\"https://drive.google.com/drive/u/1/folders/%s\";\"File %s is corrupted. lastModifyingUser: %s\")", file.getParents().get(0), file.getName().toLowerCase(), file.getLastModifyingUser().getDisplayName());
            //System.out.println(error);
            videoErrors.add(error);
        } catch (NumberFormatException e) {
            String error = String.format("=HYPERLINK(\"https://drive.google.com/drive/u/1/folders/%s\";\"File %s contains the Russian letter 'x'. lastModifyingUser: %s\")", file.getParents().get(0), file.getName().toLowerCase(), file.getLastModifyingUser().getDisplayName());
            //System.out.println(error);
            videoErrors.add(error);
        }

    }

    public static void getFolderLinksFromGoogleDrive(Drive service, InMemoryCreativeRepository repository, String creativeType, String directory) {
        String pageToken = null;
        while (true) {
            FileList result = GoogleDriveApiUtil.getFileListFromDriveAPI(service, pageToken, "'" + directory + "' in parents and mimeType='application/vnd.google-apps.folder' and trashed = false", "nextPageToken, files(name, webViewLink)");
            List<File> files = result.getFiles();
            if (files == null || files.isEmpty()) {
                System.out.println("No files found.");
            } else {
                for (File file : files) {
                    String creativeNumberString = file.getName().split(" ")[0].replace(creativeType, "");
                    if (repository.ifContainsCreative(parseIntSafely(creativeNumberString))) {
                        repository.updateFolderLink(parseIntSafely(creativeNumberString), file.getWebViewLink());
                    }
                }
            }
            pageToken = result.getNextPageToken();
            if (pageToken == null) break;
        }
    }

    public static void bannerAndLocaleRepositoryFilling(Drive service, String query) {
        String pageToken = null;
        while (true) {
            FileList result = GoogleDriveApiUtil.getFileListFromDriveAPI(service, pageToken, query, "nextPageToken, files(id, name, webViewLink, lastModifyingUser, createdTime, thumbnailLink)");
            List<File> files = result.getFiles();
            if (files == null || files.isEmpty()) {
                System.out.println("No files found.");
            } else {
                for (File file : files) {
                    String[] fileNameParsedArray = file.getName().toLowerCase().split("_");
                    String filename = file.getName().toLowerCase();
                    int bannerNumber = parseIntSafely(fileNameParsedArray[1].replace("b", ""));
                    if (filename.contains("_b") && (fileNameParsedArray.length == 4 || fileNameParsedArray.length == 5) && fileNameParsedArray[1].matches("b\\d+") && fileNameParsedArray[2].length() >= 2 && bannerNumber > 0) {
                        //System.out.println(file.getName().toLowerCase());
                        if (!GoogleDriveBannerSpider.bannerAndLocaleRepository.ifContainsCreativeAndLocale(bannerNumber + "_" + fileNameParsedArray[2]))
                            GoogleDriveBannerSpider.bannerAndLocaleRepository.add(bannerNumber, fileNameParsedArray[2]);
                        GoogleDriveBannerSpider.bannerAndLocaleRepository.update(bannerNumber + "_" + fileNameParsedArray[2], fileNameParsedArray[0], file.getThumbnailLink());
                    }
                }
                pageToken = result.getNextPageToken();
                if (pageToken == null) break;
            }
        }
    }

    public static void getFolderIdNameDictionaryFromGoogleDrive(Drive service) {
        String pageToken = null;
        while (true) {
            FileList result = GoogleDriveApiUtil.getFileListFromDriveAPI(service, pageToken, "mimeType='application/vnd.google-apps.folder' and trashed = false", "nextPageToken, files(id, name)");
            List<File> files = result.getFiles();
            if (files == null || files.isEmpty()) {
                System.out.println("No files found.");
            } else {
                for (File file : files) {
                    GoogleDriveSpider.folderDictionary.put(file.getId(), file.getName().toLowerCase());
                }
            }
            pageToken = result.getNextPageToken();
            if (pageToken == null) break;
        }
    }

    /* Лучше именовать методы как можно более абстрактно, назвать их по характеру их логики, которую они выполняют.
    Т.е. название метода в идеале должно говорить, что именно метод делает.
    В данном случае, можно сказать, что ответственность этого метода - вернуть текущее время,
    его логика никак не завязана на событие, что fixing was started.

    Проблема тут в том, что если тебе где-то еще потребуется получить текущее время
    логично использовать этот же мето, но имя метода "startTimeFixing" уже не подойдет,
    потому что ты для другой цели текущее время будешь получать уже
    (например, добавляешь в проект авто-тесты и хочешь зафиксировать начало их выполнения).

    А значит, тебе нужно будет по-хорошему переименовать этот метод во что-то общее по смыслу для двух использований.
    На этих двух примерах, может подойти getStartTime(). Тогда тебе, придется менять уже существующий код
    - переименовывать этот метод. Ну или забить на это и получить тех. долг и ухудшить качество кода.

    В целом без каких-либо изменений существующего кода, который переиспользуется, при расширении функционала,
    обычно, не приходится(особенно, если ты работаешь с кем-то, кто об это не думает). Да и в целом, что-то в любом случае меняется.
    Но возможно сократить этот фактор - заранее делая сущности как можно более абстрактными
     - меньше кода нужно будет править при внесении изменений, легче понять, что этот метод делает и собственно, понять, как его можно использовать.

    Особенно актуально на проектах с большой кодовой базой.
    Одно переименовании метода может отразиться на сотне файлов, и ревьюеру придется это просматривать, например.
    Ну или если параллельно этот же функционал кто-то другой изменял, будут конфликты VCSю
    (одновременно 2 разработчика изменили один и тот же код, один из них не сможет нормально смержить - будет конфликт изменений)

    В данном примере можно сразу назвать метод максимально обобщенно, на все случаи жизни, например что-то вроде:
    getNowInstant(), getNow(), ну или просто now().

    Тут, кстати, в выборе удачного имени можно исходить из таких моментов как:
    может ли появится в будущем еще какой-то метод в этом классе, с подобным именем, например:
     getNow***(могут ли появится в классе методы, например: getNowDate(), getNowLocalDate() и т.д.?)?
    Если да, то есть смысл назвать метод, например, getNowInstant() (в соответствии с возвращаемым типом данных, в данном примере).
    (тут пример, что по смыслу возвращается одно и то же - текущее время, но могут возвращаться разные типы -
    можно сразу описать в названии какое именно время возвращает, какой тип).

   В итоге приходим к такому методу:

    public Instant now() { return Instant.now(); }

   Выглядит избыточно. Делает тоже самое, что и вызываемый внутри Instant.now()
   Т.е. в данном случае, выходит, вместо вызова GeneralUtils.getTimeFixing(), лучше и проще вызывать сразу Instant.now() :

   Instant start = Instant.now();

   */
    public static Instant now() {
        return Instant.now();
    }

    public static String endTimeFixing(Instant start) {
        Instant end = Instant.now();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("HH:mm:ss");
        Duration diff = Duration.between(start, end);
        LocalTime fTime = LocalTime.ofNanoOfDay(diff.toNanos());
        return fTime.format(df);
    }
}
