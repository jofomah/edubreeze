package com.edubreeze.controllers;

import com.edubreeze.config.AppConfiguration;
import com.edubreeze.model.User;
import com.edubreeze.net.ApiClient;
import com.edubreeze.service.ApplicationService;
import com.edubreeze.service.LoginService;
import com.edubreeze.service.exceptions.MissingRequiredCredentialsException;
import com.edubreeze.service.exceptions.WrongLoginCredentialsException;
import com.edubreeze.service.tasks.LoginTask;
import com.edubreeze.utils.ExceptionTracker;
import com.edubreeze.utils.Util;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private LoginService loginService;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loginService = new LoginService(new ApiClient());
    }

    @FXML
    private void handleLoginButtonClick(MouseEvent event) {

        final ContentDisplay loginBtnContentDisplay = loginButton.getContentDisplay();
        final String loginBtnText = loginButton.getText();
        final Node loginIconImage = loginButton.getGraphic();

        disableLoginForm();

        LoginTask loginTask = new LoginTask(getUsername(), getPassword(), loginService);

        /**
         * set login success callback
         */
        loginTask.setOnSucceeded(workStateEvent -> {
            User loggedInUser = (User) workStateEvent.getSource().getValue();

            ApplicationService appService = new ApplicationService(loggedInUser.getApiToken(), new ApiClient());

            try {
                Stage screenStage = (Stage) loginButton.getScene().getWindow();
                if (appService.hasCompletedInitialSync()) {

                    Util.changeScreen(screenStage, AppConfiguration.STUDENT_LIST_SCREEN);

                } else {
                    Util.changeScreen(screenStage, AppConfiguration.LOADING_SCREEN);
                }

            } catch (SQLException ex) {
                ExceptionTracker.track(ex);
                Util.showExceptionDialogBox(
                        ex,
                        AppConfiguration.CHECK_INITIAL_SYNC_ERROR_HEADER,
                        AppConfiguration.INITIAL_SYNC_STATUS_CHECK_SQL_ERROR
                );

            } catch (IOException ex) {
                ExceptionTracker.track(ex);
                Util.showExceptionDialogBox(
                        ex,
                        AppConfiguration.CHANGE_SCREEN_ERROR_HEADER,
                        AppConfiguration.CHANGE_SCREEN_ERROR_DETAIL
                );
            } finally {
                enableLoginForm(loginBtnContentDisplay, loginIconImage, loginBtnText);
            }

        });

        /**
         * set login failed callback
         */
        loginTask.setOnFailed(workStateEvent -> {

            enableLoginForm(loginBtnContentDisplay, loginIconImage, loginBtnText);

            Throwable throwable = loginTask.getException();

            if (throwable instanceof WrongLoginCredentialsException) {
                Util.showErrorDialog(
                        AppConfiguration.LOGIN_ERROR_DIALOG_HEADER,
                        AppConfiguration.WRONG_USERNAME_AND_PASSWORD,
                        AppConfiguration.PLEASE_ENTER_CORRECT_CREDENTIALS
                );

            } else if (throwable instanceof MissingRequiredCredentialsException) {
                Util.showErrorDialog(
                        AppConfiguration.LOGIN_ERROR_DIALOG_HEADER,
                        AppConfiguration.MISSING_LOGIN_CREDENTIALS,
                        AppConfiguration.PLEASE_ENTER_MISSING_CREDENTIALS
                );

            } else if (throwable instanceof SQLException) {
                Util.showErrorDialog(
                        AppConfiguration.LOGIN_ERROR_DIALOG_HEADER,
                        AppConfiguration.LOGIN_SQL_ERROR,
                        AppConfiguration.LOGIN_SQL_ERROR_DETAIL
                );
            }
        });

        // start login task
        Thread th = new Thread(loginTask);
        th.start();
    }

    private String getUsername() {
        return usernameField.getText().trim();
    }

    private String getPassword() {
        return passwordField.getText().trim();
    }

    public void disableLoginForm() {
        // update login form
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                usernameField.setEditable(false);
                passwordField.setEditable(false);
                loginButton.setDisable(true);

                Image loadingIcon = new Image(getClass().getResourceAsStream(AppConfiguration.LOADING_ICON));
                final ImageView loadingIconView = new ImageView(loadingIcon);
                int loadingIconSize = 32;
                loadingIconView.setFitHeight(loadingIconSize);
                loadingIconView.setFitWidth(loadingIconSize);

                loginButton.setContentDisplay(ContentDisplay.LEFT);
                loginButton.setGraphic(loadingIconView);
                loginButton.setText(AppConfiguration.LOGIN_IN_PROGRESS_TEXT);
            }
        });
    }

    public void enableLoginForm(
            final ContentDisplay loginBtnContentDisplay,
            final Node loginBtnImageView,
            final String loginText
    ) {
        Platform.runLater(() -> {
            usernameField.setEditable(true);
            passwordField.setEditable(true);
            loginButton.setDisable(false);

            loginButton.setContentDisplay(loginBtnContentDisplay);
            loginButton.setGraphic(loginBtnImageView);
            loginButton.setText(loginText);
        });
    }
}
