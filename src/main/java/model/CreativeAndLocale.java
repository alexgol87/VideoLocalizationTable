package model;

// интерфейс для двух промежуточных классов
public interface CreativeAndLocale {

    boolean getAllData();

    String getLocale();

    String getThumbnailLink();
    
    int getCreativeNumber();

    boolean getDspData();

    boolean getFbfData();

    boolean getSquareData();

    boolean getLandscapeData();

    boolean getPortraitData();

    boolean getEtcData();

    String getFileName();
}
