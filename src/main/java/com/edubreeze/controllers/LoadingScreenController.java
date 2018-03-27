package com.edubreeze.controllers;

import com.edubreeze.config.AppConfiguration;
import com.edubreeze.database.H2DatabaseConnection;
import com.edubreeze.model.User;
import com.edubreeze.net.ApiClient;
import com.edubreeze.net.exceptions.ApiClientException;
import com.edubreeze.service.ApplicationService;
import com.edubreeze.service.LoginService;
import com.edubreeze.utils.ExceptionTracker;
import com.edubreeze.utils.Util;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoadingScreenController implements Initializable  {

    @FXML
    private ProgressBar loadingProgressBar;

    @FXML
    private Label progressText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progressText.setText("Loading application data from server ...");

        Task<Boolean> task = createSetupTask();
        new Thread(task).start();
    }

    private Task<Boolean> createSetupTask() {
        User user = LoginService.getCurrentLoggedInUser();
        final ApplicationService applicationService = new ApplicationService(user.getApiToken(), new ApiClient());

        Task task = new Task<Boolean>() {
            @Override
            public Boolean call() throws Exception {

                try {
                    Boolean result = applicationService.loadStates(loadingProgressBar, progressText, new H2DatabaseConnection(AppConfiguration.getDatabaseFileUrl()));

                    if (isCancelled() || result == false) {
                        return false;
                    }

                    Stage stage =(Stage) loadingProgressBar.getScene().getWindow();
                    Util.changeScreen(stage, AppConfiguration.STUDENT_LIST_SCREEN);
                    return true;

                } catch(ApiClientException ex) {
                    ExceptionTracker.track(ex);
                }

                return false;
            }
        };

        return task;
    }
}

