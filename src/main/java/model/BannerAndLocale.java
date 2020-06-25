package model;

public class BannerAndLocale implements CreativeAndLocale {

    private final Integer bannerNumber;
    private final boolean banner1600x900;
    private final boolean banner900x1600;
    private final boolean banner800x800;
    private final boolean etc;
    private final String locale;
    private final String thumbnailLink;

    public int getBannerNumber() {
        return bannerNumber;
    }

    public boolean isBanner1600x900() {
        return banner1600x900;
    }

    public boolean isBanner900x1600() {
        return banner900x1600;
    }

    public boolean isBanner800x800() {
        return banner800x800;
    }

    public boolean isEtc() {
        return etc;
    }

    public boolean isAll() {
        return (banner1600x900 && banner900x1600 && banner800x800 && etc);
    }

    public String getLocale() {
        return locale;
    }

    public String getThumbnailLink() {
        return thumbnailLink;
    }

    public BannerAndLocale(Integer bannerNumber, boolean banner1600x900, boolean banner900x1600, boolean banner800x800, boolean etc, String locale, String thumbnailLink) {
        super();
        this.bannerNumber = bannerNumber;
        this.banner1600x900 = banner1600x900;
        this.banner900x1600 = banner900x1600;
        this.banner800x800 = banner800x800;
        this.etc = etc;
        this.locale = locale;
        this.thumbnailLink = thumbnailLink;
    }

}
