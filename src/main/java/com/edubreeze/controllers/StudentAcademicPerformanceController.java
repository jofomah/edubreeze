package com.edubreeze.controllers;

import com.edubreeze.config.AppConfiguration;
import com.edubreeze.model.AcademicRecord;
import com.edubreeze.model.Student;
import com.edubreeze.model.StudentAcademicTerm;
import com.edubreeze.model.properties.AcademicRecordProperty;
import com.edubreeze.model.properties.StudentAcademicTermProperty;
import com.edubreeze.service.LoginService;
import com.edubreeze.utils.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
    private TableView studentAcademicRecordsTable;

    @FXML
    private Button previousSectionButton;

    @FXML
    private Button nextSectionButton;

    @FXML
    private Button saveTermButton;

    @FXML
    private ComboBox<StudentAcademicTerm> academicTermCombo;

    @FXML
    private TableView studentTermTable;

    private Student currentStudent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        academicTermCombo.setConverter(new StudentAcademicTermStringConverter());

        studentAcademicRecordsTable.getColumns().addAll(AcademicRecordProperty.getAcademicRecordsTableColumns());

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
            ExceptionTracker.track(ex);
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
                ExceptionTracker.track(ex);
                Util.showExceptionDialogBox(ex, "Change Screen Error", "An error occurred while trying to change from Student Academic screen.");
            }
        });

        saveTermButton.setOnAction(event -> saveStudentAcademicTerm());

        saveAndContinue.setOnAction(event -> {
            saveAcademicRecord();
        });

        previousSectionButton.setOnAction(event -> {
            try {
                Util.changeScreen((Stage) previousSectionButton.getScene().getWindow(), AppConfiguration.STUDENT_PERSONAL_INFO_SCREEN);
            } catch (IOException ex) {
                ExceptionTracker.track(ex);
                Util.showExceptionDialogBox(ex, "Change Screen Error", "An error occurred while trying to change from Student Academic screen.");
            }
        });

        nextSectionButton.setOnAction(event -> {
            try {
                Util.changeScreen((Stage) previousSectionButton.getScene().getWindow(), AppConfiguration.STUDENT_BIOMETRIC_SCREEN);
            } catch (IOException ex) {
                ExceptionTracker.track(ex);
                Util.showExceptionDialogBox(ex, "Change Screen Error", "An error occurred while trying to change from Student Academic screen.");
            }
        });
    }

    private void saveStudentAcademicTerm() {
        String term = termsCombo.getValue();
        String academicYear = selectAcademicYearCombo.getValue();
        String daysPresent = daysPresentTextField.getText().trim();
        String daysAbsent = daysAbsentTextField.getText().trim();

        int daysPresentInt = Integer.parseInt(daysPresent);
        int daysAbsentInt = Integer.parseInt(daysAbsent);

        StudentAcademicTerm studentAcademicTerm = new StudentAcademicTerm(term, academicYear, daysPresentInt, daysAbsentInt);
        studentAcademicTerm.setStudent(currentStudent);
        if(studentAcademicTerm.canSave()) {
            try {
                studentAcademicTerm.save(LoginService.getCurrentLoggedInUser());
                clearAcademicTermFields();
                loadStudentAcademicRecords();
            } catch (SQLException ex) {
                ExceptionTracker.track(ex);
                Util.showExceptionDialogBox(ex, "Save Student Academic Term Error", ex.getMessage());
            }
        }else {
            Util.showErrorDialog(
                    "Invalid Student Academic Term Error",
                    "Missing required field(s)",
                    "Please fill all academic term fields before saving."
            );
        }
    }

    private void loadStudentAcademicRecords() throws SQLException {
        loadAcademicTermsTable();
        // initialize academic term drop down
        List<StudentAcademicTerm> studentAcademicTerms = new ArrayList<>(currentStudent.getAcademicTerms());
        academicTermCombo.setItems(FXCollections.observableList(studentAcademicTerms));

        List<AcademicRecord> academicRecords = AcademicRecord.getByStudent(currentStudent);

        studentAcademicRecordsTable.setItems(DataUtil.convertToAcademicRecordTableRowData(academicRecords));
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

    public void saveAcademicRecord() {

        try {
            String caScore = caTextField.getText().trim();
            String examScore = examTextField.getText().trim();
            String totalScore = totalTextField.getText().trim();

            StudentAcademicTerm academicTerm = academicTermCombo.getValue();
            String subject = subjectTextField.getText().trim();
            int caScoreInt = Integer.parseInt(caScore);
            int examScoreInt = Integer.parseInt(examScore);
            int totalScoreInt = Integer.parseInt(totalScore);

            AcademicRecord academicRecord = new AcademicRecord(
                    academicTerm,
                    subject,
                    caScoreInt,
                    examScoreInt,
                    totalScoreInt
            );

            if (academicRecord.canSave()) {
                academicRecord.save(LoginService.getCurrentLoggedInUser());
                clearAcademicRecordFields();
                Util.showInfo(
                        "Student Academic Record Saved",
                        "Academic record saved successfully",
                        "Student academic record saved, add another one or click continue to enter Biometric data or exit."
                );
                loadStudentAcademicRecords();
            } else {
                Util.showErrorDialog(
                        "Incomplete Student Academic Record Data",
                        "Missing required academic record fields",
                        "Please enter valid values all fields"
                );
            }

        } catch (NumberFormatException ex) {
            ExceptionTracker.track(ex);
            Util.showExceptionDialogBox(ex, "Invalid Number Value", "Please enter only valid integers for C.A, Exams, Days Present and Days Absent.");
        } catch (SQLException ex) {
            ExceptionTracker.track(ex);
            Util.showExceptionDialogBox(ex, "Save Student Record Error", "An error occurred while trying to save student's academic data.");
        }
    }

    public void clearAcademicTermFields() {
        termsCombo.getSelectionModel().clearSelection();
        selectAcademicYearCombo.getSelectionModel().clearSelection();
        daysAbsentTextField.clear();
        daysPresentTextField.clear();
    }

    public void clearAcademicRecordFields() {
        subjectTextField.clear();
        subjectCombo.getSelectionModel().clearSelection();
        caTextField.clear();
        examTextField.clear();
        totalTextField.clear();
    }

    public void loadAcademicTermsTable() {
        studentTermTable.getColumns().clear();
        studentTermTable.getColumns().addAll(StudentAcademicTermProperty.getAcademicTermTableColumns());

        List<StudentAcademicTerm> academicTerms = new ArrayList<>(currentStudent.getAcademicTerms());
        ObservableList<StudentAcademicTermProperty> academicTermsProperty = DataUtil.convertToAcademicTermTableRowData(academicTerms);

        studentTermTable.setItems(academicTermsProperty);
    }

}
