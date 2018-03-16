package com.edubreeze.model;

import com.edubreeze.config.AppConfiguration;
import com.edubreeze.database.DatabaseHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class Student {

    private static final String UPDATED_AT_COLUMNN_NAME = "updatedAt";

    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
    private UUID autoId;

    @DatabaseField(canBeNull = false)
    private String admissionNumber;

    @DatabaseField(canBeNull = false)
    private String firstName;

    @DatabaseField(canBeNull = false)
    private String lastName;

    @DatabaseField(canBeNull = false)
    private String gender;

    // due to Java restriction, i can not name a variable "class"
    @DatabaseField(canBeNull = false)
    private String currentClass;

    @DatabaseField(canBeNull = false)
    private String classCategory;

    @DatabaseField
    private String classSection;

    @DatabaseField(canBeNull = false)
    private String classSectionType;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private School school;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Lga lga;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private State state;

    @DatabaseField
    private Date dateOfBirth;

    @DatabaseField
    private String dateEnrolled;

    @DatabaseField
    private String contactPersonName;

    @DatabaseField
    private String contactPersonPhoneNumber;

    @DatabaseField
    private String contactPersonAddress;

    @DatabaseField
    private String religion;

    @DatabaseField
    private String previousSchool;

    @DatabaseField
    private String classPassedAtPreviousSchool;

    @DatabaseField
    private String incomingTransferCertNo;

    @DatabaseField
    private String outgoingTransferCertNo;

    @DatabaseField
    private String dateOfLeaving;

    @DatabaseField
    private String causeOfLeaving;

    @DatabaseField
    private String occupationAfterLeaving;

    @DatabaseField(canBeNull = false)
    private String createdBy;

    @DatabaseField(canBeNull = false)
    private String updatedBy;

    @DatabaseField(canBeNull = false)
    private Date createdAt;

    @DatabaseField(canBeNull = false)
    private Date updatedAt;

    @DatabaseField
    private Date lastSyncedAt;

    public Student() {
        // ORMLite needs a no-arg constructor
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCurrentClass() {
        return currentClass;
    }

    public void setCurrentClass(String currentClass) {
        this.currentClass = currentClass;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public Lga getLga() {
        return lga;
    }

    public void setLga(Lga lga) {
        this.lga = lga;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDateEnrolled() {
        return dateEnrolled;
    }

    public void setDateEnrolled(String dateEnrolled) {
        this.dateEnrolled = dateEnrolled;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getContactPersonPhoneNumber() {
        return contactPersonPhoneNumber;
    }

    public void setContactPersonPhoneNumber(String contactPersonPhoneNumber) {
        this.contactPersonPhoneNumber = contactPersonPhoneNumber;
    }

    public String getContactPersonAddress() {
        return contactPersonAddress;
    }

    public void setContactPersonAddress(String contactPersonAddress) {
        this.contactPersonAddress = contactPersonAddress;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public UUID getAutoId() {
        return autoId;
    }

    public String getAdmissionNumber() {
        return admissionNumber;
    }

    public void setAdmissionNumber(String admissionNumber) {
        this.admissionNumber = admissionNumber;
    }

    public String getClassCategory() {
        return classCategory;
    }

    public void setClassCategory(String classCategory) {
        this.classCategory = classCategory;
    }

    public String getClassSection() {
        return classSection;
    }

    public void setClassSection(String classSection) {
        this.classSection = classSection;
    }

    public String getClassSectionType() {
        return classSectionType;
    }

    public void setClassSectionType(String classSectionType) {
        this.classSectionType = classSectionType;
    }

    public String getPreviousSchool() {
        return previousSchool;
    }

    public void setPreviousSchool(String previousSchool) {
        this.previousSchool = previousSchool;
    }

    public String getClassPassedAtPreviousSchool() {
        return classPassedAtPreviousSchool;
    }

    public void setClassPassedAtPreviousSchool(String classPassedAtPreviousSchool) {
        this.classPassedAtPreviousSchool = classPassedAtPreviousSchool;
    }

    public String getIncomingTransferCertNo() {
        return incomingTransferCertNo;
    }

    public void setIncomingTransferCertNo(String incomingTransferCertNo) {
        this.incomingTransferCertNo = incomingTransferCertNo;
    }

    public String getOutgoingTransferCertNo() {
        return outgoingTransferCertNo;
    }

    public void setOutgoingTransferCertNo(String outgoingTransferCertNo) {
        this.outgoingTransferCertNo = outgoingTransferCertNo;
    }

    public String getDateOfLeaving() {
        return dateOfLeaving;
    }

    public void setDateOfLeaving(String dateOfLeaving) {
        this.dateOfLeaving = dateOfLeaving;
    }

    public String getCauseOfLeaving() {
        return causeOfLeaving;
    }

    public void setCauseOfLeaving(String causeOfLeaving) {
        this.causeOfLeaving = causeOfLeaving;
    }

    public String getOccupationAfterLeaving() {
        return occupationAfterLeaving;
    }

    public void setOccupationAfterLeaving(String occupationAfterLeaving) {
        this.occupationAfterLeaving = occupationAfterLeaving;
    }

    public boolean canSavePersonalInfo() {
        return (isValidString(admissionNumber) && isValidString(firstName) && isValidString(lastName) && dateOfBirth != null &&
                isValidString(gender) && isValidString(currentClass) && isValidString(classCategory) && isValidString(classSectionType) &&
                dateEnrolled != null && isValidString(contactPersonAddress) && state != null && lga != null && isValidString(contactPersonName) &&
                isValidString(contactPersonPhoneNumber) && isValidString(religion));
    }

    public void save(User user) throws SQLException {
        LocalDateTime now = LocalDateTime.now();
        Instant instant = now.atZone(ZoneId.systemDefault()).toInstant();
        Date currentDateTime = Date.from(instant);

        if (createdAt == null) {
            setCreatedAt(currentDateTime);
        }

        // always update "updatedAt"
        setUpdatedAt(currentDateTime);

        if (createdBy == null) {
            setCreatedBy(user.getUsername());
        }

        // always update updatedBy
        setUpdatedBy(user.getUsername());

        Dao<Student, UUID> studentDao = DatabaseHelper.getStudentDao();

        studentDao.createOrUpdate(this);
    }

    public static List<Student> getAll() throws SQLException {
        Dao<Student, UUID> studentDao = DatabaseHelper.getStudentDao();
        boolean isAscendingOrder = false;
        return studentDao.queryBuilder().orderBy(Student.UPDATED_AT_COLUMNN_NAME, isAscendingOrder).query();
    }

    public static Student find(UUID studentId) throws SQLException {
        return DatabaseHelper.getStudentDao().queryForId(studentId);
    }

    public static List<Student> searchBy(State state, Lga lga, School school, String searchKeyword) throws SQLException {
        Dao<Student, UUID> studentDao = DatabaseHelper.getStudentDao();

        if (state == null && lga == null && school == null && (searchKeyword == null || searchKeyword.isEmpty())) {
            return getAll();
        }

        QueryBuilder<Student, UUID> queryBuilder = studentDao.queryBuilder();
        Where<Student, UUID> where = queryBuilder.where();

        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            // split by space and comma, e.g  "one,   two, three" => [one, two, three]
            String[] keywords = searchKeyword.split(",\\s*");
            for (String keyword : keywords) {
                where.or(
                        where.like("lastName", "%" + keyword + "%"),
                        where.like("firstName", "%" + keyword + "%")
                );
            }
        } else {
            // if keyword is not set, set to all student record, before filtering by school, lga, state in that order.
            where.isNotNull("autoId");
        }

        String idColumn = "id";
        String schoolIdColumn = "school_id";
        String lgaIdColumn = "lga_id";
        String stateIdColumn = "state_id";

        Dao<School, Integer> schoolDao = DatabaseHelper.getSchoolDao();
        QueryBuilder<School, Integer> schoolQueryBuilder = schoolDao.queryBuilder();

        if (school != null) {
            schoolQueryBuilder.selectColumns(idColumn)
                    .where()
                    .eq(idColumn, school.getId());

            where.and()
                    .in(schoolIdColumn, schoolQueryBuilder);

        } else if (lga != null) {
            schoolQueryBuilder.selectColumns(idColumn)
                    .where()
                    .eq(lgaIdColumn, lga.getId());

            where.and()
                    .in(schoolIdColumn, schoolQueryBuilder);

        } else if(state != null) {
            Dao<Lga, Integer> lgaDao = DatabaseHelper.getLgaDao();
            QueryBuilder<Lga, Integer> lgaQueryBuilder = lgaDao.queryBuilder();

            lgaQueryBuilder.selectColumns(idColumn)
                    .where()
                    .eq(stateIdColumn, state.getId());

            schoolQueryBuilder.selectColumns(idColumn)
                    .where()
                    .in(lgaIdColumn, lgaQueryBuilder);

            where.and()
                    .in(schoolIdColumn, schoolQueryBuilder);
        }

        return queryBuilder.query();
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getLastSyncedAt() {
        return lastSyncedAt;
    }

    public void setLastSyncedAt(Date lastSyncedAt) {
        this.lastSyncedAt = lastSyncedAt;
    }

    private boolean isValidString(String value) {
        return !(value == null || value.isEmpty());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(autoId, student.getAutoId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(autoId);
    }
}
