import util.GoogleDriveApiUtil;
import util.GoogleDriveBannerSpider;
import util.GoogleDriveCommunitySpider;
import util.GoogleDriveSpider;

import java.time.Instant;

import static util.GeneralUtil.*;
import static util.GoogleDriveSpider.videoErrorsCE;

// класс для тестирования работы скрипта без веба, основной класс, из которого запускается программа в Java, если не задана другая конфигурация
public class Main {

    public static void main(String[] args) throws InterruptedException {
        // забираем время последнего обновления COEM Videos и выводим его
        String lastUpdateTime = GoogleDriveApiUtil.getModifiedTime(GoogleDriveApiUtil.buildSheetsApiClientService(), "1SC92tKYXQDqujUcvZVYMmmNiJp35Q1b22fKg2C7zeQI", "video COEm!R1:R1");
        System.out.println("Last update time: " + lastUpdateTime);
        // начинаем фиксировать время старта работы скрипта
        Instant start = startTimeFixing();
        // запускаем задание на парсинг videos / banners / community banners, раскомментируем нужную строку.
      //  Runnable task = GoogleDriveCommunitySpider::new;
        Runnable task = GoogleDriveSpider::new;
      //  Runnable task = GoogleDriveBannerSpider::new;
        // стартуем поток
        Thread thread = new Thread(task);
        thread.start();
        // ожидаем выполнение потока
        thread.join();
        // выводим время работы скрипта
        System.out.println(endTimeFixing(start));
        // выводим ошибки парсинга COEM videos
        for (String s : videoErrorsCE) {
            System.out.println(s);
        }
    }
}