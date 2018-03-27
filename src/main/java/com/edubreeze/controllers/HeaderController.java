package com.edubreeze.controllers;

import com.edubreeze.model.Student;
import com.edubreeze.model.User;
import com.edubreeze.service.LoginService;
import com.edubreeze.service.exceptions.SyncStillRunningException;
import com.edubreeze.service.tasks.PushScheduledService;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class HeaderController implements Initializable {

    private static final SimpleObjectProperty<User> currentUserProperty = LoginService.getCurrentLoggedInUserProperty();
    private static final SimpleStringProperty appStatusTextProperty = new SimpleStringProperty();

    @FXML
    private Label appStatusLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String currentStatusText = appStatusTextProperty.get();
        if(currentStatusText != null) {
            appStatusLabel.setText(currentStatusText);
            setStatusTextColor(currentStatusText);
        }

        appStatusLabel.textProperty().bind(appStatusTextProperty);

        appStatusTextProperty.addListener((observable, oldValue, newValue) -> {
            if(newValue == null) {
                return;
            }

            setStatusTextColor(newValue);
        });

        currentUserProperty.addListener((observable, oldValue, newValue) -> {

            if (newValue != null) {
                try {
                    List<Student> studentsDueForSync = Student.getStudentsDueForSync();
                    PushScheduledService pushService = PushScheduledService.getPushService(newValue.getApiToken(), studentsDueForSync);

                    pushService.setOnSucceeded(result -> {
                        Platform.runLater(() -> {
                            appStatusTextProperty.set("Push completed successfully");
                        });
                    });

                    pushService.setOnRunning(status -> {
                        Platform.runLater(() -> {
                            appStatusTextProperty.set("Push students currently running");
                        });
                    });

                    pushService.setOnCancelled(status -> {
                        Platform.runLater(() -> {
                            appStatusTextProperty.set("Push students cancelled");
                        });
                    });

                    pushService.setOnFailed(state -> {
                        Platform.runLater(() -> {
                            appStatusTextProperty.set("Push students failed");
                        });
                    });

                    if (!pushService.isRunning()) {
                        pushService.setDelay(Duration.minutes(1));
                        pushService.restart();

                        Platform.runLater(() -> {
                            appStatusTextProperty.set("Push students sync started");
                        });
                    }

                } catch (SQLException | SyncStillRunningException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void setStatusTextColor(String newValue) {
        if(newValue.endsWith("successfully")) {
            appStatusLabel.setTextFill(Color.GREEN);

        } else if(newValue.endsWith("running")) {
            appStatusLabel.setTextFill(Color.WHITE);

        } else if(newValue.endsWith("cancelled")) {
            appStatusLabel.setTextFill(Color.GRAY);

        } else if(newValue.endsWith("failed")) {
            appStatusLabel.setTextFill(Color.RED);
        }
    }

}
