import util.GoogleDriveApiUtil;
import util.GoogleDriveBannerSpider;
import util.GoogleDriveSpider;

import java.time.Instant;

import static util.GeneralUtil.*;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        String lastUpdateTime = GoogleDriveApiUtil.getModifiedTime(GoogleDriveApiUtil.buildSheetsApiClientService(), "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI");
        System.out.println("Last update time: " + lastUpdateTime);
        Instant start = startTimeFixing();
        Runnable task = GoogleDriveBannerSpider::new;
        //Runnable task = GoogleDriveSpider::new;
        Thread thread = new Thread(task);
        thread.start();
        thread.join();
        System.out.println(endTimeFixing(start));
    }
}