package com.edubreeze.net;

import com.edubreeze.config.AppConfiguration;
import com.edubreeze.net.exceptions.ApiClientException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

public class ApiClient implements ClientInterface {

    private static final String SIGN_IN_ENDPOINT = "/signin";

    private String apiBaseUrl;

    public ApiClient()
    {
        this.apiBaseUrl = AppConfiguration.getByKey(AppConfiguration.API_BASE_URL_KEY);
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
            System.out.println(ex.getClass().getName());
            throw new ApiClientException(ex.getMessage(), ex.getCause());
        }

        return apiToken;
    }

    public Object getStudent(int studentId)
    {
        return new Object();
    }
}
