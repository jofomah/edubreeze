package com.edubreeze.controllers;

import com.edubreeze.config.AppConfiguration;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class FooterController implements Initializable {
    
    @FXML
    private Label footerLabel;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        footerLabel.setText("EduBreeze copyright 2018 | version " + AppConfiguration.VERSION_NO);
    }    
}
