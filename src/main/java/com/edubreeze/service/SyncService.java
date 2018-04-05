package com.edubreeze.service;

import com.edubreeze.model.*;
import com.edubreeze.utils.DateUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.*;

public class SyncService {
    public static final String UUID_KEY = "uuid";
    private static final String ADMISSION_NO_KEY = "admission_no";
    private static final String FIRST_NAME_KEY = "first_name";
    private static final String LAST_NAME_KEY = "last_name";
    private static final String DATE_OF_BIRTH_KEY = "date_of_birth";
    private static final String GENDER_KEY = "gender";
    private static final String CURRENT_CLASS_KEY = "current_class";
    private static final String CLASS_CATEGORY_KEY = "class_category";
    private static final String DATE_ENROLLED_KEY = "date_enrolled";
    private static final String GUARDIAN_KEY = "guardian";
    private static final String ADDRESS_KEY = "address";
    private static final String PHONE_KEY = "phone";
    private static final String RELIGION_KEY = "religion";
    private static final String SCHOOL_ID_KEY = "school_id";
    private static final String CLASS_TYPE_KEY = "class_type";
    private static final String CLASS_SECTION_KEY = "class_section";
    private static final String PREVIOUS_SCHOOL_KEY = "previous_school";
    private static final String CLASS_PASSED_AT_PREVIOUS_SCHOOL_KEY = "class_passed_at_previous_school";
    private static final String INCOMING_TRANSFER_CERT_NO_KEY = "incoming_transfer_cert_no";
    private static final String OUTGOING_TRANSFER_CERT_NO_KEY = "outgoing_transfer_cert_no";
    private static final String DATE_OF_LEAVING_KEY = "date_of_leaving";
    private static final String CAUSE_OF_LEAVING_KEY = "cause_of_leaving";
    private static final String OCCUPATION_AFTER_LEAVING_KEY = "occupation_after_leaving";
    private static final String LGA_ID_KEY = "lga_id";
    private static final String STATE_ID_KEY = "state_id";
    private static final String PHOTO_KEY = "student_image";
    private static final String STUDENT_UUID_KEY = "student_uuid";
    private static final String FINGERPRINT_IMAGE_KEY = "fingerprint_image";
    private static final String FMD_KEY = "fmd";
    private static final String FINGER_TYPE_KEY = "finger_type";
    public static final String FINGERPRINTS_KEY = "fingerprints";
    private static final String ACADEMIC_TERM_UUID_KEY = "academic_term_uuid";
    private static final String SUBJECT_KEY = "subject";
    private static final String CA_SCORE_KEY = "ca_score";
    private static final String EXAM_SCORE_KEY = "exam_score";
    private static final String TOTAL_SCORE_KEY = "total_score";
    private static final String TERM_KEY = "term";
    private static final String YEAR_KEY = "year";
    private static final String DAYS_PRESENT_KEY = "days_present";
    private static final String DAYS_ABSENT_KEY = "days_absent";
    public static final String ACADEMIC_RECORDS_KEY = "academic_records";
    public static final String ACADEMIC_TERM_KEY = "academic_terms";
    public static final String LAST_SYNCED_AT_KEY = "last_synced_at";
    private static final String SYNCED_AT_KEY = "syncedAt";
    private static final String CREATED_BY_KEY = "createdBy";
    private static final String UPDATED_BY_KEY = "updatedBy";
    private static final String UPDATED_AT_KEY = "updatedAt";
    private static final String CREATED_AT_KEY = "createdAt";
    private static final int KATSINA_LGA_ID = 434;

    public AcademicRecord academicRecordFrom(StudentAcademicTerm academicTerm, JSONObject academicRecordObject) {
        AcademicRecord academicRecord = new AcademicRecord();

        academicRecord.setId(UUID.fromString(academicRecordObject.optString(UUID_KEY)));
        academicRecord.setAcademicTerm(academicTerm);
        academicRecord.setSubject(academicRecordObject.optString(SUBJECT_KEY));
        academicRecord.setContinuousAssessmentScore(academicRecordObject.optInt(CA_SCORE_KEY));
        academicRecord.setExamScore(academicRecordObject.optInt(EXAM_SCORE_KEY));
        academicRecord.setTotalScore(academicRecordObject.optInt(TOTAL_SCORE_KEY));

        return academicRecord;
    }

