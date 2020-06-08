import util.GoogleDriveApiUtil;
import util.GoogleDriveSpider;

import java.time.Instant;

import static util.GeneralUtil.*;

public class Main {

    public static void main(String[] args) {
        Instant start = startTimeFixing();
        String lastUpdateTime = GoogleDriveApiUtil.getModifiedTime(GoogleDriveApiUtil.buildSheetsApiClientService(), "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI");
        System.out.println("Last update time: " + lastUpdateTime);
        GoogleDriveSpider.getInstance();
        System.out.println(endTimeFixing(start));
    }
}