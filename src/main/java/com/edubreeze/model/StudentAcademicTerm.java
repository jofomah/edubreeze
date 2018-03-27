package com.edubreeze.model;

import com.edubreeze.database.DatabaseHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@DatabaseTable(tableName = "student_academic_terms")
public class StudentAcademicTerm {
    private static final String UPDATED_AT_COLUMN_NAME = "updatedAt";

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
    private String createdBy;

    @DatabaseField(canBeNull = false)
    private String updatedBy;

    @DatabaseField(canBeNull = false)
    private Date createdAt;

    @DatabaseField(canBeNull = false)
    private Date updatedAt;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Student student;

    @ForeignCollectionField(eager = false)
    private ForeignCollection<AcademicRecord> academicRecords;

    public StudentAcademicTerm() {
        // no args constructor required by ORMLite
    }

    public StudentAcademicTerm(String term, String year, int daysPresent, int daysAbsent) {
        this.term = term;
        this.year = year;
        this.daysPresent = daysPresent;
        this.daysAbsent = daysAbsent;
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

    public ForeignCollection<AcademicRecord> getAcademicRecords() {
        return academicRecords;
    }

    public boolean canSave() {
        return (term != null && !term.isEmpty() && year != null && !year.isEmpty() && student != null);
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

        /**
         * We check if there is an existing students academic record that matches new academic record's year, term and
         * student id, if so, we update that to avoid duplicate.
         */
        Dao<StudentAcademicTerm, UUID> studentAcademicTermDao = DatabaseHelper.getStudentAcademicTermDao();

        QueryBuilder<StudentAcademicTerm, UUID> qb = studentAcademicTermDao.queryBuilder();
        qb.where()
                .and().eq("student_id", this.getStudent().getAutoId())
                .and().eq("year", this.getYear())
                .and().eq("term", this.getTerm());

        StudentAcademicTerm temp = qb.queryForFirst();
        if(temp != null && this.getId() == null) {
            this.setId(temp.getId());
        }

        studentAcademicTermDao.createOrUpdate(this);
    }

    public void savePullSync() throws SQLException {
        Dao<StudentAcademicTerm, UUID> studentAcademicTermDao = DatabaseHelper.getStudentAcademicTermDao();

        studentAcademicTermDao.createOrUpdate(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentAcademicTerm that = (StudentAcademicTerm) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
