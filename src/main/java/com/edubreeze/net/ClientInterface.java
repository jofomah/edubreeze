package com.edubreeze.net;

import com.edubreeze.model.State;
import com.edubreeze.net.exceptions.ApiClientException;
import org.json.JSONArray;

import java.util.List;

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

    public JSONArray getStates(String apiToken) throws ApiClientException;

    public JSONArray getSchoolsByLga(String apiToken, int lgaId) throws ApiClientException;

    public JSONArray getSchoolsByState(String apiToken, int stateId) throws ApiClientException;

}
