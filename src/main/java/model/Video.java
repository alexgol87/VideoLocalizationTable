package model;

public class Video {
    private Integer videoNumber;
    private String enData;
    private String deData;
    private String frData;
    private String jaData;
    private String mxData;
    private String zhsData;
    private String thumbnailLink;
    private String folderLink;

    public Integer getVideoNumber() {
        return videoNumber;
    }

    public String getEnData() {
        return enData;
    }

    public String getDeData() {
        return deData;
    }

    public String getFrData() {
        return frData;
    }

    public String getJaData() {
        return jaData;
    }

    public String getMxData() {
        return mxData;
    }

    public String getZhsData() {
        return zhsData;
    }

    public String getThumbnailLink() {
        return thumbnailLink;
    }

    public void setThumbnailLink(String thumbnailLink) {
        this.thumbnailLink = thumbnailLink;
    }

    public void setFolderLink(String folderLink) {
        this.folderLink = folderLink;
    }

    public Video(Integer videoNumber, String enData, String deData, String frData, String jaData, String mxData, String zhsData, String thumbnailLink, String folderLink) {
        this.videoNumber = videoNumber;
        this.enData = enData;
        this.deData = deData;
        this.frData = frData;
        this.jaData = jaData;
        this.mxData = mxData;
        this.zhsData = zhsData;
        this.thumbnailLink = thumbnailLink;
        this.folderLink = folderLink;
    }

    public String getFolderLink() {
        return folderLink;
    }
}
