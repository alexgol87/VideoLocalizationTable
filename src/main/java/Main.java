import util.GoogleDriveApiUtil;
import util.GoogleDriveSpider;

import java.time.Instant;

import static util.GeneralUtil.endTimeFixing;
import static util.GeneralUtil.now;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        /*
        Сложно читать код, когда он не помещается в ширину монитора.

        Особенно актуально при использовании вертикальных мониторов и/или при просмотре Pull Request'ов, поэтому в
        идеале лучше соблюдать лимит строки в 120 символов.
         */
        String lastUpdateTime = GoogleDriveApiUtil.getModifiedTime(
                GoogleDriveApiUtil.buildSheetsApiClientService(),
                "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI",
                "video COEm!Q1:Q1"
        );

        System.out.println("Last update time: " + lastUpdateTime);
// Блоки кода, на мой взгляд, лучше разделять переносами друг от друга - так лучше воспринимается при чтении.

// На всякий случай: опасаться в итоге больших методов или классов не стоит.
// Если такое происходит, почти всегда возможно разнести логику по разным методам и сделать этим код еще очевиднее.
// Не стоит экономить место на экране/в классе/методе.
// Лучше, чтобы код был как можно более очевидным. Ведь, как известно, промышленный код намного чаще читают, чем пишут.
// В том плане, что большие методы и классы сложно понимать.
// (Судя по коду, для тебя это и так очевидно, перегруженных методов/классов пока не увидел)
        Instant start = now();

        //Runnable task = GoogleDriveBannerSpider::new;
        Runnable task = GoogleDriveSpider::new;

        Thread thread = new Thread(task);
        thread.start();
        thread.join();

        System.out.println(endTimeFixing(start));
    }
}