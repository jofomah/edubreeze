package com.edubreeze.utils;

import com.edubreeze.config.AppConfiguration;
import com.edubreeze.model.*;
import com.edubreeze.service.enrollment.FingerPrintEnrollment;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Util {

    public static void showViewStudentData(Student student, URL resourceURL) {
        Platform.runLater(() -> {
            try {
                Alert alert = new Alert(Alert.AlertType.NONE);

                // required for NONE alert type to close, it must have at least one button so we add ok button
                alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                alert.setTitle("View Student Data");

                BorderPane rootBorderPane = FXMLLoader.load(resourceURL);
                TabPane tabPane = (TabPane) rootBorderPane.getCenter();
                ObservableList<Tab> tabs = tabPane.getTabs();

                Tab personalDataTab = tabs.get(0);
                GridPane personalDataGridPane = (GridPane) personalDataTab.getContent();
                addPersonalData(student, personalDataGridPane);

                Tab biometricDataTab = tabs.get(2);
                HBox biometricMainHBox = (HBox)biometricDataTab.getContent();
                addBiometricData(student, biometricMainHBox);

                alert.getDialogPane().setContent(rootBorderPane);

                alert.showAndWait();

            } catch (IOException ex) {
                showExceptionDialogBox(ex, "View Student Data Error", "Could not show student data dialog");
            }

        });
    }

    private static void addBiometricData(Student student, HBox biometricMainHBox) {
        byte[] studentImageBytes = student.getStudentImage();
        Image studentImage = null;
        HashMap<FingerPrintEnrollment.FingerType, Image> fingerprintsImage = new HashMap<>();
        if(studentImageBytes != null) {
            try {
                studentImage = ImageUtil.convertToImage(studentImageBytes);
            } catch (IOException ex) {
                Util.showExceptionDialogBox(
                        ex,
                        "Convert Student Image Error",
                        "An error occurred while trying to convert student image."
                );
            }
        }

        List<StudentFingerprint> studentFingerprints = new ArrayList<>(student.getFingerprints());
        for(StudentFingerprint studentFp : studentFingerprints) {

            FingerPrintEnrollment.FingerType key = FingerPrintEnrollment.FingerType.getFromFingerName(studentFp.getFingerType());

            if(key == null) {
                continue;
            }

            try {
                fingerprintsImage.put(key, ImageUtil.convertToImage(studentFp.getFingerprintImageBytes()));
            } catch (IOException ex) {
                Util.showExceptionDialogBox(
                        ex,
                        "Convert Student Fingerprint Image Error",
                        "An error occurred while trying to convert student fingerprint data to image."
                );
            }
        }

        int studentImageVBoxIndex = 0;
        VBox studentImageVBox = (VBox) biometricMainHBox.getChildren().get(studentImageVBoxIndex);
        int imageViewIndex = 1;
        ImageView studentImageView = (ImageView)studentImageVBox.getChildren().get(imageViewIndex);
        if(studentImage != null) {
            studentImageView.setFitHeight(studentImage.getHeight());
            studentImageView.setFitWidth(studentImage.getWidth());
            studentImageView.setImage(studentImage);
        }

        int studentFingerprintVBoxIndex = 1;
        VBox studentFingerprintVBox = (VBox) biometricMainHBox.getChildren().get(studentFingerprintVBoxIndex);

        int currentFinger = 0;

        int currentFingerLabelIndex = 0; // first one
        int currentFingerprintImageIndex = 1; // first one
        int rowGap = 2;

        for(HashMap.Entry<FingerPrintEnrollment.FingerType, Image> fingerprintImage : fingerprintsImage.entrySet()) {

            if(currentFinger < fingerprintsImage.size()) {
                Label currentLabel = (Label) studentFingerprintVBox.getChildren().get(currentFingerLabelIndex);
                currentLabel.setText(currentLabel.getText() + " " + fingerprintImage.getKey().fingerName());

                ImageView currentFingerprintImageView = (ImageView) studentFingerprintVBox.getChildren().get(currentFingerprintImageIndex);
                currentFingerprintImageView.setImage(fingerprintImage.getValue());

                // move to second row from current row in vertical box
                currentFingerLabelIndex += rowGap;
                currentFingerprintImageIndex += rowGap;
                currentFinger++;
            }
        }
    }

    private static void addPersonalData(Student student, GridPane personalDataPane) {
        int secondColumnIndex = 1;
        int fourthColumnIndex = 3;

        int firstRow = 0, secondRow = 1, thirdRow = 2, fourthRow = 3, fifthRow = 4, sixthRow = 5, seventhRow = 6,
                eighthRow = 7, ninthRow = 8, tenthRow = 9, eleventhRow = 10, twevlvethRow = 11;

        // row index ranges from 0 to 11 i.e 12 rows
        personalDataPane.add(new Label(student.getAdmissionNumber()), secondColumnIndex, firstRow);
        personalDataPane.add(new Label(student.getFirstName()), secondColumnIndex, secondRow);
        personalDataPane.add(new Label(student.getLastName()), secondColumnIndex, thirdRow);

        DateFormat dateFormat = new SimpleDateFormat(AppConfiguration.DATE_PATTERN_DD_MM_YYYY);
        personalDataPane.add(new Label(dateFormat.format(student.getDateOfBirth())), secondColumnIndex, fourthRow);

        personalDataPane.add(new Label(student.getGender()), secondColumnIndex, fifthRow);
        personalDataPane.add(new Label(student.getCurrentClass()), secondColumnIndex, sixthRow);
        personalDataPane.add(new Label(student.getClassCategory()), secondColumnIndex, seventhRow);
        personalDataPane.add(new Label(student.getClassSectionType()), secondColumnIndex, eighthRow);
        personalDataPane.add(new Label(student.getClassSection()), secondColumnIndex, ninthRow);
        personalDataPane.add(new Label(student.getSchool().getName()), secondColumnIndex, tenthRow);
        personalDataPane.add(new Label(student.getDateEnrolled()), secondColumnIndex, eleventhRow);
        personalDataPane.add(new Label(student.getContactPersonAddress()), secondColumnIndex, twevlvethRow);

        // add third column values
        personalDataPane.add(new Label(student.getState().getName()), fourthColumnIndex, firstRow);
        personalDataPane.add(new Label(student.getLga().getName()), fourthColumnIndex, secondRow);
        personalDataPane.add(new Label(student.getContactPersonName()), fourthColumnIndex, thirdRow);
        personalDataPane.add(new Label(student.getContactPersonPhoneNumber()), fourthColumnIndex, fourthRow);
        personalDataPane.add(new Label(student.getReligion()), fourthColumnIndex, fifthRow);
        personalDataPane.add(new Label(student.getPreviousSchool()), fourthColumnIndex, sixthRow);
        personalDataPane.add(new Label(student.getClassPassedAtPreviousSchool()), fourthColumnIndex, seventhRow);
        personalDataPane.add(new Label(student.getIncomingTransferCertNo()), fourthColumnIndex, eighthRow);
        personalDataPane.add(new Label(student.getOutgoingTransferCertNo()), fourthColumnIndex, ninthRow);
        personalDataPane.add(new Label(student.getDateOfLeaving()), fourthColumnIndex, tenthRow);
        personalDataPane.add(new Label(student.getCauseOfLeaving()), fourthColumnIndex, eleventhRow);
        personalDataPane.add(new Label(student.getOccupationAfterLeaving()), fourthColumnIndex, twevlvethRow);
    }

    public static void showInfo(String title, String headerText, String contentText) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(headerText);
            alert.setContentText(contentText);

            alert.showAndWait();
        });
    }

    public static void showErrorDialog(final String title, final String headerText, final String contentBody) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(headerText);
            alert.setContentText(contentBody);

            alert.showAndWait();
        });
    }

    public static void showExceptionDialogBox(
            final Throwable ex,
            final String title,
            final String headerText
    ) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(headerText);
            alert.setContentText(ex.getMessage());


            // Create expandable Exception.
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();

            Label label = new Label("The exception stacktrace was:");

            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);

            // Set expandable Exception into the dialog pane.
            alert.getDialogPane().setExpandableContent(expContent);

            alert.showAndWait();
        });
    }

    public static void changeScreen(final Stage stage, final String screenFxmlResourcePath) throws IOException {
        final Parent root = FXMLLoader.load(Util.class.getClass().getResource(screenFxmlResourcePath));
        Platform.runLater(() -> {
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        });
    }

    public static School showSelectSchoolDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("School Selection Dialog Box");
        alert.setHeaderText("Select school");

        School selectedSchool = null;

        GridPane gridContentPane = new GridPane();

        try {

            ComboBox selectStateCombo = new ComboBox();
            selectStateCombo.setPromptText("Please select state ...");
            selectStateCombo.getItems().addAll(State.getStates());

            ComboBox selectLgaCombo = new ComboBox();
            selectLgaCombo.setPromptText("Please select LGA ...");

            ComboBox selectSchoolCombo = new ComboBox();
            selectSchoolCombo.setPromptText("Please select school ...");

            selectStateCombo.setConverter(new StateStringConverter());
            selectSchoolCombo.setConverter(new SchoolStringConverter());
            selectLgaCombo.setConverter(new LgaStringConverter());

            selectStateCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                State selectedState = ((State) newVal);
                if (selectedState == null) {
                    // set selected LGA to
                    selectLgaCombo.getItems().clear();
                    return;
                }

                //set LGA list
                selectLgaCombo.setItems(FXCollections.observableList(new ArrayList<Lga>(selectedState.getLgas())));

                selectSchoolCombo.setItems(FXCollections.observableList(new ArrayList<School>()));
            });


            selectLgaCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                Lga selectedLga = ((Lga) newVal);
                if (selectedLga == null) {
                    // set selected LGA to nothing
                    selectSchoolCombo.getItems().clear();
                    return;
                }

                selectSchoolCombo.setItems(FXCollections.observableList(new ArrayList<>(selectedLga.getSchools())));
            });

            gridContentPane.add(new Label("State:"), 0, 0);
            gridContentPane.add(selectStateCombo, 1, 0);

            gridContentPane.add(new Label("LGA:"), 0, 1);
            gridContentPane.add(selectLgaCombo, 1, 1);

            gridContentPane.add(new Label("School:"), 0, 2);
            gridContentPane.add(selectSchoolCombo, 1, 2);

            alert.getDialogPane().setContent(gridContentPane);

            alert.setResizable(true);
            alert.getDialogPane().setPrefSize(350, 350);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                if (selectSchoolCombo.getValue() != null) {
                    selectedSchool = (School) selectSchoolCombo.getValue();
                }
            }

        } catch (Exception ex) {
            Util.showExceptionDialogBox(
                    ex,
                    "School selection dialog box error",
                    "An Error occurred while trying to show school selection dialog box"
            );
        }

        return selectedSchool;
    }
}
