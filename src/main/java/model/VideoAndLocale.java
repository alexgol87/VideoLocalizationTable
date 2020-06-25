package model;

public class VideoAndLocale implements CreativeAndLocale {

    private final Integer videoNumber;
    private final boolean video1920x1080;
    private final boolean video1080x1920;
    private final boolean video1080x1080;
    private final boolean video1080x1350;
    private final boolean video960x640;
    private final boolean video640x960;
    private final boolean video1024x768;
    private final boolean video768x1024;
    private final String locale;
    private final String thumbnailLink;

    public VideoAndLocale() {
        this.videoNumber = null;
        this.video1920x1080 = false;
        this.video1080x1920 = false;
        this.video1080x1080 = false;
        this.video1080x1350 = false;
        this.video960x640 = false;
        this.video640x960 = false;
        this.video1024x768 = false;
        this.video768x1024 = false;
        this.locale = null;
        this.thumbnailLink = null;
    }

    public Integer getVideoNumber() {
        return videoNumber;
    }

    public boolean isVideo1920x1080() {
        return video1920x1080;
    }

    public boolean isVideo1080x1920() {
        return video1080x1920;
    }

    public boolean isVideo1080x1080() {
        return video1080x1080;
    }

    public boolean isVideo1080x1350() {
        return video1080x1350;
    }

    public boolean isVideo960x640() {
        return video960x640;
    }

    public boolean isVideo640x960() {
        return video640x960;
    }

    public boolean isVideo1024x768() {
        return video1024x768;
    }

    public boolean isVideo768x1024() {
        return video768x1024;
    }

    public boolean isVideoDSP() {
        return (video960x640 && video640x960 && video1024x768 && video768x1024);
    }

    public boolean isVideoAll() {
        return (video1920x1080 && video1080x1920 && video1080x1080 && video1080x1350 && video960x640 && video640x960 && video1024x768 && video768x1024);
    }

    @Override
    public boolean isAll() {
        return false;
    }

    public String getLocale() {
        return locale;
    }

    public String getThumbnailLink() {
        return thumbnailLink;
    }

    public int getBannerNumber() {
        return 0;
    }

    public boolean isBanner1600x900() {
        return false;
    }

    public boolean isBanner900x1600() {
        return false;
    }

    public boolean isBanner800x800() {
        return false;
    }

    public boolean isEtc() {
        return false;
    }

    public VideoAndLocale(Integer videoNumber, boolean video1920x1080, boolean video1080x1920, boolean video1080x1080, boolean video1080x1350, boolean video960x640, boolean video640x960, boolean video1024x768, boolean video768x1024, String locale, String thumbnailLink) {
        this.videoNumber = videoNumber;
        this.video1920x1080 = video1920x1080;
        this.video1080x1920 = video1080x1920;
        this.video1080x1080 = video1080x1080;
        this.video1080x1350 = video1080x1350;
        this.video960x640 = video960x640;
        this.video640x960 = video640x960;
        this.video1024x768 = video1024x768;
        this.video768x1024 = video768x1024;
        this.locale = locale;
        this.thumbnailLink = thumbnailLink;
    }

}
