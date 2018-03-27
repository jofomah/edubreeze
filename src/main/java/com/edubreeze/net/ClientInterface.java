package com.edubreeze.net;

import com.edubreeze.net.exceptions.ApiClientException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An interface for connecting to remote server
 */
public interface ClientInterface {

    public JSONObject pushStudent(String apiToken, JSONObject payload) throws ApiClientException;

    public String login(String username, String password) throws ApiClientException;

    public JSONArray getStates(String apiToken) throws ApiClientException;

    public JSONArray getSchoolsByLga(String apiToken, int lgaId) throws ApiClientException;

    public JSONArray getSchoolsByState(String apiToken, int stateId) throws ApiClientException;

    public JSONArray getStudentsBySchool(String apiToken, int schoolId) throws ApiClientException;

}
