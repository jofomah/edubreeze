package com.edubreeze.database;

import com.edubreeze.config.AppConfiguration;
import com.edubreeze.database.dao.observers.StudentDaoObserver;
import com.edubreeze.model.*;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;
import java.util.UUID;

public class DatabaseHelper {

    private static Dao<State, Integer> stateDao = null;
    private static DatabaseConnectionInterface databaseConnection = null;
    private static Dao<Lga, Integer> lgaDao = null;
    private static Dao<School, Integer> schoolDao = null;
    private static Dao<Student, UUID> studentDao = null;
    private static Dao<AppStatus, String> appStatusDao = null;
    private static Dao<StudentAcademicRecord, UUID> studentAcademicPerformanceDao = null;
    private static Dao<StudentFingerprint, UUID> studentFingerprintDao = null;

    public static DatabaseConnectionInterface getDatabaseConnection() throws SQLException {
        if (databaseConnection == null) {
            databaseConnection = new H2DatabaseConnection(AppConfiguration.getDatabaseFileUrl());
        }
        return databaseConnection;
    }

    public static Dao<StudentAcademicRecord, UUID> getStudentAcademicPerformanceDao() throws SQLException{
        if(studentAcademicPerformanceDao == null) {
            studentAcademicPerformanceDao = DaoManager.createDao(getDatabaseConnection().getConnectionSource(), StudentAcademicRecord.class);
        }
        return studentAcademicPerformanceDao;
    }

    public static Dao<State, Integer> getStateDao() throws SQLException {
        if (stateDao == null) {
            stateDao = DaoManager.createDao(getDatabaseConnection().getConnectionSource(), State.class);
        }
        return stateDao;
    }

    public static Dao<Lga, Integer> getLgaDao() throws SQLException {
        if (lgaDao == null) {
            lgaDao = DaoManager.createDao(getDatabaseConnection().getConnectionSource(), Lga.class);
        }

        return lgaDao;
    }

    public static Dao<School, Integer> getSchoolDao() throws SQLException {
        if (schoolDao == null) {
            schoolDao = DaoManager.createDao(getDatabaseConnection().getConnectionSource(), School.class);
        }

        return schoolDao;
    }

    public static Dao<Student, UUID> getStudentDao() throws SQLException {
        if (studentDao == null) {
            studentDao = DaoManager.createDao(getDatabaseConnection().getConnectionSource(), Student.class);
            studentDao.registerObserver(new StudentDaoObserver());
        }

        return studentDao;
    }

    public static Dao<AppStatus, String> getAppStatusDao() throws SQLException {
        if (appStatusDao == null) {
            appStatusDao = DaoManager.createDao(getDatabaseConnection().getConnectionSource(), AppStatus.class);
        }

        return appStatusDao;
    }

    public static Dao<StudentFingerprint, UUID> getStudentFingerprintDao() throws SQLException {
        if(studentFingerprintDao == null) {
            studentFingerprintDao = DaoManager.createDao(getDatabaseConnection().getConnectionSource(), StudentFingerprint.class);
        }
        return studentFingerprintDao;
    }
}
