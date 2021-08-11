import util.GoogleDriveApiUtil;
import util.GoogleDriveBannerSpider;
import util.GoogleDriveCommunitySpider;
import util.GoogleDriveSpider;

import java.time.Instant;
import java.util.Arrays;

import static util.GeneralUtil.*;
import static util.GoogleDriveSpider.videoErrorsCE;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        String lastUpdateTime = GoogleDriveApiUtil.getModifiedTime(GoogleDriveApiUtil.buildSheetsApiClientService(), "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "video COEm!R1:R1");
        System.out.println("Last update time: " + lastUpdateTime);
        Instant start = startTimeFixing();
        Runnable task = GoogleDriveCommunitySpider::new;
      //  Runnable task = GoogleDriveSpider::new;
      //  Runnable task = GoogleDriveBannerSpider::new;
        Thread thread = new Thread(task);
        thread.start();
        thread.join();
        System.out.println(endTimeFixing(start));
        for (String s : videoErrorsCE) {
            System.out.println(s);
        }
    }
}