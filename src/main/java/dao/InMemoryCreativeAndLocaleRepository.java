package dao;

import model.CreativeAndLocale;

import java.util.List;

public interface InMemoryCreativeAndLocaleRepository {

    public void add(Integer creativeNumber, String locale);

    public void update(String keyCreativeAndLocale, String creativeSize, String newThumbnailLink);

    public List<CreativeAndLocale> getAll();

    public CreativeAndLocale getByCreativeAndLocale(String cretiveAndLocale);

    public boolean ifContainsCreativeAndLocale(String cretiveAndLocale);
}