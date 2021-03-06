package com.edubreeze.service;

import com.edubreeze.config.AppConfiguration;
import com.edubreeze.database.DatabaseConnectionInterface;
import com.edubreeze.database.H2DatabaseConnection;
import com.edubreeze.model.User;
import com.edubreeze.net.ClientInterface;
import com.edubreeze.net.exceptions.ApiClientException;
import com.edubreeze.service.exceptions.MissingRequiredCredentialsException;
import com.edubreeze.service.exceptions.WrongLoginCredentialsException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import javafx.beans.property.SimpleObjectProperty;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class LoginService {

    private ClientInterface apiClient;

    private static User currentLoggedInUser = null;

    public static final SimpleObjectProperty<User> currentLoggedInUserProperty = new SimpleObjectProperty<>();

    public LoginService(ClientInterface apiClient) {
        this.apiClient = apiClient;
    }

    public static User getCurrentLoggedInUser() {
        return currentLoggedInUser;
    }

    public static void setCurrentLoggedInUser(User user) {
        currentLoggedInUser = user;

        currentLoggedInUserProperty.set(currentLoggedInUser);
    }

    public static SimpleObjectProperty<User> getCurrentLoggedInUserProperty() {
        return currentLoggedInUserProperty;
    }

    public String login(String email, String password) throws ApiClientException, MissingRequiredCredentialsException, WrongLoginCredentialsException {

        if(email == null || email.isEmpty()) {
            throw new MissingRequiredCredentialsException("Username is either not set or is empty, username: " + email);
        }

        if(password == null || password.isEmpty()) {
            throw new MissingRequiredCredentialsException("Password is either not set or is empty, password " + password);
        }

        String apiToken = apiClient.login(email, password);

        if(apiToken == null) {
            throw new WrongLoginCredentialsException("Invalid username and/or password");
        }

        return apiToken;
    }

    public User saveValidLogin(String username, String password, String apiToken) throws SQLException
    {
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

        DatabaseConnectionInterface dbConnection = new H2DatabaseConnection(AppConfiguration.getDatabaseFileUrl());

        // instantiate the dao
        Dao<User, String> userDao = DaoManager.createDao(dbConnection.getConnectionSource(), User.class);

        User user = new User(username, passwordHash, apiToken);

        userDao.createOrUpdate(user);

        return user;
    }

    public User loginOffline(String username, String password) throws SQLException, WrongLoginCredentialsException
    {
        DatabaseConnectionInterface dbConnection = new H2DatabaseConnection(AppConfiguration.getDatabaseFileUrl());
        Dao<User, String> userDao = DaoManager.createDao(dbConnection.getConnectionSource(), User.class);

        User user = userDao.queryForId(username);

        if(BCrypt.checkpw(password, user.getPassword()))
        {
            return user;
        }

        throw new WrongLoginCredentialsException("Wrong username and/or password combination.");
    }
}
