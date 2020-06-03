import java.time.Instant;

import static util.GeneralUtil.*;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Instant start = startTimeFixing();
        initialization();
        System.out.println(endTimeFixing(start));
    }
}