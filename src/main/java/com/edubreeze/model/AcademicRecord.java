package com.edubreeze.model;

import com.edubreeze.database.DatabaseHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@DatabaseTable(tableName = "academic_records")
public class AcademicRecord {
    private static final String UPDATED_AT_COLUMN_NAME = "updatedAt";

    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
    private UUID id;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private StudentAcademicTerm academicTerm;

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

    public AcademicRecord() {
        // no-args constructor required by ORMLite
    }

    public AcademicRecord(
            StudentAcademicTerm academicTerm,
            String subject,
            int continuousAssessmentScore,
            int examScore,
            int totalScore
    ) {
        this.academicTerm = academicTerm;
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

    public StudentAcademicTerm getAcademicTerm() {
        return academicTerm;
    }

    public void setAcademicTerm(StudentAcademicTerm academicTerm) {
        this.academicTerm = academicTerm;
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

    public boolean canSave() {
        return (academicTerm != null && subject != null && !subject.isEmpty());
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

        Dao<AcademicRecord, UUID> academicRecordDao = DatabaseHelper.getAcademicRecordDao();

        /**
         * we check for existing academic records where academic term and subject matches new record and update
         * instead of creating new one.
         */
        QueryBuilder<AcademicRecord, UUID> qb = academicRecordDao.queryBuilder();
        qb.where()
                .eq("academicTerm_id", this.getAcademicTerm().getId())
                .and().eq("subject", this.getSubject());

        AcademicRecord temp = qb.queryForFirst();
        if(temp != null && this.getId() == null) {
            this.setId(temp.getId());
        }

        academicRecordDao.createOrUpdate(this);
    }

    public void savePullSync() throws SQLException {
        Dao<AcademicRecord, UUID> academicRecordDao = DatabaseHelper.getAcademicRecordDao();

        academicRecordDao.createOrUpdate(this);
    }

    public static List<AcademicRecord> getByStudent(Student student) throws SQLException{
        Dao<AcademicRecord, UUID> academicRecordDao = DatabaseHelper.getAcademicRecordDao();
        boolean isAscendingOrder = false;

        return academicRecordDao.queryBuilder()
                .orderBy(AcademicRecord.UPDATED_AT_COLUMN_NAME, isAscendingOrder)
                .where()
                .in("academicTerm_id", student.getAcademicTerms())
                .query();
    }
}
