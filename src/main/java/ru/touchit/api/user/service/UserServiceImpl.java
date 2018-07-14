package ru.touchit.api.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.touchit.api.catalog.dao.CountryDao;
import ru.touchit.api.catalog.dao.DocDao;
import ru.touchit.api.catalog.exception.NoSuchCountryException;
import ru.touchit.api.catalog.exception.NoSuchDocException;
import ru.touchit.api.catalog.model.Country;
import ru.touchit.api.catalog.model.Doc;
import ru.touchit.api.office.dao.OfficeDao;
import ru.touchit.api.office.exception.NoSuchOfficeException;
import ru.touchit.api.office.model.Office;
import ru.touchit.api.organisation.dao.OrganisationDao;
import ru.touchit.api.organisation.exception.NoSuchOrganisationException;
import ru.touchit.api.organisation.model.Organisation;
import ru.touchit.api.user.dao.UserDao;
import ru.touchit.api.user.exception.IncorrectDateException;
import ru.touchit.api.user.exception.NoSuchUserException;
import ru.touchit.api.office.exception.OfficeDoesNotInOrganisationException;
import ru.touchit.api.user.model.User;
import ru.touchit.api.user.view.BaseUserView;
import ru.touchit.api.user.view.FilterResultUserView;
import ru.touchit.api.user.view.FilterUserView;
import ru.touchit.api.user.view.FullUserView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * {@inheritDoc}
 * @author Artyom Karkavin
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    UserDao userDao;
    OrganisationDao organisationDao;
    OfficeDao officeDao;
    DocDao docDao;
    CountryDao countryDao;

    /**
     * Конструктор
     * @param userDao Dao для работы с сотрудниками
     * @param organisationDao Dao для работы с организациями
     * @param officeDao Dao для работы с офисами
     * @param docDao Dao для работы с документами
     * @param countryDao Dao для работы со странами
     */
    @Autowired
    public UserServiceImpl(UserDao userDao, OrganisationDao organisationDao,
                           OfficeDao officeDao, DocDao docDao, CountryDao countryDao){
        this.userDao = userDao;
        this.organisationDao = organisationDao;
        this.officeDao = officeDao;
        this.docDao = docDao;
        this.countryDao = countryDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public FullUserView getById(Long id) throws NoSuchUserException {
        Optional<User> optional = userDao.findById(id);

        if (!optional.isPresent()) {
            throw new NoSuchUserException("No such user with id " + id);
        } else {
            return new FullUserView(optional.get());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void add(BaseUserView userView) throws NoSuchOrganisationException, NoSuchOfficeException,
            IncorrectDateException, NoSuchDocException, NoSuchCountryException,
            OfficeDoesNotInOrganisationException {
        customAddUpdate(userView, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void update(FullUserView userView) throws NoSuchUserException, NoSuchOrganisationException,
            NoSuchOfficeException, IncorrectDateException, NoSuchDocException, NoSuchCountryException,
            OfficeDoesNotInOrganisationException {
        Optional<User> optional = userDao.findById(userView.getId());

        if (!optional.isPresent()) {
            throw new NoSuchUserException("No such user with id " + userView.getId());
        } else {
            customAddUpdate(userView, false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<FilterResultUserView> filter(FilterUserView userView)
            throws NoSuchOfficeException, NoSuchDocException, NoSuchCountryException {
        Optional<Office> optionalOffice = officeDao.findById(userView.getOffId());
        Doc doc = null;
        if (userView.getDocCode() != null) {
            Optional<Doc> optionalDoc = docDao.findById(userView.getDocCode());
            if (optionalDoc.isPresent()) {
                doc = optionalDoc.get();
            } else {
                throw new NoSuchDocException("No such doc with id " + userView.getDocCode());
            }
        }
        Country country = null;
        if (userView.getCitizenshipCode() != null) {
            Optional<Country> optionalCountry = countryDao.findById(userView.getCitizenshipCode());
            if (optionalCountry.isPresent()) {
                country = optionalCountry.get();
            } else {
                throw new NoSuchCountryException("No such country with id " + userView.getCitizenshipCode());
            }
        }

        if (!optionalOffice.isPresent()) {
            throw new NoSuchOfficeException("No such office with id " + userView.getOffId());
        } else {
            String firstName = "", secondName = "", middleName = "", position = "";
            if (userView.getFirstName() != null){
                firstName = userView.getFirstName();
            }
            if (userView.getSecondName() != null){
                secondName = userView.getSecondName();
            }
            if (userView.getMiddleName() != null){
                middleName = userView.getMiddleName();
            }
            if (userView.getPosition() != null){
                position = userView.getPosition();
            }
            return userDao.findByOfficeAndNameAndPositionAndDocAndCountry(optionalOffice.get(), firstName,
                    secondName, middleName, position, doc, country)
                    .stream()
                    .map(mapUser())
                    .collect(Collectors.toList());
        }
    }

    private void customAddUpdate(BaseUserView userView, boolean isNew) throws NoSuchOrganisationException,
            NoSuchOfficeException, IncorrectDateException, NoSuchDocException, NoSuchCountryException,
            OfficeDoesNotInOrganisationException {
        Optional<Organisation> optionalOrganisation = organisationDao.findById(userView.getOrgId());
        Optional<Office> optionalOffice = officeDao.findById(userView.getOffId());

        if (!optionalOrganisation.isPresent()) {
            throw new NoSuchOrganisationException("No such organisation with id " + userView.getOrgId());
        } else if (!optionalOffice.isPresent()) {
            throw new NoSuchOfficeException("No such office with id " + userView.getOffId());
        } else {
            if (!optionalOffice.get().getOrganisation().getId().equals(optionalOrganisation.get().getId())) {
                throw new OfficeDoesNotInOrganisationException("No office with id = " + optionalOffice.get().getId() +
                        " in organisation with id = " + optionalOrganisation.get().getId());
            }
            DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
            Date date;
            try {
                formatter.setLenient(false);
                date = formatter.parse(userView.getDocDate());
            } catch (ParseException e) {
                throw new IncorrectDateException("Incorrect date: " + userView.getDocDate() +
                        ". Date should be formatted to : MM-dd-yyyy");
            }

            Optional<Doc> optionalDoc = null;
            Optional<Country> optionalCountry = null;
            if (userView.getDocCode() != null) {
                optionalDoc = docDao.findById(userView.getDocCode());
                if (!optionalDoc.isPresent()) {
                    throw new NoSuchDocException("No such doc with id " + userView.getDocCode());
                }
            }
            if (userView.getCitizenshipCode() != null) {
                optionalCountry = countryDao.findById(userView.getCitizenshipCode());
                if (!optionalCountry.isPresent()) {
                    throw new NoSuchCountryException("No such country with id " + userView.getCitizenshipCode());
                }
            }

            if (isNew) {
                userDao.save(new User(
                        optionalOrganisation.get(),
                        optionalOffice.get(),
                        optionalDoc != null ? optionalDoc.get() : null,
                        optionalCountry != null ? optionalCountry.get() : null,
                        userView.getFirstName(),
                        userView.getSecondName(),
                        userView.getMiddleName(),
                        userView.getPosition(),
                        userView.getPhone(),
                        userView.getDocNumber(),
                        date,
                        userView.getIsIdentified()
                ));
            } else {
                FullUserView fullUserView = (FullUserView) userView;
                User userFromDb = userDao.findById(fullUserView.getId()).get();
                userFromDb.setOrganisation(optionalOrganisation.get());
                userFromDb.setOffice(optionalOffice.get());
                userFromDb.setDoc(optionalDoc != null ? optionalDoc.get() : null);
                userFromDb.setCountry(optionalCountry != null ? optionalCountry.get() : null);
                userFromDb.setFirstName(userView.getFirstName());
                userFromDb.setSecondName(userView.getSecondName());
                userFromDb.setMiddleName(userView.getMiddleName());
                userFromDb.setPosition(userView.getPosition());
                userFromDb.setPhone(userView.getPhone());
                userFromDb.setDocNumber(userView.getDocNumber());
                userFromDb.setDocDate(date);
                userFromDb.setIsIdentified(userView.getIsIdentified());
                userDao.save(userFromDb);
            }
        }
    }

    private Function<User, FilterResultUserView> mapUser() {
        return u -> {
            FilterResultUserView view = new FilterResultUserView();
            view.setId(u.getId());
            view.setFirstName(u.getFirstName());
            view.setSecondName(u.getSecondName());
            view.setMiddleName(u.getMiddleName());
            view.setPosition(u.getPosition());

            return view;
        };
    }
}