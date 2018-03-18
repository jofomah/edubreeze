package com.edubreeze.controllers;

import com.edubreeze.config.AppConfiguration;
import com.edubreeze.model.Lga;
import com.edubreeze.model.School;
import com.edubreeze.model.State;
import com.edubreeze.model.Student;
import com.edubreeze.service.LoginService;
import com.edubreeze.utils.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class StudentPersonalInfoController implements Initializable {

    private final ToggleGroup genderGroup = new ToggleGroup();

    @FXML
    private TextField admissionNoTextField;

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private RadioButton genderMaleRadioButton;

    @FXML
    private RadioButton genderFemaleRadioButton;

    @FXML
    private DatePicker dobDatePicker;

    @FXML
    private ComboBox<String> classComboBox;

    @FXML
    private ComboBox<School> schoolComboBox;

    @FXML
    private ComboBox<String> dateEnrolledCombo;

    @FXML
    private TextField addressTextField;

    @FXML
    private ComboBox lgaOfOriginComboBox;

    @FXML
    private ComboBox stateOfOriginComboBox;

    @FXML
    private TextField classCategoryTextField;

    @FXML
    private TextField classSectionTextField;

    @FXML
    private ComboBox classSectionTypeComboBox;

    @FXML
    private TextField parentGuardianTextField;

    @FXML
    private TextField parentGuardianPhoneTextField;

    @FXML
    private ComboBox religionComboBox;

    @FXML
    private TextField previousSchoolTextField;

    @FXML
    private TextField classPassedAtPreviousSchoolTextField;

    @FXML
    private TextField incomingTransferCertNoTextField;

    @FXML
    private TextField outgoingTransferCertNoTextField;

    @FXML
    private ComboBox<String> dateOfLeavingCombo;

    @FXML
    private TextField causeOfLeavingTextField;

    @FXML
    private TextField occupationAfterLeavingTextField;

    @FXML
    private Button cancelButton;

    @FXML
    private Button saveAndContinueButton;

    @FXML
    private Button nextSectionButton;

    private School currentSelectedSchool = null;
    private Student currentStudent = null;

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        stateOfOriginComboBox.setConverter(new StateStringConverter());
        schoolComboBox.setConverter(new SchoolStringConverter());
        lgaOfOriginComboBox.setConverter(new LgaStringConverter());

        /**
         * set school selection to last selected school to avoid selecting school while entering data
         * at a given school
         */
        try {
            currentSelectedSchool = AppConfiguration.getCurrentSchool();

            schoolComboBox.setItems(FXCollections.observableArrayList(currentSelectedSchool));
            schoolComboBox.getSelectionModel().select(0);

        } catch (SQLException ex) {
            Util.showExceptionDialogBox(ex, "SQL Error", "Error occurred while getting current selected school...");
        }


        try {
            currentStudent = AppConfiguration.getCurrentlyEditedStudent();
            if (currentStudent != null) {
                loadFormWithCurrentStudentData(currentStudent);
            }

        } catch (SQLException ex) {
            Util.showExceptionDialogBox(ex, "Get Student Record for Edit Error", "An error occurred while trying to fetch student data been edited.");
        }

        try {
            stateOfOriginComboBox.setItems(FXCollections.observableList(State.getStates()));
        } catch (SQLException ex) {
            Util.showExceptionDialogBox(ex, "SQL Error", "Error occurred while getting state list...");
        }


        stateOfOriginComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            lgaOfOriginComboBox.getSelectionModel().clearSelection();
            if (newVal == null) {
                lgaOfOriginComboBox.setItems(FXCollections.observableList(new ArrayList<Lga>()));
                return;
            }
            State selectedState = (State) newVal;
            lgaOfOriginComboBox.setItems(FXCollections.observableList(new ArrayList<Lga>(selectedState.getLgas())));
        });

        dateEnrolledCombo.setItems(AppConfiguration.getAcademicSessions());
        dateOfLeavingCombo.setItems(AppConfiguration.getAcademicSessions());
        classComboBox.setItems(AppConfiguration.getStudentClassList());
        classSectionTypeComboBox.setItems(AppConfiguration.getStudentClassSectionTypes());

        genderFemaleRadioButton.setToggleGroup(genderGroup);
        genderMaleRadioButton.setToggleGroup(genderGroup);

        dobDatePicker.setConverter(getDateStringConverter());

        schoolComboBox.setOnMouseClicked(event -> {
            School selectedSchool = Util.showSelectSchoolDialog();
            if (selectedSchool != null) {
                // set school combo to school select from dialog
                AppConfiguration.setCurrentSchoolId(selectedSchool.getId());
                schoolComboBox.setItems(FXCollections.observableArrayList(selectedSchool));
                schoolComboBox.getSelectionModel().select(0);
            }
        });

        religionComboBox.setItems(AppConfiguration.getReligionList());

        cancelButton.setOnAction(event -> {
            try {
                Util.changeScreen((Stage) cancelButton.getScene().getWindow(), AppConfiguration.STUDENT_LIST_SCREEN);
            } catch (IOException ex) {
                Util.showExceptionDialogBox(ex, "Change Screen Error", "An error occurred while trying to change from StudentPersonalInfo.");
            }
        });

        nextSectionButton.setOnAction(event -> {
            if(currentStudent != null && currentStudent.canSavePersonalInfo() && currentStudent.getAutoId() != null) {
                try {
                    Util.changeScreen((Stage) cancelButton.getScene().getWindow(), AppConfiguration.STUDENT_ACADEMIC_PERFORMANCE_SCREEN);
                } catch (IOException ex) {
                    Util.showExceptionDialogBox(ex, "Change Screen Error", "An error occurred while trying to change from StudentPersonalInfo.");
                }
            } else {
                Util.showInfo(
                        "Incomplete/Unsaved Student Data Error",
                        "Student Personal Data is Not Complete",
                        "Please, complete and save student personal information before moving to next section."
                );
            }
        });
    }

    @FXML
    public void handleSaveAndContinue(ActionEvent event) {
        try {

            Student student = (currentStudent != null)? currentStudent : new Student();

            String admissionNumber = admissionNoTextField.getText().trim();
            student.setAdmissionNumber(admissionNumber);

            String firstName = firstNameTextField.getText().trim();
            student.setFirstName(firstName);

            String lastName = lastNameTextField.getText().trim();
            student.setLastName(lastName);

            Date dob = getDateFrom(dobDatePicker.getValue());
            student.setDateOfBirth(dob);

            String gender = getGender();
            student.setGender(gender);

            String currentClass = getComboBoxValue(classComboBox);
            student.setCurrentClass(currentClass);

            String classCategory = classCategoryTextField.getText().trim();
            student.setClassCategory(classCategory);

            String classSection = classSectionTextField.getText().trim();
            student.setClassSection(classSection);

            String classSectionType = getComboBoxValue(classSectionTypeComboBox);
            student.setClassSectionType(classSectionType);

            School school = schoolComboBox.getValue();
            student.setSchool(school);

            String dateEnrolled = getComboBoxValue(dateEnrolledCombo);
            student.setDateEnrolled(dateEnrolled);

            String address = addressTextField.getText().trim();
            student.setContactPersonAddress(address);

            State stateOfOrigin = (stateOfOriginComboBox.getValue() != null) ? (State) stateOfOriginComboBox.getValue() : null;
            student.setState(stateOfOrigin);

            Lga lgaOfOrigin = (lgaOfOriginComboBox.getValue() != null) ? (Lga) lgaOfOriginComboBox.getValue() : null;
            student.setLga(lgaOfOrigin);

            String parentOrGuardianName = parentGuardianTextField.getText().trim();
            student.setContactPersonName(parentOrGuardianName);

            String parentOrGuardianPhoneNo = parentGuardianPhoneTextField.getText().trim();
            student.setContactPersonPhoneNumber(parentOrGuardianPhoneNo);

            String religion = getComboBoxValue(religionComboBox);
            student.setReligion(religion);

            // optional fields
            String previousSchool = previousSchoolTextField.getText().trim();
            student.setPreviousSchool(previousSchool);

            String classPassedAtPreviousSchool = classPassedAtPreviousSchoolTextField.getText().trim();
            student.setClassPassedAtPreviousSchool(classPassedAtPreviousSchool);

            String incomingTransferCertNo = incomingTransferCertNoTextField.getText().trim();
            student.setIncomingTransferCertNo(incomingTransferCertNo);

            String outgoingTransferCertNo = outgoingTransferCertNoTextField.getText().trim();
            student.setOutgoingTransferCertNo(outgoingTransferCertNo);

            String dateOfLeaving = getComboBoxValue(dateOfLeavingCombo);
            student.setDateOfLeaving(dateOfLeaving);

            String causeOfLeaving = causeOfLeavingTextField.getText().trim();
            student.setCauseOfLeaving(causeOfLeaving);

            String occupationAfterLeaving = occupationAfterLeavingTextField.getText().trim();
            student.setOccupationAfterLeaving(occupationAfterLeaving);

            if (student.canSavePersonalInfo()) {

                // save
                student.save(LoginService.getCurrentLoggedInUser());
                AppConfiguration.setCurrentlyEditedStudentId(student.getAutoId());
                Util.changeScreen((Stage) saveAndContinueButton.getScene().getWindow(), AppConfiguration.STUDENT_ACADEMIC_PERFORMANCE_SCREEN);

            } else {
                // check invalid fields and highlight
                Util.showErrorDialog(
                        "Student Data Validation Error",
                        "Incomplete student data",
                        "Please fill in all required student data, marked with *."
                );
            }

        } catch (Exception ex) {
            Util.showExceptionDialogBox(ex, "Save Student Record Error", "An error occurred while trying to save student data.");
        }

    }

    private Date getDateFrom(LocalDate localDate) throws ParseException {
        Date date = null;
        if (localDate != null) {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(localDate.toString());
        }
        return date;

    }

    public String getComboBoxValue(ComboBox comboBox) {
        Object value = comboBox.getValue();
        return (value == null) ? null : value.toString();
    }

    private String getGender() {
        RadioButton selectedGenderRadioButton = (RadioButton) genderGroup.getSelectedToggle();
        if (selectedGenderRadioButton != null) {
            return selectedGenderRadioButton.getText();
        }
        return null;
    }

    private StringConverter<LocalDate> getDateStringConverter() {
        return new StringConverter<LocalDate>() {
            private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public String toString(LocalDate localDate) {
                if (localDate == null)
                    return "";
                return dateTimeFormatter.format(localDate);
            }

            @Override
            public LocalDate fromString(String dateString) {
                if (dateString == null || dateString.trim().isEmpty()) {
                    return null;
                }
                return LocalDate.parse(dateString, dateTimeFormatter);
            }
        };
    }

    private void loadFormWithCurrentStudentData(Student student) {
        admissionNoTextField.setText(student.getAdmissionNumber());
        firstNameTextField.setText(student.getFirstName());
        lastNameTextField.setText(student.getLastName());
        dobDatePicker.setValue(DateUtil.convertDate(student.getDateOfBirth()));

        if(student.getGender().equalsIgnoreCase("MALE")) {
            genderMaleRadioButton.setSelected(true);
        } else if (student.getGender().equalsIgnoreCase("FEMALE")){
            genderFemaleRadioButton.setSelected(true);
        }

        classComboBox.getSelectionModel().select(student.getCurrentClass());
        classCategoryTextField.setText(student.getClassCategory());
        classSectionTextField.setText(student.getClassSection());
        classSectionTypeComboBox.getSelectionModel().select(student.getClassSectionType());

        ObservableList<School> schools = FXCollections.observableArrayList(student.getSchool());
        schoolComboBox.setItems(schools);
        System.out.println("School : " + student.getSchool().getName() + ", size : " + schools.size());
        schoolComboBox.getSelectionModel().select(student.getSchool());

        schoolComboBox.getSelectionModel().select(student.getSchool());
        dateEnrolledCombo.getSelectionModel().select(student.getDateEnrolled());
        addressTextField.setText(student.getContactPersonAddress());
        stateOfOriginComboBox.getSelectionModel().select(student.getState());
        lgaOfOriginComboBox.getSelectionModel().select(student.getLga());
        parentGuardianTextField.setText(student.getContactPersonName());
        parentGuardianPhoneTextField.setText(student.getContactPersonPhoneNumber());
        religionComboBox.getSelectionModel().select(student.getReligion());
        previousSchoolTextField.setText(student.getPreviousSchool());
        classPassedAtPreviousSchoolTextField.setText(student.getClassPassedAtPreviousSchool());
        incomingTransferCertNoTextField.setText(student.getIncomingTransferCertNo());
        outgoingTransferCertNoTextField.setText(student.getOutgoingTransferCertNo());
        dateOfLeavingCombo.getSelectionModel().select(student.getDateOfLeaving());
        causeOfLeavingTextField.setText(student.getCauseOfLeaving());
        occupationAfterLeavingTextField.setText(student.getOccupationAfterLeaving());
    }
}
