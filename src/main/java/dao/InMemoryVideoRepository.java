package dao;

import model.Video;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class InMemoryVideoRepository {

    static Map<Integer, Video> videoMap = new TreeMap<>();

    public void add(Integer videoNumber) {
        videoMap.put(videoNumber, new Video(videoNumber, "", "", "", "", "", "", "", ""));
    }

    public void update(Integer videoNumber, String locale, boolean isAllData, boolean isDspData, boolean isFacebookData, boolean isNetworkData, boolean isSquareData, boolean isFbfData, String thumbnailLink) {
        Video tmpVideo = this.getByVideo(videoNumber);
        String enData = tmpVideo.getEnData();
        String deData = tmpVideo.getDeData();
        String frData = tmpVideo.getFrData();
        String jaData = tmpVideo.getJaData();
        String mxData = tmpVideo.getMxData();
        String zhsData = tmpVideo.getZhsData();
        if (thumbnailLink == null) thumbnailLink = tmpVideo.getThumbnailLink();
        String folderLink = tmpVideo.getFolderLink();
        final String ALL = "ALL";
        final String NT = "NT ";
        final String SQR = "Sqr ";
        final String FBF = "FBF ";
        final String FB = "FB ";
        final String DSP = "DSP";

        switch (locale) {
            case "en":
                if (isAllData) enData = ALL;
                else {
                    if (isNetworkData) enData += NT;
                    if (isSquareData) enData += SQR;
                    if (isFbfData) enData += FBF;
                    if (isFacebookData) enData += FB;
                    if (isDspData) enData += DSP;
                }
                break;
            case "de":
                if (isAllData) deData = ALL;
                else {
                    if (isNetworkData) deData += NT;
                    if (isSquareData) deData += SQR;
                    if (isFbfData) deData += FBF;
                    if (isFacebookData) deData += FB;
                    if (isDspData) deData += DSP;
                }
                break;
            case "fr":
                if (isAllData) frData = ALL;
                else {
                    if (isNetworkData) frData += NT;
                    if (isSquareData) frData += SQR;
                    if (isFbfData) frData += FBF;
                    if (isFacebookData) frData += FB;
                    if (isDspData) frData += DSP;
                }
                break;
            case "ja":
                if (isAllData) jaData = ALL;
                else {
                    if (isNetworkData) jaData += NT;
                    if (isSquareData) jaData += SQR;
                    if (isFbfData) jaData += FBF;
                    if (isFacebookData) jaData += FB;
                    if (isDspData) jaData += DSP;
                }
                break;
            case "mx":
                if (isAllData) mxData = ALL;
                else {
                    if (isNetworkData) mxData += NT;
                    if (isSquareData) mxData += SQR;
                    if (isFbfData) mxData += FBF;
                    if (isFacebookData) mxData += FB;
                    if (isDspData) mxData += DSP;
                }
                break;
            case "zhs":
                if (isAllData) zhsData = ALL;
                else {
                    if (isNetworkData) zhsData += NT;
                    if (isSquareData) zhsData += SQR;
                    if (isFbfData) zhsData += FBF;
                    if (isFacebookData) zhsData += FB;
                    if (isDspData) zhsData += DSP;
                }
                break;
            default:
                break;
        }
        videoMap.put(videoNumber, new Video(videoNumber, enData, deData, frData, jaData, mxData, zhsData, thumbnailLink, folderLink));
    }

    public void updateThumbnailLinkToDropboxLink(Integer videoNumber, String dropboxLink) {
        String dropboxRawLink = dropboxLink.replace("dl=0", "raw=1");
        Video tmpVideo = this.getByVideo(videoNumber);
        tmpVideo.setThumbnailLink(dropboxRawLink);
        videoMap.put(videoNumber, tmpVideo);
    }

    public void updateFolderLink(Integer videoNumber, String folderLink) {
        Video tmpVideo = this.getByVideo(videoNumber);
        tmpVideo.setFolderLink(folderLink);
        videoMap.put(videoNumber, tmpVideo);
    }

    public Video getByVideo(Integer videoNumber) {
        return videoMap.get(videoNumber);
    }

    public boolean ifContainsVideo(Integer videoNumber) {
        return videoMap.containsKey(videoNumber);
    }

    public List<Video> getAll() {
        return videoMap.values().stream().collect(Collectors.toList());
    }
}

