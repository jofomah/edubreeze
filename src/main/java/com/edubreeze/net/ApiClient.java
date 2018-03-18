package com.edubreeze.net;

import com.edubreeze.net.exceptions.ApiClientException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

public class ApiClient implements ClientInterface {

    private static final String SIGN_IN_ENDPOINT = "/signin";
    private static final String STATES_ENDPOINT = "/getStates";
    private static final String SCHOOLS_BY_LGA = "/getSchoolsByLga/";
    private static final String SCHOOLS_BY_STATE = "/getSchoolsByState/";
    private static final String API_BASE_URL = "http://katsina-edubreeze.ng/Webservice";

    private String apiBaseUrl;

    public ApiClient()
    {
        this.apiBaseUrl = API_BASE_URL;
    }

    public String login(String email, String password) throws ApiClientException
    {
        String loginUrl = this.apiBaseUrl + SIGN_IN_ENDPOINT;
        String apiToken = null;

        try {
            HttpResponse<JsonNode> response = Unirest.post(loginUrl)
                    .header("accept", "application/json")
                    .field("email", email)
                    .field("password", password)
                    .asJson();

            JSONObject responseObject = response.getBody().getObject();
            String jwtKey = "jwt";

            if(responseObject.has(jwtKey)) {
               apiToken = responseObject.getString(jwtKey);
            }

        } catch (UnirestException ex) {
            throw new ApiClientException(ex.getMessage(), ex.getCause());
        }

        return apiToken;
    }

    public JSONArray getStates(String apiToken) throws ApiClientException {
        String getStatesUrl = this.apiBaseUrl + STATES_ENDPOINT;

        try {
            HttpResponse<JsonNode> response = Unirest.get(getStatesUrl)
                    .header("accept", "application/json")
                    .header("Authorization", getAuthorizationToken(apiToken))
                    .asJson();

            return response.getBody().getArray();

        } catch (UnirestException ex) {
            throw new ApiClientException(ex.getMessage(), ex.getCause());
        }
    }

    public JSONArray getSchoolsByState(String apiToken, int stateId) throws ApiClientException {
        String getSchoolsByStateUrl = this.apiBaseUrl + SCHOOLS_BY_STATE + stateId;

        try {
            HttpResponse<JsonNode> response = Unirest.get(getSchoolsByStateUrl)
                    .header("accept", "application/json")
                    .header("Authorization", getAuthorizationToken(apiToken))
                    .asJson();

            return response.getBody().getArray();

        } catch (UnirestException ex) {
            throw new ApiClientException(ex.getMessage(), ex.getCause());
        }
    }

    public JSONArray getSchoolsByLga(String apiToken, int lgaId) throws ApiClientException {
        String getSchoolsByLgaUrl = this.apiBaseUrl + SCHOOLS_BY_LGA + lgaId;

        try {
            HttpResponse<JsonNode> response = Unirest.get(getSchoolsByLgaUrl)
                    .header("accept", "application/json")
                    .header("Authorization", getAuthorizationToken(apiToken))
                    .asJson();

            return response.getBody().getArray();

        } catch (UnirestException ex) {
            throw new ApiClientException(ex.getMessage(), ex.getCause());
        }
    }

    private String getAuthorizationToken(String apiToken) {
        return "Bearer " + apiToken;
    }

    public Object getStudent(int studentId)
    {
        return new Object();
    }
}
