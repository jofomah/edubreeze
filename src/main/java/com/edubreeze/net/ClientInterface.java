package com.edubreeze.net;

import com.edubreeze.net.exceptions.ApiClientException;

/**
 * An interface for connecting to remote server
 */
public interface ClientInterface {
    /**
     * Fetches a student resource object or null
     * @param int studentId
     * @return Object|null
     */
    public Object getStudent(int studentId);

    public String login(String username, String password) throws ApiClientException;

}
