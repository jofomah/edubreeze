package com.edubreeze.database;

import com.edubreeze.model.User;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class TableSchemaManager {

    public void initDatabase(DatabaseConnectionInterface databaseConnection) throws SQLException {

        TableUtils.createTableIfNotExists(databaseConnection.getConnectionSource(), User.class);
    }
}
