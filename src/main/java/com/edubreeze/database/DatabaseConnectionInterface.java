package com.edubreeze.database;

import com.j256.ormlite.support.ConnectionSource;

import java.io.IOException;

public interface DatabaseConnectionInterface {
    /**
     *  Returns ConnectionSource instance
     * @return ConnectionSource
     */
    ConnectionSource getConnectionSource();

    /**
     * Closes database connection
     * @throws IOException
     */
    void close() throws IOException;
}
