package me.koenn.kingdomwars.tracker.processing;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SheetsAPI {

    /**
     * Application name.
     */
    private static final String APPLICATION_NAME = "KingdomWars Analyser";

    private static final String SPREADSHEET_ID = "1wCPZ3RCXbQhc73R_NnI-0Zp80nBFD8TSlrqJIx7mlmk";

    /**
     * Directory to store user credentials for this application.
     */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/sheets.googleapis.com-java-quickstart-2");
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    /**
     * Global instance of the scopes required by this quickstart.
     * <p>
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/sheets.googleapis.com-java-quickstart
     */
    private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS, SheetsScopes.DRIVE);
    /**
     * Global instance of the {@link FileDataStoreFactory}.
     */
    private static FileDataStoreFactory DATA_STORE_FACTORY;
    /**
     * Global instance of the HTTP transport.
     */
    private static HttpTransport HTTP_TRANSPORT;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in = new FileInputStream("G:\\Minecraft Plugins\\KingdomWars\\src\\main\\resources\\client_secret.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Sheets API client service.
     *
     * @return an authorized Sheets API client service
     * @throws IOException
     */
    public static Sheets getSheetsService() throws IOException {
        Credential credential = authorize();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void main(String[] args) {
        Sheets sheets = connect();
        Random random = ThreadLocalRandom.current();
        for (int i = 0; i < 5; i++) {
            setKills(sheets, "Global Statistics", random.nextInt(20), random.nextInt(20), random.nextInt(20));
        }
    }

    public static Sheets connect() {
        try {
            return getSheetsService();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setKills(Sheets sheets, String sheet, int roaming, int defending, int attacking) {
        String range = String.format("%s!C%s:E", sheet, getNewRow(sheets, sheet));
        List<List<Object>> values = Arrays.asList(Arrays.asList(roaming, defending, attacking));
        try {
            sheets.spreadsheets().values().update(SPREADSHEET_ID, range, new ValueRange().setValues(values)).setValueInputOption("RAW").execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getNewRow(Sheets sheets, String sheet) {
        String range = String.format("%s!C3:E", sheet);
        ValueRange result = null;
        try {
            result = sheets.spreadsheets().values().get(SPREADSHEET_ID, range).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.getValues() != null ? result.getValues().size() + 3 : 3;
    }
}
