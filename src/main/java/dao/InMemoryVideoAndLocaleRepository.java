package dao;

import model.VideoAndLocale;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class InMemoryVideoAndLocaleRepository {

    static Map<String, VideoAndLocale> videoAndLocaleMap = new TreeMap<>();

    public void add(Integer videoNumber, String locale) {
        videoAndLocaleMap.put(videoNumber + "_" + locale, new VideoAndLocale(videoNumber, false, false, false, false, false, false, false, false, locale, null));
    }

    public void update(String keyVideoAndLocale, String videoSize, String newThumbnailLink) {
        VideoAndLocale tmpVideoAndLocale = this.getByVideoAndLocale(keyVideoAndLocale);
        int videoNumber = tmpVideoAndLocale.getVideoNumber();
        boolean video1920x1080 = tmpVideoAndLocale.isVideo1920x1080();
        boolean video1080x1920 = tmpVideoAndLocale.isVideo1080x1920();
        boolean video1080x1080 = tmpVideoAndLocale.isVideo1080x1080();
        boolean video1080x1350 = tmpVideoAndLocale.isVideo1080x1350();
        boolean video960x640 = tmpVideoAndLocale.isVideo960x640();
        boolean video640x960 = tmpVideoAndLocale.isVideo640x960();
        boolean video1024x768 = tmpVideoAndLocale.isVideo1024x768();
        boolean video768x1024 = tmpVideoAndLocale.isVideo768x1024();
        String locale = tmpVideoAndLocale.getLocale();
        String thumbnailLink = tmpVideoAndLocale.getThumbnailLink();

        switch (videoSize) {
            case "1920x1080":
                video1920x1080 = true;
                if (locale.equals("en")) thumbnailLink = newThumbnailLink;
                if (!locale.equals("en") && thumbnailLink == null) thumbnailLink = newThumbnailLink;
                break;
            case "1080x1920":
                video1080x1920 = true;
                if (locale.equals("en") && thumbnailLink == null) thumbnailLink = newThumbnailLink;
                break;
            case "1080x1080":
                video1080x1080 = true;
                if (locale.equals("en") && thumbnailLink == null) thumbnailLink = newThumbnailLink;
                break;
            case "1080x1350":
                video1080x1350 = true;
                if (locale.equals("en") && thumbnailLink == null) thumbnailLink = newThumbnailLink;
                break;
            case "960x640":
                video960x640 = true;
                if (locale.equals("en")) thumbnailLink = newThumbnailLink;
                if (!locale.equals("en") && thumbnailLink == null) thumbnailLink = newThumbnailLink;
                break;
            case "640x960":
                video640x960 = true;
                if (locale.equals("en") && thumbnailLink == null) thumbnailLink = newThumbnailLink;
                break;
            case "1024x768":
                video1024x768 = true;
                if (locale.equals("en")) thumbnailLink = newThumbnailLink;
                if (!locale.equals("en") && thumbnailLink == null) thumbnailLink = newThumbnailLink;
                break;
            case "768x1024":
                video768x1024 = true;
                if (locale.equals("en") && thumbnailLink == null) thumbnailLink = newThumbnailLink;
                break;
            default:
                if (locale.equals("en") && thumbnailLink == null) thumbnailLink = newThumbnailLink;
                break;
        }
        videoAndLocaleMap.put(keyVideoAndLocale, new VideoAndLocale(videoNumber, video1920x1080, video1080x1920, video1080x1080, video1080x1350, video960x640, video640x960, video1024x768, video768x1024, locale, thumbnailLink));
    }

    public List<VideoAndLocale> getAll() {
        return videoAndLocaleMap.values().stream().collect(Collectors.toList());
    }

    public VideoAndLocale getByVideoAndLocale(String videoAndLocale) {
        return videoAndLocaleMap.get(videoAndLocale);
    }

    public boolean ifContainsVideoAndLocale(String videoAndLocale) {
        return videoAndLocaleMap.containsKey(videoAndLocale);
    }
}