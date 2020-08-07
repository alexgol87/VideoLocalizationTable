package dao;

import model.Creative;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class InMemoryCreativeRepository {

    static Map<Integer, Creative> creativeTreeMap = new TreeMap<>();

    public void add(Integer creativeNumber) {
        creativeTreeMap.put(creativeNumber, new Creative(creativeNumber, "", "", "", "", "", "", "", ""));
    }

    public void update(Integer creativeNumber, String locale, boolean isAllData, boolean isSquareData, boolean isLandscapeData, boolean isPortraitData, boolean isDspData, boolean isFbfData, boolean isEtcData, String thumbnailLink) {
        Creative tmpCreative = this.getByCreativeNumber(creativeNumber);
        String enData = tmpCreative.getEnData();
        String deData = tmpCreative.getDeData();
        String frData = tmpCreative.getFrData();
        String jaData = tmpCreative.getJaData();
        String mxData = tmpCreative.getMxData();
        String zhsData = tmpCreative.getZhsData();
        if (thumbnailLink == null) thumbnailLink = tmpCreative.getThumbnailLink();
        String folderLink = tmpCreative.getFolderLink();
        final String ALL = "ALL";
        final String VER = "Ver ";
        final String HOR = "Hor ";
        final String SQR = "Sqr ";
        final String FBF = "FBF ";
        final String DSP = "DSP";
        final String ETC = "etc. ";

        switch (locale) {
            case "en":
                if (isAllData) enData = ALL;
                else {
                    if (isLandscapeData) enData += HOR;
                    if (isPortraitData) enData += VER;
                    if (isSquareData) enData += SQR;
                    if (isFbfData) enData += FBF;
                    if (isDspData) enData += DSP;
                    if (isEtcData) enData += ETC;
                }
                break;
            case "de":
                if (isAllData) deData = ALL;
                else {
                    if (isLandscapeData) deData += HOR;
                    if (isPortraitData) deData += VER;
                    if (isSquareData) deData += SQR;
                    if (isFbfData) deData += FBF;
                    if (isDspData) deData += DSP;
                    if (isEtcData) deData += ETC;
                }
                break;
            case "fr":
                if (isAllData) frData = ALL;
                else {
                    if (isLandscapeData) frData += HOR;
                    if (isPortraitData) frData += VER;
                    if (isSquareData) frData += SQR;
                    if (isFbfData) frData += FBF;
                    if (isDspData) frData += DSP;
                    if (isEtcData) frData += ETC;
                }
                break;
            case "ja":
                if (isAllData) jaData = ALL;
                else {
                    if (isLandscapeData) jaData += HOR;
                    if (isPortraitData) jaData += VER;
                    if (isSquareData) jaData += SQR;
                    if (isFbfData) jaData += FBF;
                    if (isDspData) jaData += DSP;
                    if (isEtcData) jaData += ETC;
                }
                break;
            case "mx":
                if (isAllData) mxData = ALL;
                else {
                    if (isLandscapeData) mxData += HOR;
                    if (isPortraitData) mxData += VER;
                    if (isSquareData) mxData += SQR;
                    if (isFbfData) mxData += FBF;
                    if (isDspData) mxData += DSP;
                    if (isEtcData) mxData += ETC;
                }
                break;
            case "zhs":
                if (isAllData) zhsData = ALL;
                else {
                    if (isLandscapeData) zhsData += HOR;
                    if (isPortraitData) zhsData += VER;
                    if (isSquareData) zhsData += SQR;
                    if (isFbfData) zhsData += FBF;
                    if (isDspData) zhsData += DSP;
                    if (isEtcData) zhsData += ETC;
                }
                break;
            default:
                break;
        }
        creativeTreeMap.put(creativeNumber, new Creative(creativeNumber, enData, deData, frData, jaData, mxData, zhsData, thumbnailLink, folderLink));
    }

    public void updateThumbnailLinkToDropboxLink(Integer creativeNumber, String dropboxLink) {
        String dropboxRawLink = dropboxLink.replace("dl=0", "raw=1");
        Creative tmpCreative = this.getByCreativeNumber(creativeNumber);
        tmpCreative.setThumbnailLink(dropboxRawLink);
        creativeTreeMap.put(creativeNumber, tmpCreative);
    }

    public void updateFolderLink(Integer creativeNumber, String folderLink) {
        Creative tmpCreative = this.getByCreativeNumber(creativeNumber);
        tmpCreative.setFolderLink(folderLink);
        creativeTreeMap.put(creativeNumber, tmpCreative);
    }

    public Creative getByCreativeNumber(Integer creativeNumber) {
        return creativeTreeMap.get(creativeNumber);
    }

    public boolean ifContainsCreative(Integer creativeNumber) {
        return creativeTreeMap.containsKey(creativeNumber);
    }

    public List<Creative> getAll() {
        return creativeTreeMap.values().stream().collect(Collectors.toList());
    }

    public void clear() {
        creativeTreeMap.clear();
    }
}

