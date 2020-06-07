import util.GoogleDriveSpider;

import java.time.Instant;

import static util.GeneralUtil.*;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Instant start = startTimeFixing();
        GoogleDriveSpider.getInstance();
        System.out.println(endTimeFixing(start));
    }
}