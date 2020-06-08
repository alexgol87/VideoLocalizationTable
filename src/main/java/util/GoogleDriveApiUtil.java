package util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.*;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static util.GoogleDriveSpider.videoRepository;

public class GoogleDriveApiUtil {
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String CREDENTIALS = System.getenv("googledrive_credentials");
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String RANGE_UPDATE_LASTUPDATETIME = "video main!Q1:R1";
    private static final String RANGE_UPDATE_LOCALIZATIONTABLE = "video main!A2:I";
    private static final String RANGE_GET_LASTUPDATETIME = "Q1:Q1";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */

    private static final List<String> SCOPES = Arrays.asList(
            SheetsScopes.SPREADSHEETS,
            DriveScopes.DRIVE_READONLY
    );

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        //InputStream in = GoogleDriveSpider.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        InputStream in = new ByteArrayInputStream(CREDENTIALS.getBytes());
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static Drive buildDriveApiClientService() {
        // Build a new authorized API client service.
        Drive service = null;
        try {
            final String APPLICATION_NAME = "Google Drive API Java";
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
        return service;
    }

    public static Sheets buildSheetsApiClientService() {
        // Build a new authorized API client service.
        Sheets service = null;
        try {
            final String APPLICATION_NAME = "Google Sheets API Java";
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
        return service;
    }

    public static FileList getFileListFromDriveAPI(Drive service, String pageToken, String query, String fields) {
        FileList fileList = null;
        try {
            fileList = service.files().list()
                    .setQ(query)
                    .setTeamDriveId("0AMS6TbWRrBLJUk9PVA")
                    .setCorpora("drive")
                    .setSupportsTeamDrives(true)
                    .setIncludeTeamDriveItems(true)
                    .setPageSize(1000)
                    .setPageToken(pageToken)
                    .setFields(fields)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileList;
    }

    public static void clearAndPublishNewTableOnSpreadsheet(Sheets service, String spreadsheetId, String valueInputOption) {
        try {
            // clear old values
            ClearValuesRequest requestBodyClear = new ClearValuesRequest();
            Sheets.Spreadsheets.Values.Clear request =
                    service.spreadsheets().values().clear(spreadsheetId, RANGE_UPDATE_LOCALIZATIONTABLE, requestBodyClear);
            request.execute();

            ValueRange requestBody = new ValueRange();
            requestBody.setRange(RANGE_UPDATE_LOCALIZATIONTABLE);
            List<List<Object>> localizationValues = new ArrayList<>();
            AtomicInteger lineIndex = new AtomicInteger();

            videoRepository.getAll()
                    .stream()
                    .forEach(v -> {

                        localizationValues.add(new ArrayList<>());
                        localizationValues.get(lineIndex.get()).add("=HYPERLINK(\"" + v.getFolderLink() + "\"; " + v.getVideoNumber() + ")");
                        localizationValues.get(lineIndex.get()).add(v.getEnData());
                        localizationValues.get(lineIndex.get()).add(v.getDeData());
                        localizationValues.get(lineIndex.get()).add(v.getFrData());
                        localizationValues.get(lineIndex.get()).add(v.getJaData());
                        localizationValues.get(lineIndex.get()).add(v.getMxData());
                        localizationValues.get(lineIndex.get()).add(v.getZhsData());
                        localizationValues.get(lineIndex.get()).add("=IMAGE(\"" + v.getThumbnailLink() + "\";1)");
                        localizationValues.get(lineIndex.get()).add("=HYPERLINK(\"" + v.getThumbnailLink() + "\"; \"Preview\")");
                        lineIndex.getAndIncrement();

                    });

            requestBody.setValues(localizationValues);

            service.spreadsheets().values().update(spreadsheetId, RANGE_UPDATE_LOCALIZATIONTABLE, requestBody)
                    .setValueInputOption(valueInputOption)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void publishModifiedTime(Sheets service, String spreadsheetId, String valueInputOption) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+3"));
            String dateString = simpleDateFormat.format(new Date());
            ValueRange modifiedTimeRequestBody = new ValueRange();
            modifiedTimeRequestBody.setRange(RANGE_UPDATE_LASTUPDATETIME);
            List<List<Object>> modifiedTimeValues = new ArrayList<>();
            modifiedTimeValues.add(new ArrayList<>());
            modifiedTimeValues.get(0).add(dateString);
            modifiedTimeValues.get(0).add("Last update time");
            modifiedTimeRequestBody.setValues(modifiedTimeValues);

            service.spreadsheets().values().update(spreadsheetId, RANGE_UPDATE_LASTUPDATETIME, modifiedTimeRequestBody)
                    .setValueInputOption(valueInputOption)
                    .execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getModifiedTime(Sheets service, String spreadsheetId) {
        ValueRange response = null;
        try {
            Sheets.Spreadsheets.Values.Get request = service.spreadsheets().values().get(spreadsheetId, RANGE_GET_LASTUPDATETIME);
             response = request.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (String) response.getValues().get(0).get(0);
    }
}