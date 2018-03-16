package com.edubreeze.model;

import com.edubreeze.database.DatabaseHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@DatabaseTable(tableName = "student_academic_records")
public class StudentAcademicRecord {

    private static final String UPDATED_AT_COLUMNN_NAME = "updatedAt";

    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
    private UUID id;

    @DatabaseField(canBeNull = false)
    private String term;

    @DatabaseField(canBeNull = false)
    private String year;

    @DatabaseField(canBeNull = false)
    private int daysPresent;

    @DatabaseField(canBeNull = false)
    private int daysAbsent;

    @DatabaseField(canBeNull = false)
    private String subject;

    @DatabaseField(canBeNull = false)
    private int continuousAssessmentScore;

    @DatabaseField(canBeNull = false)
    private int examScore;

    @DatabaseField(canBeNull = false)
    private int totalScore;

    @DatabaseField(canBeNull = false)
    private String createdBy;

    @DatabaseField(canBeNull = false)
    private String updatedBy;

    @DatabaseField(canBeNull = false)
    private Date createdAt;

    @DatabaseField(canBeNull = false)
    private Date updatedAt;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Student student;

    public StudentAcademicRecord() {
        // no-args constructor required by ORMLite
    }

    public StudentAcademicRecord(
            String term,
            String year,
            int daysPresent,
            int daysAbsent,
            String subject,
            int continuousAssessmentScore,
            int examScore,
            int totalScore
    ) {
        this.term = term;
        this.year = year;
        this.daysPresent = daysPresent;
        this.daysAbsent = daysAbsent;
        this.subject = subject;
        this.continuousAssessmentScore = continuousAssessmentScore;
        this.examScore = examScore;
        this.totalScore = totalScore;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getDaysPresent() {
        return daysPresent;
    }

    public void setDaysPresent(int daysPresent) {
        this.daysPresent = daysPresent;
    }

    public int getDaysAbsent() {
        return daysAbsent;
    }

    public void setDaysAbsent(int daysAbsent) {
        this.daysAbsent = daysAbsent;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getContinuousAssessmentScore() {
        return continuousAssessmentScore;
    }

    public void setContinuousAssessmentScore(int continuousAssessmentScore) {
        this.continuousAssessmentScore = continuousAssessmentScore;
    }

    public int getExamScore() {
        return examScore;
    }

    public void setExamScore(int examScore) {
        this.examScore = examScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
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

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public boolean canSave() {
        return (term != null && !term.isEmpty() && year != null && !year.isEmpty() && student != null &&
                subject != null && !subject.isEmpty());
    }

    public void save(User user) throws SQLException{
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

        Dao<StudentAcademicRecord, UUID> studentAcademicDao = DatabaseHelper.getStudentAcademicPerformanceDao();

        studentAcademicDao.createOrUpdate(this);
    }

    public static List<StudentAcademicRecord> getByStudent(Student student) throws SQLException{
        Dao<StudentAcademicRecord, UUID> studentAcademiceDao = DatabaseHelper.getStudentAcademicPerformanceDao();
        boolean isAscendingOrder = false;

        return studentAcademiceDao.queryBuilder()
                .orderBy(StudentAcademicRecord.UPDATED_AT_COLUMNN_NAME, isAscendingOrder)
                .where()
                .eq("student_id", student)
                .query();
    }
}
