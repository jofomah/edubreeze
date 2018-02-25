package com.edubreeze.database;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.io.IOException;
import java.sql.SQLException;

public class H2DatabaseConnection implements DatabaseConnectionInterface{

    private String databaseUrl;
    private JdbcConnectionSource connectionSource;

    public H2DatabaseConnection(String databaseUrl) throws SQLException{
        this.databaseUrl = databaseUrl;
        this.connectionSource = new JdbcConnectionSource(this.databaseUrl);
    }

    /**
     * Returns ConnectionSource instance
     *
     * @return ConnectionSource
     */
    public ConnectionSource getConnectionSource() {
        return this.connectionSource;
    }

    /**
     * Closes database connection
     * throws IOException
     */
    public void close() throws IOException {
        if(this.connectionSource != null) {
            this.connectionSource.close();
        }
    }
}
