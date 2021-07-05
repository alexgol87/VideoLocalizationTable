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
    private String ruData;
    private String thumbnailLink;
    private String folderLink;
    private String fileName;

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

    public String getRuData() {
        return ruData;
    }

    public String getThumbnailLink() {
        return thumbnailLink;
    }

    public String getFolderLink() {
        return folderLink;
    }

    public String getFileName() {
        return fileName;
    }

    public void setThumbnailLink(String thumbnailLink) {
        this.thumbnailLink = thumbnailLink;
    }

    public void setFolderLink(String folderLink) {
        this.folderLink = folderLink;
    }

    public Creative(Integer creativeNumber, String enData, String deData, String frData, String jaData, String mxData, String zhsData, String koData, String brData, String ruData, String thumbnailLink, String folderLink, String fileName) {
        this.creativeNumber = creativeNumber;
        this.enData = enData;
        this.deData = deData;
        this.frData = frData;
        this.jaData = jaData;
        this.mxData = mxData;
        this.zhsData = zhsData;
        this.koData = koData;
        this.brData = brData;
        this.ruData = ruData;
        this.thumbnailLink = thumbnailLink;
        this.folderLink = folderLink;
        this.fileName = fileName;
    }

}
