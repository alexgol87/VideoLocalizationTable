package model;

public class Creative {
    private Integer creativeNumber;
    private String enData;
    private String deData;
    private String frData;
    private String jaData;
    private String mxData;
    private String zhsData;
    private String koData;
    private String brData;
    private String thumbnailLink;
    private String folderLink;

    public Integer getCreativeNumber() {
        return creativeNumber;
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

    public String getKoData() {
        return koData;
    }

    public String getBrData() {
        return brData;
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

    public Creative(Integer creativeNumber, String enData, String deData, String frData, String jaData, String mxData, String zhsData, String koData, String brData, String thumbnailLink, String folderLink) {
        this.creativeNumber = creativeNumber;
        this.enData = enData;
        this.deData = deData;
        this.frData = frData;
        this.jaData = jaData;
        this.mxData = mxData;
        this.zhsData = zhsData;
        this.koData = koData;
        this.brData = brData;
        this.thumbnailLink = thumbnailLink;
        this.folderLink = folderLink;
    }

    public String getFolderLink() {
        return folderLink;
    }
}
