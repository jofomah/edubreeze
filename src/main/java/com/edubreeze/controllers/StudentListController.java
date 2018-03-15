package com.edubreeze.controllers;

import com.edubreeze.config.AppConfiguration;
import com.edubreeze.model.Lga;
import com.edubreeze.model.School;
import com.edubreeze.model.State;
import com.edubreeze.model.Student;
import com.edubreeze.utils.DateUtil;
import com.edubreeze.utils.Util;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class StudentListController implements Initializable {

    @FXML
    private Button addStudentButton;

    @FXML
    private ComboBox selectStateComboBox;

    @FXML
    private ComboBox selectLgaCombox;

    @FXML
    private ComboBox selectSchoolComboBox;

    @FXML
    private TextField searchStudentTextField;

    @FXML
    private Button searchStudentButton;

    @FXML
    private GridPane contentGridPane;

    private List<Student> students = new ArrayList<>();

    private TableView studentListTableView = new TableView();

    private final static int ROWS_PER_PAGE = 100;


    public void initialize(URL location, ResourceBundle resources) {
        try {
            selectStateComboBox.setItems(FXCollections.observableList(State.getStates()));

            selectStateComboBox.setConverter(new StringConverter() {
                @Override
                public String toString(Object object) {
                    return (object == null) ? "" : ((State) object).getName();
                }

                @Override
                public Object fromString(String string) {
                    return null;
                }
            });

            selectStateComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                State selectedState = ((State) newVal);
                if (selectedState == null) {
                    // set selected LGA to nothing
                    selectLgaCombox.getItems().clear();
                    return;
                }

                //set LGA list
                selectLgaCombox.setItems(FXCollections.observableList(new ArrayList<Lga>(selectedState.getLgas())));

                selectSchoolComboBox.getItems().clear();
            });

            selectLgaCombox.setConverter(new StringConverter() {
                @Override
                public String toString(Object object) {
                    return (object == null) ? "" : ((Lga) object).getName();
                }

                @Override
                public Object fromString(String string) {
                    return null;
                }
            });

            selectLgaCombox.valueProperty().addListener((obs, oldVal, newVal) -> {
                Lga selectedLga = ((Lga) newVal);
                if (selectedLga == null) {
                    // set selected LGA to nothing
                    selectSchoolComboBox.getItems().clear();
                    return;
                }

                selectSchoolComboBox.setItems(FXCollections.observableList(new ArrayList<School>(selectedLga.getSchools())));
            });

            selectSchoolComboBox.setConverter(new StringConverter() {
                @Override
                public String toString(Object object) {
                    return (object == null) ? "" : ((School) object).getName();
                }

                @Override
                public Object fromString(String string) {
                    return null;
                }
            });

            students.clear();
            students.addAll(Student.getAll());

        } catch (Exception ex) {

        }

        setPagination();

        initializeStudentTable();

        addStudentButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Util.changeScreen((Stage) addStudentButton.getScene().getWindow(), AppConfiguration.STUDENT_PERSONAL_INFO_SCREEN);
                } catch (IOException ex) {
                    Util.showExceptionDialogBox(ex, "Change Screen Error", "An error occurred while trying to change from Login screen.");
                }
            }
        });
    }

    private void initializeStudentTable() {
        studentListTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn firstNameCol = new TableColumn("First Name");
        firstNameCol.setCellValueFactory(
                new PropertyValueFactory<Person, String>("firstName"));

        TableColumn lastNameCol = new TableColumn("Last Name");
        lastNameCol.setCellValueFactory(
                new PropertyValueFactory<Person, String>("lastName"));

        TableColumn ageCol = new TableColumn("Age");
        ageCol.setCellValueFactory(
                new PropertyValueFactory<Person, Integer>("age"));

        TableColumn genderCol = new TableColumn("Gender");
        genderCol.setCellValueFactory(
                new PropertyValueFactory<Person, String>("gender"));

        TableColumn classCol = new TableColumn("Class");
        classCol.setCellValueFactory(
                new PropertyValueFactory<Person, String>("studentClass"));

        TableColumn schoolCol = new TableColumn("School");
        schoolCol.setCellValueFactory(
                new PropertyValueFactory<Person, String>("school"));

        TableColumn lgaCol = new TableColumn("LGA of Origin");
        lgaCol.setCellValueFactory(
                new PropertyValueFactory<Person, String>("lga"));

        studentListTableView.getColumns().addAll(
                firstNameCol,
                lastNameCol,
                ageCol,
                genderCol,
                classCol,
                schoolCol,
                lgaCol
        );

    }

    private Node createPage(int pageIndex) {

        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, students.size());

        List<Person> studentDataModel = new ArrayList<>();
        List<Student> studentSubList = students.subList(fromIndex, toIndex);

        for (Student student : studentSubList) {

            Lga originLga = null;
            School school = null;
            try {
                originLga = Lga.find(student.getLga().getId());
                school = School.find(student.getSchool().getId());

            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            Person personData = new Person(
                    student.getAdmissionNumber(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getDateOfBirth(),
                    student.getGender(),
                    student.getCurrentClass(),
                    student.getDateEnrolled(),
                    (school != null) ? school.getName() : "N/A",
                    (originLga != null) ? originLga.getName() : "N/A"
            );

            studentDataModel.add(personData);
        }

        studentListTableView.setItems(FXCollections.observableList(studentDataModel));

        return new BorderPane(studentListTableView);
    }

    private Pagination setPagination() {
        Pagination pagination = new Pagination((students.size() / ROWS_PER_PAGE + 1), 0);
        pagination.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageIndex) {
                return createPage(pageIndex);
            }
        });

        contentGridPane.add(pagination, 0, 3, GridPane.REMAINING, 1);
        return pagination;
    }

    public void handleSearchButtonClick(MouseEvent event) {
        State state = null;
        if (selectStateComboBox.getValue() != null) {
            state = (State) selectStateComboBox.getValue();
        }

        Lga lga = null;
        if (selectLgaCombox.getValue() != null) {
            lga = (Lga) selectLgaCombox.getValue();
        }

        School school = null;
        if (selectSchoolComboBox.getValue() != null) {
            school = (School) selectSchoolComboBox.getValue();
        }

        String keyword = searchStudentTextField.getText().trim();

        try {
            List<Student> studentSearchResult = Student.searchBy(state, lga, school, keyword);
            students.clear();
            students.addAll(studentSearchResult);
        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        }

        setPagination();
    }


    public static class Person {

        private final SimpleStringProperty admissionNumber;
        private final SimpleStringProperty firstName;
        private final SimpleStringProperty lastName;
        private final SimpleStringProperty gender;
        private final SimpleStringProperty dateOfBirth;
        private final SimpleStringProperty studentClass;
        private final SimpleStringProperty enrollmentDate;
        private final SimpleStringProperty school;
        private final SimpleStringProperty lga;
        private final SimpleIntegerProperty age;

        private Person(String admNo, String fName, String lName, Date dob, String gender, String studentClass, String enrollmentDate, String school, String lga) {
            this.admissionNumber = new SimpleStringProperty(admNo);
            this.firstName = new SimpleStringProperty(fName);
            this.lastName = new SimpleStringProperty(lName);
            this.gender = new SimpleStringProperty(gender);
            this.school = new SimpleStringProperty(school);
            this.lga = new SimpleStringProperty(lga);

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            this.dateOfBirth = new SimpleStringProperty(dateFormat.format(dob));

            this.studentClass = new SimpleStringProperty(studentClass);
            this.enrollmentDate = new SimpleStringProperty(enrollmentDate);

            this.age = new SimpleIntegerProperty(DateUtil.getAge(dob));
        }

        public String getAdmissionNumber() {
            return admissionNumber.get();
        }

        public void setAdmissionNumber(String admissionNumber) {
            this.admissionNumber.set(admissionNumber);
        }

        public String getFirstName() {
            return firstName.get();
        }

        public void setFirstName(String fName) {
            firstName.set(fName);
        }

        public String getLastName() {
            return lastName.get();
        }

        public void setLastName(String fName) {
            lastName.set(fName);
        }

        public String getDateOfBirth() {
            return dateOfBirth.get();
        }

        public SimpleStringProperty dateOfBirthProperty() {
            return dateOfBirth;
        }

        public void setDateOfBirth(String dateOfBirth) {
            this.dateOfBirth.set(dateOfBirth);
        }

        public String getGender() {
            return gender.get();
        }

        public void setGender(String sex) {
            gender.set(sex);
        }

        public String getStudentClass() {
            return studentClass.get();
        }

        public SimpleStringProperty studentClassProperty() {
            return studentClass;
        }

        public void setStudentClass(String studentClass) {
            this.studentClass.set(studentClass);
        }

        public String getEnrollmentDate() {
            return enrollmentDate.get();
        }

        public SimpleStringProperty enrollmentDateProperty() {
            return enrollmentDate;
        }

        public void setEnrollmentDate(String enrollmentDate) {
            this.enrollmentDate.set(enrollmentDate);
        }

        public String getSchool() {
            return school.get();
        }

        public void setSchool(String school) {
            this.school.set(school);
        }

        public String getLga() {
            return lga.get();
        }

        public void setLga(String lga) {
            this.lga.set(lga);
        }

        public int getAge() {
            return age.get();
        }

        public SimpleIntegerProperty ageProperty() {
            return age;
        }

        public void setAge(int age) {
            this.age.set(age);
        }
    }

}
