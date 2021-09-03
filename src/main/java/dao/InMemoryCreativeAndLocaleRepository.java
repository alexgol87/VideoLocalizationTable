package dao;

import model.CreativeAndLocale;

import java.util.List;

// интерфейс для двух конечных классов
public interface InMemoryCreativeAndLocaleRepository {

    void add(Integer creativeNumber, String locale);

    void update(String keyCreativeAndLocale, String creativeSize, String newThumbnailLink);

    List<CreativeAndLocale> getAll();

    CreativeAndLocale getByCreativeAndLocale(String cretiveAndLocale);

    boolean ifContainsCreativeAndLocale(String cretiveAndLocale);
}