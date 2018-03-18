package com.edubreeze.model;

import com.edubreeze.database.DatabaseHelper;
import com.edubreeze.service.enrollment.FingerPrintEnrollment;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@DatabaseTable(tableName = "student_fingerprints")
public class StudentFingerprint {

    private static final String UPDATED_AT_COLUMNN_NAME = "updatedAt";

    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
    private UUID id;

    @DatabaseField(dataType = DataType.BYTE_ARRAY, canBeNull = false)
    byte[] fingerprintImageBytes;

    @DatabaseField(dataType = DataType.BYTE_ARRAY, canBeNull = false)
    byte[] fmdBytes;

    @DatabaseField(canBeNull = false)
    String fingerType;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Student student;

    @DatabaseField(canBeNull = false)
    private String createdBy;

    @DatabaseField(canBeNull = false)
    private String updatedBy;

    @DatabaseField(canBeNull = false)
    private Date createdAt;

    @DatabaseField(canBeNull = false)
    private Date updatedAt;

    public StudentFingerprint() {
        // no args constructor required by ORMLite
    }

    public StudentFingerprint(byte[] fpImageBytes, byte[] fmdData, String fpType, Student std) {
        fingerprintImageBytes = fpImageBytes;
        fmdBytes = fmdData;
        fingerType = fpType;
        student = std;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public byte[] getFingerprintImageBytes() {
        return fingerprintImageBytes;
    }

    public void setFingerprintImageBytes(byte[] fingerprintImageBytes) {
        this.fingerprintImageBytes = fingerprintImageBytes;
    }

    public byte[] getFmdBytes() {
        return fmdBytes;
    }

    public void setFmdBytes(byte[] fmdBytes) {
        this.fmdBytes = fmdBytes;
    }

    public String getFingerType() {
        return fingerType;
    }

    public void setFingerType(String fingerType) {
        this.fingerType = fingerType;
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

    public boolean canSaveBiometric() {
        return (fingerType != null && !fingerType.isEmpty() &&
                fingerprintImageBytes != null && fingerprintImageBytes.length > 0 &&
                fmdBytes != null && fmdBytes.length > 0 && student != null);
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

        Dao<StudentFingerprint, UUID> studentFingerprintDao = DatabaseHelper.getStudentFingerprintDao();

        studentFingerprintDao.createOrUpdate(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentFingerprint that = (StudentFingerprint) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId());
    }
}
