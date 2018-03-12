package com.edubreeze.service.tasks;

import com.edubreeze.model.User;
import com.edubreeze.net.exceptions.ApiClientException;
import com.edubreeze.service.exceptions.LoginFailedException;
import com.edubreeze.service.exceptions.WrongLoginCredentialsException;
import com.edubreeze.service.LoginService;
import com.edubreeze.service.exceptions.MissingRequiredCredentialsException;
import javafx.concurrent.Task;

import java.sql.SQLException;

public class LoginTask extends Task<User> {

    private final String username;
    private final String password;
    private final LoginService loginService;

    public LoginTask(String username, String password, LoginService loginService) {
        this.username = username;
        this.password = password;
        this.loginService = loginService;
    }

    /**
     * Runs login task in the background.
     * @return User
     * @throws SQLException
     * @throws WrongLoginCredentialsException
     * @throws ApiClientException
     * @throws MissingRequiredCredentialsException
     * @throws LoginFailedException
     */
    @Override
    protected User call() throws SQLException, WrongLoginCredentialsException, LoginFailedException, MissingRequiredCredentialsException {

        User loggedInUser = null;
        try {

            String apiToken = loginService.login(username, password);

            // save user record
            loggedInUser = loginService.saveValidLogin(username, password, apiToken);

        } catch(ApiClientException ex) {

            // API error, could be offline, try offline login
            loggedInUser = loginService.loginOffline(username, password);

        } finally {
            if (loggedInUser != null) {
                // set current logged in user session
                LoginService.setCurrentLoggedInUser(loggedInUser);
            }
        }

        if(loggedInUser == null) {
            throw new LoginFailedException("Login failed");
        }

        return loggedInUser;
    }
}
