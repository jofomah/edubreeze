package com.edubreeze.controllers;

import com.edubreeze.config.AppConfiguration;
import com.edubreeze.model.Student;
import com.edubreeze.model.User;
import com.edubreeze.service.LoginService;
import com.edubreeze.service.exceptions.SyncStillRunningException;
import com.edubreeze.service.tasks.MonitorInternetConnectionTask;
import com.edubreeze.service.tasks.PushScheduledService;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class HeaderController implements Initializable {

    private static final SimpleObjectProperty<User> currentUserProperty = LoginService.getCurrentLoggedInUserProperty();
    private static final SimpleStringProperty appStatusTextProperty = new SimpleStringProperty();
    public static final SimpleBooleanProperty hasWorkingInternetNetworkProperty = MonitorInternetConnectionTask.getConnectionStatusProperty();

    @FXML
    private Label appStatusLabel;

    @FXML
    private ImageView networkStatusImageView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // set current value of network status
        setNetworkStatusIcon(hasWorkingInternetNetworkProperty.get());

        // set current value of app status text
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

        hasWorkingInternetNetworkProperty.addListener(((observable, oldValue, newValue) -> {
            if(newValue == null) {
                Platform.runLater(() -> {
                    Image unknownStatusIcon = new Image(getClass().getResourceAsStream(AppConfiguration.UNKNOWN_NETWORK_STATUS_ICON));

                    networkStatusImageView.setImage(unknownStatusIcon);
                });
                return;
            }

            setNetworkStatusIcon(newValue);

        }));

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

    private void setNetworkStatusIcon(boolean newValue) {
        String imageName = newValue? AppConfiguration.CONNECTION_ON_ICON : AppConfiguration.CONNECTION_OFF_ICON;

        Platform.runLater(() -> {
            Image unknownStatusIcon = new Image(getClass().getResourceAsStream(imageName));

            networkStatusImageView.setImage(unknownStatusIcon);
        });
    }

}