    public StudentAcademicTerm academicTermFrom(JSONObject academicTermObject) throws SQLException {
        StudentAcademicTerm academicTerm = new StudentAcademicTerm();

        academicTerm.setId(UUID.fromString(academicTermObject.optString(UUID_KEY)));

        Student student = Student.find(UUID.fromString(academicTermObject.optString(STUDENT_UUID_KEY)));
        academicTerm.setStudent(student);

        academicTerm.setTerm(academicTermObject.optString(TERM_KEY));
        academicTerm.setYear(academicTermObject.optString(YEAR_KEY));
        academicTerm.setDaysPresent(academicTermObject.optInt(DAYS_PRESENT_KEY));
        academicTerm.setDaysAbsent(academicTermObject.optInt(DAYS_ABSENT_KEY));

        return academicTerm;
    }

    public StudentFingerprint fingerprintFrom(JSONObject fingerprintObject) throws SQLException {
        StudentFingerprint fingerprint = new StudentFingerprint();

        fingerprint.setId(UUID.fromString(fingerprintObject.optString(UUID_KEY)));
        fingerprint.setFingerprintImageBytes(base64Decoding(fingerprintObject.optString(FINGERPRINT_IMAGE_KEY)));
        fingerprint.setFmdBytes(base64Decoding(fingerprintObject.optString(FMD_KEY)));
        fingerprint.setFingerType(fingerprintObject.optString(FINGER_TYPE_KEY));
        Student student = Student.find(UUID.fromString(fingerprintObject.optString(STUDENT_UUID_KEY)));
        fingerprint.setStudent(student);

        return fingerprint;
    }

    public List<StudentFingerprint> fingerprintsFrom(JSONArray fingerprintList) throws Exception {
        List<StudentFingerprint> fingerprints = new ArrayList<>();

        int fingerprintSize = fingerprintList.length();
        for (int i = 0; i < fingerprintSize; i++) {
            StudentFingerprint fingerprint = fingerprintFrom(fingerprintList.getJSONObject(i));
            fingerprints.add(fingerprint);
        }

        return fingerprints;
    }

    public Student studentFrom(JSONObject studentObject) throws SQLException {
        String autoId = studentObject.optString(UUID_KEY);
        String admissionNumber = studentObject.optString(ADMISSION_NO_KEY);
        String firstName = studentObject.optString(FIRST_NAME_KEY);
        String lastName = studentObject.optString(LAST_NAME_KEY);
        String gender = studentObject.optString(GENDER_KEY);
        String currentClass = studentObject.optString(CURRENT_CLASS_KEY);
        String classCategory = studentObject.optString(CLASS_CATEGORY_KEY);
        String classSection = studentObject.optString(CLASS_SECTION_KEY);
        String classSectionType = studentObject.optString(CLASS_TYPE_KEY);
        Integer schoolId = studentObject.optInt(SCHOOL_ID_KEY);
        Integer lgaId = studentObject.optInt(LGA_ID_KEY, KATSINA_LGA_ID);
        lgaId = (lgaId == 0)? KATSINA_LGA_ID : lgaId;
        Integer stateId = studentObject.optInt(STATE_ID_KEY);

        Date dateOfBirth = DateUtil.getDateFromTimestampInSeconds(studentObject.optLong(DATE_OF_BIRTH_KEY));

        String dateEnrolled = studentObject.optString(DATE_ENROLLED_KEY);
        String contactPersonName = studentObject.optString(GUARDIAN_KEY);
        String contactPersonPhoneNumber = studentObject.optString(PHONE_KEY);
        String contactPersonAddress = studentObject.optString(ADDRESS_KEY);
        String religion = studentObject.optString(RELIGION_KEY);
        String previousSchool = studentObject.optString(PREVIOUS_SCHOOL_KEY);
        String classPassedAtPreviousSchool = studentObject.optString(CLASS_PASSED_AT_PREVIOUS_SCHOOL_KEY);
        String incomingTransferCertNo = studentObject.optString(INCOMING_TRANSFER_CERT_NO_KEY);
        String outgoingTransferCertNo = studentObject.optString(OUTGOING_TRANSFER_CERT_NO_KEY);
        String dateOfLeaving = studentObject.optString(DATE_OF_LEAVING_KEY);
        String causeOfLeaving = studentObject.optString(CAUSE_OF_LEAVING_KEY);
        String occupationAfterLeaving = studentObject.optString(OCCUPATION_AFTER_LEAVING_KEY);

        Date lastSyncedAt = DateUtil.getDateFromTimestampInSeconds(studentObject.optLong(SYNCED_AT_KEY, 0));

        byte[] studentImage = base64Decoding(studentObject.optString(PHOTO_KEY));

        Student student = new Student();
        student.setAutoId(UUID.fromString(autoId));
        student.setAdmissionNumber(admissionNumber);
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setGender(gender);
        student.setCurrentClass(currentClass);
        student.setClassCategory(classCategory);
        student.setClassSection(classSection);
        student.setClassSectionType(classSectionType);
        student.setSchool(School.find(schoolId));
        student.setLga(Lga.find(lgaId));
        student.setState(State.find(stateId));
        student.setDateOfBirth(dateOfBirth);
        student.setDateEnrolled(dateEnrolled);
        student.setContactPersonName(contactPersonName);
        student.setContactPersonPhoneNumber(contactPersonPhoneNumber);
        student.setContactPersonAddress(contactPersonAddress);
        student.setReligion(religion);
        student.setPreviousSchool(previousSchool);
        student.setClassPassedAtPreviousSchool(classPassedAtPreviousSchool);
        student.setIncomingTransferCertNo(incomingTransferCertNo);
        student.setOutgoingTransferCertNo(outgoingTransferCertNo);
        student.setDateOfLeaving(dateOfLeaving);
        student.setCauseOfLeaving(causeOfLeaving);
        student.setOccupationAfterLeaving(occupationAfterLeaving);
        student.setLastSyncedAt(lastSyncedAt);
        student.setStudentImage(studentImage);

        student.setCreatedBy(studentObject.optString(CREATED_BY_KEY, "Not set"));
        student.setUpdatedBy(studentObject.optString(UPDATED_BY_KEY, "Not set"));

        student.setUpdatedAt(DateUtil.getDateFromTimestampInSeconds(studentObject.optLong(UPDATED_AT_KEY, 0)));
        student.setCreatedAt(DateUtil.getDateFromTimestampInSeconds(studentObject.optLong(CREATED_AT_KEY, 0)));

        return student;
    }

