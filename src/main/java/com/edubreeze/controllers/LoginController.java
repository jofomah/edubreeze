package com.edubreeze.controllers;

import com.edubreeze.config.AppConfiguration;
import com.edubreeze.database.DatabaseConnectionInterface;
import com.edubreeze.database.H2DatabaseConnection;
import com.edubreeze.model.User;
import com.edubreeze.net.ApiClient;
import com.edubreeze.net.exceptions.ApiClientException;
import com.edubreeze.net.exceptions.WrongLoginCredentialsException;
import com.edubreeze.service.LoginService;
import com.edubreeze.service.exceptions.MissingRequiredCredentialsException;
import com.edubreeze.utils.Util;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
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

import java.awt.*;
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void handleLoginButtonClick(MouseEvent event) {
        LoginTask loginTask = new LoginTask(usernameField, passwordField, loginButton);

        loginTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent t) {
                System.out.println("done:" + t.getSource().getValue());
            }
        });

        loginTask.start();

    }

    public static  void disableFormFields(final TextField usernameField, final PasswordField passwordField, final Button loginButton)
    {
        // update login form
        Platform.runLater(new Runnable() {
            @Override public void run() {
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
                loginButton.setText("Logging in ...");
            }
        });
    }

    public static void enableFormFields(
            final TextField usernameField,
            final PasswordField passwordField,
            final Button loginButton,
            final ContentDisplay loginBtnContentDisplay,
            final Node loginBtnImageView,
            final String loginText
    ) {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                usernameField.setEditable(true);
                passwordField.setEditable(true);
                loginButton.setDisable(false);

                loginButton.setContentDisplay(loginBtnContentDisplay);
                loginButton.setGraphic(loginBtnImageView);
                loginButton.setText(loginText);
            }
        });
    }

    private static class LoginTask extends Service<Void> {
        private TextField usernameField;
        private PasswordField passwordField;
        private Button loginButton;

       public LoginTask(TextField usernameField, PasswordField passwordField, Button loginButton)
       {
           this.usernameField = usernameField;
           this.passwordField = passwordField;
           this.loginButton = loginButton;
       }

        protected Task<Void> createTask() {
            final TextField usernameField = this.usernameField;
            final PasswordField passwordField = this.passwordField;
            final Button loginButton = this.loginButton;
            final LoginService loginService = new LoginService(new ApiClient());

            return new Task<Void>() {
                protected Void call() {
                    final String username = usernameField.getText();
                    final String password = passwordField.getText();

                    final ContentDisplay loginBtnContentDisplay = loginButton.getContentDisplay();
                    final String loginBtnText = loginButton.getText();
                    final Node loginIconImage = loginButton.getGraphic();

                    LoginController.disableFormFields(usernameField, passwordField, loginButton);

                    try {

                        String apiToken = loginService.login(username, password);

                        // save apiToken to database
                        User user = loginService.saveValidLogin(username, password, apiToken);

                        Stage stage =(Stage) loginButton.getScene().getWindow();

                        try {
                            System.out.println("change screen");
                            Util.changeScreen(stage, AppConfiguration.STUDENT_PERSONAL_INFO_SCREEN);
                        } catch (IOException e) {
                            Util.showExceptionDialogBox(e, "Change Screen Error", "An error occurred while trying to change from Login screen.");

                        }

                    }
                    catch (SQLException ex) {
                        Util.showExceptionDialogBox(ex, "Database Error", "An error occurred while interacting with app database.");

                    } catch(MissingRequiredCredentialsException ex) {
                        Util.showErrorDialog(
                                "Missing Login Credentials",
                                "Please enter username and/or password",
                                "Please enter username and password, then submit to login."
                        );

                        LoginController.enableFormFields(usernameField, passwordField, loginButton, loginBtnContentDisplay, loginIconImage, loginBtnText);

                    } catch (WrongLoginCredentialsException ex) {
                        // show login failed message
                        Util.showErrorDialog(
                                "Login Failed",
                                "Wrong username and/or Password.",
                                "Please try again with correct username and password, if this persists, contact support"
                        );

                        LoginController.enableFormFields(usernameField, passwordField, loginButton, loginBtnContentDisplay, loginIconImage, loginBtnText);

                    } catch (ApiClientException ex) {
                        // API connection error, check if user is connected locally.
                        try {
                            User user = loginService.loginOffline(username, password);
                            Stage stage =(Stage) loginButton.getScene().getWindow();

                            System.out.println(AppConfiguration.STUDENT_PERSONAL_INFO_SCREEN);

                            Util.changeScreen(stage, AppConfiguration.STUDENT_PERSONAL_INFO_SCREEN);

                        } catch (SQLException e) {
                            Util.showExceptionDialogBox(ex, "Database Error", "An error occurred while interacting with app database.");

                        } catch (WrongLoginCredentialsException e) {
                            Util.showErrorDialog(
                                    "Login Failed",
                                    "Wrong username and/or Password.",
                                    "Please try again with correct username and password, if this persists, contact support"
                            );
                        } catch (IOException e) {
                            Util.showExceptionDialogBox(ex, "Change Screen Error", "An error occurred while trying to change from Login screen.");

                        }

                    } finally {
                        LoginController.enableFormFields(usernameField, passwordField, loginButton, loginBtnContentDisplay, loginIconImage, loginBtnText);
                    }

                    return null;
                }
            };
        }
    }
}
