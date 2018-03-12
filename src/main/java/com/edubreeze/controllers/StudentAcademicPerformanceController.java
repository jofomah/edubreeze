package com.edubreeze.controllers;

import com.edubreeze.config.AppConfiguration;
import com.edubreeze.model.Student;
import com.edubreeze.model.StudentAcademicRecord;
import com.edubreeze.model.properties.StudentAcademicRecordProperty;
import com.edubreeze.service.LoginService;
import com.edubreeze.utils.NumberStringFilteredConverter;
import com.edubreeze.utils.Util;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class StudentAcademicPerformanceController implements Initializable {

    @FXML
    private ComboBox<String> termsCombo;

    @FXML
    private ComboBox<String> selectAcademicYearCombo;

    @FXML
    private TextField daysPresentTextField;

    @FXML
    private TextField daysAbsentTextField;

    @FXML
    private Button cancelButton;

    @FXML
    private Button saveAndContinue;

    @FXML
    private ComboBox<String> subjectCombo;

    @FXML
    TextField subjectTextField;

    @FXML
    private TextField caTextField;

    @FXML
    private TextField examTextField;

    @FXML
    private TextField totalTextField;

    @FXML
    private TableView studentAcademiceRecordsTable;

    @FXML
    private Button previousSectionButton;

    @FXML
    private Button nextSectionButton;

    private Student currentStudent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        TableColumn yearCol = new TableColumn("Academic Year");
        yearCol.setCellValueFactory(
                new PropertyValueFactory<StudentAcademicRecordProperty, String>("year"));

        TableColumn termCol = new TableColumn("Term");
        termCol.setCellValueFactory(
                new PropertyValueFactory<StudentAcademicRecordProperty, String>("term"));

        TableColumn daysPresentCol = new TableColumn("Days Present");
        daysPresentCol.setCellValueFactory(
                new PropertyValueFactory<StudentAcademicRecordProperty, Integer>("daysPresent"));

        TableColumn daysAbsentCol = new TableColumn("Days Absent");
        daysAbsentCol.setCellValueFactory(
                new PropertyValueFactory<StudentAcademicRecordProperty, Integer>("daysAbsent"));

        TableColumn subjectCol = new TableColumn("Subject");
        subjectCol.setCellValueFactory(
                new PropertyValueFactory<StudentAcademicRecordProperty, String>("subject"));

        TableColumn caCol = new TableColumn("C.A");
        caCol.setCellValueFactory(
                new PropertyValueFactory<StudentAcademicRecordProperty, Integer>("caScore"));

        TableColumn examCol = new TableColumn("Exam");
        examCol.setCellValueFactory(
                new PropertyValueFactory<StudentAcademicRecordProperty, Integer>("examScore"));

        TableColumn totalCol = new TableColumn("Total");
        totalCol.setCellValueFactory(
                new PropertyValueFactory<StudentAcademicRecordProperty, Integer>("totalScore"));


        studentAcademiceRecordsTable.getColumns().addAll(
                termCol, yearCol, daysPresentCol, daysAbsentCol, subjectCol, caCol, examCol, totalCol
        );

        caTextField.setTextFormatter(NumberStringFilteredConverter.getNumberFormatter());
        examTextField.setTextFormatter(NumberStringFilteredConverter.getNumberFormatter());
        totalTextField.setTextFormatter(NumberStringFilteredConverter.getNumberFormatter());

        caTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            int caScore = (!newValue.isEmpty()) ? Integer.valueOf(newValue) : 0;

            String examValue = examTextField.getText().trim();
            int examScore = (!examValue.isEmpty()) ? Integer.valueOf(examValue) : 0;

            setTotalScore(caScore, examScore, totalTextField);
        });

        examTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            int examScore = (!newValue.isEmpty()) ? Integer.valueOf(newValue) : 0;

            String caValue = caTextField.getText().trim();
            int caScore = (!caValue.isEmpty()) ? Integer.valueOf(caValue) : 0;

            setTotalScore(caScore, examScore, totalTextField);
        });

        subjectCombo.setItems(AppConfiguration.getSubjects());
        subjectCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                subjectTextField.setEditable(false);
                subjectTextField.setText("");
                return;
            }
            if (newValue.equals(AppConfiguration.ADD_SUBJECT_MANUALLY)) {
                subjectTextField.setEditable(true);
                subjectTextField.setText("");
                return;
            }

            subjectTextField.setEditable(false);
            subjectTextField.setText(newValue);
        });

        try {
            currentStudent = AppConfiguration.getCurrentlyEditedStudent();
            if (currentStudent == null) {
                Util.showErrorDialog(
                        "Missing Student Record",
                        "There is no student record existing already",
                        "Please create student's personal record first before adding academic performance data"
                );
                return;
            }

            loadStudentAcademicRecords();

        } catch (SQLException ex) {
            Util.showExceptionDialogBox(ex, "Get Student Record for Edit Error", "An error occurred while trying to fetch student data been edited.");
        }

        termsCombo.setItems(AppConfiguration.getTerms());
        selectAcademicYearCombo.setItems(AppConfiguration.getAcademicSessions());

        daysPresentTextField.setTextFormatter(NumberStringFilteredConverter.getNumberFormatter());

        daysAbsentTextField.setTextFormatter(NumberStringFilteredConverter.getNumberFormatter());

        cancelButton.setOnAction(event -> {
            try {
                Util.changeScreen((Stage) cancelButton.getScene().getWindow(), AppConfiguration.STUDENT_LIST_SCREEN);
            } catch (IOException ex) {
                Util.showExceptionDialogBox(ex, "Change Screen Error", "An error occurred while trying to change from Student Academic screen.");
            }
        });

        saveAndContinue.setOnAction(event -> {
            saveStudentAcademicPerformance();
        });

        previousSectionButton.setOnAction(event -> {
            try {
                Util.changeScreen((Stage) previousSectionButton.getScene().getWindow(), AppConfiguration.STUDENT_PERSONAL_INFO_SCREEN);
            } catch (IOException ex) {
                Util.showExceptionDialogBox(ex, "Change Screen Error", "An error occurred while trying to change from Student Academic screen.");
            }
        });

        nextSectionButton.setOnAction(event -> {
            try {
                Util.changeScreen((Stage) previousSectionButton.getScene().getWindow(), AppConfiguration.STUDENT_BIOMETRIC_SCREEN);
            } catch (IOException ex) {
                Util.showExceptionDialogBox(ex, "Change Screen Error", "An error occurred while trying to change from Student Academic screen.");
            }
        });
    }

    private void loadStudentAcademicRecords() throws SQLException {
        List<StudentAcademicRecord> studentRecords = StudentAcademicRecord.getByStudent(currentStudent);
        List<StudentAcademicRecordProperty> rows = new ArrayList<>();

        for (StudentAcademicRecord studentRecord : studentRecords) {

            StudentAcademicRecordProperty row = new StudentAcademicRecordProperty(
                    new SimpleStringProperty(studentRecord.getSubject()),
                    new SimpleStringProperty(studentRecord.getYear()),
                    new SimpleStringProperty(studentRecord.getTerm()),
                    new SimpleIntegerProperty(studentRecord.getDaysPresent()),
                    new SimpleIntegerProperty(studentRecord.getDaysAbsent()),
                    new SimpleIntegerProperty(studentRecord.getContinuousAssessmentScore()),
                    new SimpleIntegerProperty(studentRecord.getExamScore()),
                    new SimpleIntegerProperty(studentRecord.getTotalScore())
            );

            rows.add(row);
        }

        ObservableList<StudentAcademicRecordProperty> academicRecords = FXCollections.observableArrayList(rows);

        studentAcademiceRecordsTable.setItems(academicRecords);
    }

    private void setTotalScore(int caScore, int examScore, TextField totalTextField) {
        int total = caScore + examScore;

        if (total < 0 || total > 100) {
            Util.showErrorDialog(
                    "Invalid Scores",
                    "Please check the exam and continuous assessment score(s) entered.",
                    "Total score can only be between 0 and 100, Total score entered is: " + total
            );
            totalTextField.setText("");
            return;
        }

        totalTextField.setText(String.valueOf(examScore + caScore));
    }

    public Student saveStudentAcademicPerformance() {

        try {
            String daysPresent = daysPresentTextField.getText().trim();
            String daysAbsent = daysAbsentTextField.getText().trim();
            String caScore = caTextField.getText().trim();
            String examScore = examTextField.getText().trim();
            String totalScore = totalTextField.getText().trim();

            String academicYear = selectAcademicYearCombo.getValue();
            String term = termsCombo.getValue();
            String subject = subjectTextField.getText().trim();
            int daysPresentInt = Integer.parseInt(daysPresent);
            int daysAbsentInt = Integer.parseInt(daysAbsent);
            int caScoreInt = Integer.parseInt(caScore);
            int examScoreInt = Integer.parseInt(examScore);
            int totalScoreInt = Integer.parseInt(totalScore);

            StudentAcademicRecord studentAcademicRecord = new StudentAcademicRecord(
                    term,
                    academicYear,
                    daysPresentInt,
                    daysAbsentInt,
                    subject,
                    caScoreInt,
                    examScoreInt,
                    totalScoreInt
            );

            studentAcademicRecord.setStudent(AppConfiguration.getCurrentlyEditedStudent());

            if (studentAcademicRecord.canSave()) {
                studentAcademicRecord.save(LoginService.getCurrentLoggedInUser());
                clearAcademicRecordFields();
                Util.showInfo(
                        "Student Academic Performance Saved",
                        "Academic performance saved successfully",
                        "Student academic record saved, add another one or click continue to enter Biometric data or exit."
                );
                loadStudentAcademicRecords();
            } else {
                Util.showErrorDialog(
                        "Incomplete Student Academic Record Data",
                        "Missing required academic student fields",
                        "Please enter valid values all fields"
                );
            }

        } catch (NumberFormatException ex) {
            Util.showExceptionDialogBox(ex, "Invalid Number Value", "Please enter only valid integers for C.A, Exams, Days Present and Days Absent.");
        } catch (SQLException ex) {
            Util.showExceptionDialogBox(ex, "Save Student Record Error", "An error occurred while trying to save student's academic data.");
        }

        return null;
    }

    public void clearAcademicRecordFields() {
        subjectTextField.clear();
        termsCombo.getSelectionModel().clearSelection();
        selectAcademicYearCombo.getSelectionModel().clearSelection();
        subjectCombo.getSelectionModel().clearSelection();
        daysAbsentTextField.clear();
        daysPresentTextField.clear();
        caTextField.clear();
        examTextField.clear();
        totalTextField.clear();
    }

}
