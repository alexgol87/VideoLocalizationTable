package dao;

import model.BannerAndLocale;
import model.CreativeAndLocale;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class InMemoryBannerAndLocaleRepository {

    static Map<String, CreativeAndLocale> bannerAndLocaleMap = new TreeMap<>();

    public void add(Integer bannerNumber, String locale) {
        bannerAndLocaleMap.put(bannerNumber + "_" + locale, new BannerAndLocale(bannerNumber, false, false, false, false, locale, null));
    }

    public void update(String keyBannerAndLocale, String bannerSize, String newThumbnailLink) {
        BannerAndLocale tmpBannerAndLocale = (BannerAndLocale) this.getByCreativeAndLocale(keyBannerAndLocale);
        int bannerNumber = tmpBannerAndLocale.getCreativeNumber();
        boolean banner1600x900 = tmpBannerAndLocale.getLandscapeData();
        boolean banner900x1600 = tmpBannerAndLocale.getPortraitData();
        boolean banner800x800 = tmpBannerAndLocale.getSquareData();
        boolean etc = tmpBannerAndLocale.getEtcData();
        String locale = tmpBannerAndLocale.getLocale();
        String thumbnailLink = tmpBannerAndLocale.getThumbnailLink();

        switch (bannerSize) {
            case "1600x900":
                banner1600x900 = true;
                if (locale.equals("en")) thumbnailLink = newThumbnailLink;
                break;
            case "900x1600":
                banner900x1600 = true;
                if (locale.equals("en") && thumbnailLink == null) thumbnailLink = newThumbnailLink;
                break;
            case "800x800":
                banner800x800 = true;
                if (locale.equals("en") && thumbnailLink == null) thumbnailLink = newThumbnailLink;
                break;
            default:
                etc = true;
                if (locale.equals("en") && thumbnailLink == null) thumbnailLink = newThumbnailLink;
                break;
        }
        bannerAndLocaleMap.put(keyBannerAndLocale, new BannerAndLocale(bannerNumber, banner1600x900, banner900x1600, banner800x800, etc, locale, thumbnailLink));
    }

    public List<CreativeAndLocale> getAll() {
        return bannerAndLocaleMap.values().stream().collect(Collectors.toList());
    }

    public CreativeAndLocale getByCreativeAndLocale(String bannerAndLocale) {
        return bannerAndLocaleMap.get(bannerAndLocale);
    }

    public boolean ifContainsCreativeAndLocale(String bannerAndLocale) {
        return bannerAndLocaleMap.containsKey(bannerAndLocale);
    }

    public void clear() {
        bannerAndLocaleMap.clear();
    }

}