    public JSONArray convertToFingerprintsPayload(List<StudentFingerprint> fingerprints) {
        JSONArray fingerprintsPayload = new JSONArray();

        for (StudentFingerprint fingerprint : fingerprints) {
            JSONObject fingerprintPayload = convertToFingerprintPayload(fingerprint);
            fingerprintsPayload.put(fingerprintPayload);
        }

        return fingerprintsPayload;
    }

    public JSONObject convertToAcademicRecordPayload(AcademicRecord academicRecord) {
        JSONObject academicRecordPayload = new JSONObject();

        academicRecordPayload.put(UUID_KEY, academicRecord.getId());
        academicRecordPayload.put(ACADEMIC_TERM_UUID_KEY, academicRecord.getAcademicTerm().getId());
        academicRecordPayload.put(SUBJECT_KEY, academicRecord.getSubject());
        academicRecordPayload.put(CA_SCORE_KEY, academicRecord.getContinuousAssessmentScore());
        academicRecordPayload.put(EXAM_SCORE_KEY, academicRecord.getExamScore());
        academicRecordPayload.put(TOTAL_SCORE_KEY, academicRecord.getTotalScore());

        return academicRecordPayload;
    }

    public JSONArray convertToAcademicRecordsPayload(List<AcademicRecord> academicRecords) {
        JSONArray academicRecordsPayload = new JSONArray();
        for (AcademicRecord academicRecord : academicRecords) {
            JSONObject academicRecordPayload = convertToAcademicRecordPayload(academicRecord);

            academicRecordsPayload.put(academicRecordPayload);
        }
        return academicRecordsPayload;
    }

    public JSONArray convertToAcademicTermsPayload(List<StudentAcademicTerm> academicTerms) {
        JSONArray studentAcademicTerms = new JSONArray();
        for (StudentAcademicTerm academicTerm : academicTerms) {
            studentAcademicTerms.put(convertToAcademicTermPayload(academicTerm));
        }
        return studentAcademicTerms;
    }

