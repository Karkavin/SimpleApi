package ru.touchit.catalog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.touchit.catalog.dao.CountryDao;
import ru.touchit.catalog.dao.DocDao;
import ru.touchit.catalog.model.Country;
import ru.touchit.catalog.model.Doc;
import ru.touchit.catalog.view.CountryView;
import ru.touchit.catalog.view.DocView;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@inheritDoc}
 * @autor Artyom Karkavin
 */
@Service("catalogService")
public class CatalogServiceImpl implements CatalogService {
    CountryDao countryDao;
    DocDao docDao;

    /**
     * Конструктор
     * @param countryDao Dao для работы со странами
     * @param docDao Dao для работы с документами
     */
    @Autowired
    public CatalogServiceImpl(CountryDao countryDao, DocDao docDao) {
        this.countryDao = countryDao;
        this.docDao = docDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<CountryView> getCountries() {
        List<Country> countries = countryDao.findAll();

        return countries.stream()
                .map(CountryView::new)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<DocView> getDocs() {
        List<Doc> docs = docDao.findAll();

        return docs.stream()
                .map(DocView::new)
                .collect(Collectors.toList());
    }
}