package com.edubreeze.database;

import com.edubreeze.model.*;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class TableSchemaManager {

    public void initDatabase(DatabaseConnectionInterface databaseConnection) throws SQLException {

        TableUtils.createTableIfNotExists(databaseConnection.getConnectionSource(), User.class);
        TableUtils.createTableIfNotExists(databaseConnection.getConnectionSource(), State.class);
        TableUtils.createTableIfNotExists(databaseConnection.getConnectionSource(), Lga.class);
        TableUtils.createTableIfNotExists(databaseConnection.getConnectionSource(), School.class);
        TableUtils.createTableIfNotExists(databaseConnection.getConnectionSource(), Student.class);
        TableUtils.createTableIfNotExists(databaseConnection.getConnectionSource(), AppStatus.class);
        TableUtils.createTableIfNotExists(databaseConnection.getConnectionSource(), StudentAcademicTerm.class);
        TableUtils.createTableIfNotExists(databaseConnection.getConnectionSource(), StudentFingerprint.class);
        TableUtils.createTableIfNotExists(databaseConnection.getConnectionSource(), AcademicRecord.class);
    }
}
