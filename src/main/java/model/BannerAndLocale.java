package model;

public class BannerAndLocale implements CreativeAndLocale {

    private final Integer bannerNumber;
    private final boolean banner1600x900;
    private final boolean banner900x1600;
    private final boolean banner800x800;
    private final boolean etc;
    private final String locale;
    private final String thumbnailLink;
    private final String fileName;

    public int getCreativeNumber() {
        return bannerNumber;
    }

    @Override
    public boolean getDspData() {
        return false;
    }

    @Override
    public boolean getFbfData() {
        return false;
    }

    @Override
    public boolean getSquareData() {
        return banner800x800;
    }

    @Override
    public boolean getLandscapeData() {
        return banner1600x900;
    }

    @Override
    public boolean getPortraitData() {
        return banner900x1600;
    }

    public boolean getEtcData() {
        return etc;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    public boolean getAllData() {
        return (banner1600x900 && banner900x1600 && banner800x800 && etc);
    }

    public String getLocale() {
        return locale;
    }

    public String getThumbnailLink() {
        return thumbnailLink;
    }

    public BannerAndLocale(Integer bannerNumber, boolean banner1600x900, boolean banner900x1600, boolean banner800x800, boolean etc, String locale, String thumbnailLink, String fileName) {
        super();
        this.bannerNumber = bannerNumber;
        this.banner1600x900 = banner1600x900;
        this.banner900x1600 = banner900x1600;
        this.banner800x800 = banner800x800;
        this.etc = etc;
        this.locale = locale;
        this.thumbnailLink = thumbnailLink;
        this.fileName = fileName;
    }

}
