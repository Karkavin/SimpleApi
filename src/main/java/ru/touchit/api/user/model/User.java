package ru.touchit.api.user.model;

import ru.touchit.api.catalog.model.Country;
import ru.touchit.api.catalog.model.Doc;
import ru.touchit.api.office.model.Office;
import ru.touchit.api.organisation.model.Organisation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Entity для сущности Сотрудник
 * @author Artyom Karkavin
 */
@Entity
@Table(name = "user", catalog = "public")
public class User {
    /** Поле: идентификатор */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Поле: имя */
    @Column(name = "first_name", nullable = false, length = 30)
    private String firstName;

    /** Поле: фамилия */
    @Column(name = "second_name", length = 30)
    private String secondName;

    /** Поле: отчество */
    @Column(name = "middle_name", length = 30)
    private String middleName;

    /** Поле: должность */
    @Column(name = "position", nullable = false, length = 50)
    private String position;

    /** Поле: телефон */
    @Column(name = "phone", length = 20)
    private String phone;

    /** Поле: номер документа */
    @Column(name = "doc_number", length = 30)
    private String docNumber;

    /** Поле: дата выдачи документа */
    @Column(name = "doc_date")
    @Temporal(TemporalType.DATE)
    private Date docDate;

    /** Поле: идентифицирован ли сотрудник */
    @Column(name = "is_identified")
    private boolean isIdentified = false;

    /** Поле: связь с сущностью Документ */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_code")
    private Doc doc;

    /** Поле: связь с сущностью Страна */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "citizenship_code")
    private Country country;

    /** Поле: связь с сущностью Организация */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private Organisation organisation;

    /** Поле: связь с сущностью Офис */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "off_id", nullable = false)
    private Office office;

    /**
     * Конструктор
     */
    public User() {

    }

    /**
     * Конструктор
     * @see Organisation
     * @see Office
     * @see Doc
     * @see Country
     */
    public User(Organisation organisation, Office office, Doc doc, Country country, String firstName, String secondName, String middleName, String position, String phone, String docNumber, Date docDate, boolean isIdentified) {
        this.organisation = organisation;
        this.office = office;
        this.doc = doc;
        this.country = country;
        this.firstName = firstName;
        this.secondName = secondName;
        this.middleName = middleName;
        this.position = position;
        this.phone = phone;
        this.docNumber = docNumber;
        this.docDate = docDate;
        this.isIdentified = isIdentified;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public Date getDocDate() {
        return docDate;
    }

    public void setDocDate(Date docDate) {
        this.docDate = docDate;
    }

    public boolean getIsIdentified() {
        return isIdentified;
    }

    public void setIsIdentified(boolean identified) {
        isIdentified = identified;
    }

    public Doc getDoc() {
        return doc;
    }

    public void setDoc(Doc doc) {
        this.doc = doc;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public Office getOffice() {
        return office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }
}