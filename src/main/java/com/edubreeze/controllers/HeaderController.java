package com.edubreeze.controllers;

import com.edubreeze.config.AppConfiguration;
import com.edubreeze.service.tasks.MonitorInternetConnectionTask;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class HeaderController implements Initializable {

    public static final SimpleStringProperty appStatusTextProperty = new SimpleStringProperty();
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
        if (currentStatusText != null) {
            appStatusLabel.setText(currentStatusText);
            setStatusTextColor(currentStatusText);
        }

        appStatusLabel.textProperty().bind(appStatusTextProperty);

        appStatusTextProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }

            setStatusTextColor(newValue);
        });

        hasWorkingInternetNetworkProperty.addListener(((observable, oldValue, newValue) -> {
            if (newValue == null) {
                Platform.runLater(() -> {
                    Image unknownStatusIcon = new Image(getClass().getResourceAsStream(AppConfiguration.UNKNOWN_NETWORK_STATUS_ICON));

                    networkStatusImageView.setImage(unknownStatusIcon);
                });
                return;
            }

            setNetworkStatusIcon(newValue);

        }));
    }

    private void setStatusTextColor(String newValue) {
        if (newValue.endsWith("successfully")) {
            appStatusLabel.setTextFill(Color.GREEN);

        } else if (newValue.endsWith("running")) {
            appStatusLabel.setTextFill(Color.WHITE);

        } else if (newValue.endsWith("cancelled")) {
            appStatusLabel.setTextFill(Color.GRAY);

        } else if (newValue.endsWith("failed")) {
            appStatusLabel.setTextFill(Color.RED);
        } else {
            appStatusLabel.setTextFill(Color.BLACK);
        }
    }

    private void setNetworkStatusIcon(boolean newValue) {
        String imageName = newValue ? AppConfiguration.CONNECTION_ON_ICON : AppConfiguration.CONNECTION_OFF_ICON;

        Platform.runLater(() -> {
            Image unknownStatusIcon = new Image(getClass().getResourceAsStream(imageName));

            networkStatusImageView.setImage(unknownStatusIcon);
        });
    }

}