    public JSONObject convertToAcademicTermPayload(StudentAcademicTerm academicTerm) {
        JSONObject academicTermPayload = new JSONObject();

        academicTermPayload.put(UUID_KEY, academicTerm.getId());
        academicTermPayload.put(STUDENT_UUID_KEY, academicTerm.getStudent().getAutoId());
        academicTermPayload.put(TERM_KEY, academicTerm.getTerm());
        academicTermPayload.put(YEAR_KEY, academicTerm.getYear());
        academicTermPayload.put(DAYS_PRESENT_KEY, academicTerm.getDaysPresent());
        academicTermPayload.put(DAYS_ABSENT_KEY, academicTerm.getDaysAbsent());
        academicTermPayload.put(ACADEMIC_RECORDS_KEY, convertToAcademicRecordsPayload(new ArrayList<>(academicTerm.getAcademicRecords())));

        return academicTermPayload;
    }

    public JSONObject convertToFingerprintPayload(StudentFingerprint fingerprint) {
        JSONObject fingerprintPayload = new JSONObject();

        fingerprintPayload.put(UUID_KEY, fingerprint.getId());
        fingerprintPayload.put(FINGER_TYPE_KEY, fingerprint.getFingerType());
        fingerprintPayload.put(STUDENT_UUID_KEY, fingerprint.getStudent().getAutoId());
        fingerprintPayload.put(FMD_KEY, base64Encoding(fingerprint.getFmdBytes()));
        fingerprintPayload.put(FINGERPRINT_IMAGE_KEY, base64Encoding(fingerprint.getFingerprintImageBytes()));

        return fingerprintPayload;
    }

    public JSONObject convertToStudentPayload(Student student) {

        JSONObject studentPayload = new JSONObject();

        studentPayload.put(UUID_KEY, student.getAutoId().toString());
        studentPayload.put(ADMISSION_NO_KEY, student.getAdmissionNumber());
        studentPayload.put(FIRST_NAME_KEY, student.getFirstName());
        studentPayload.put(LAST_NAME_KEY, student.getLastName());
        studentPayload.put(DATE_OF_BIRTH_KEY, DateUtil.convertDateToSeconds(student.getDateOfBirth()));
        studentPayload.put(GENDER_KEY, student.getGender());
        studentPayload.put(CURRENT_CLASS_KEY, student.getCurrentClass());
        studentPayload.put(CLASS_CATEGORY_KEY, student.getClassCategory());
        studentPayload.put(DATE_ENROLLED_KEY, student.getDateEnrolled());
        studentPayload.put(GUARDIAN_KEY, student.getContactPersonName());
        studentPayload.put(ADDRESS_KEY, student.getContactPersonAddress());
        studentPayload.put(PHONE_KEY, student.getContactPersonPhoneNumber());
        studentPayload.put(RELIGION_KEY, student.getReligion());
        studentPayload.put(SCHOOL_ID_KEY, student.getSchool().getId());
        studentPayload.put(CLASS_TYPE_KEY, student.getClassSectionType());
        studentPayload.put(CLASS_SECTION_KEY, student.getClassSection());
        studentPayload.put(PREVIOUS_SCHOOL_KEY, student.getPreviousSchool());
        studentPayload.put(CLASS_PASSED_AT_PREVIOUS_SCHOOL_KEY, student.getClassPassedAtPreviousSchool());
        studentPayload.put(INCOMING_TRANSFER_CERT_NO_KEY, student.getIncomingTransferCertNo());
        studentPayload.put(OUTGOING_TRANSFER_CERT_NO_KEY, student.getOutgoingTransferCertNo());
        studentPayload.put(DATE_OF_LEAVING_KEY, student.getDateOfLeaving());
        studentPayload.put(CAUSE_OF_LEAVING_KEY, student.getCauseOfLeaving());
        studentPayload.put(OCCUPATION_AFTER_LEAVING_KEY, student.getOccupationAfterLeaving());
        studentPayload.put(LGA_ID_KEY, student.getLga().getId());
        studentPayload.put(STATE_ID_KEY, student.getState().getId());

        String imageDataStr = (student.getStudentImage() != null) ? base64Encoding(student.getStudentImage()) : "";
        studentPayload.put(PHOTO_KEY, imageDataStr);

        return studentPayload;
    }

    private String base64Encoding(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    private byte[] base64Decoding(String encodedString) {
        if (encodedString == null) {
            return null;
        }
        return Base64.getDecoder().decode(encodedString);
    }
}
