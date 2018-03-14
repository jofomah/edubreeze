package com.edubreeze.config;

import com.edubreeze.model.School;
import com.edubreeze.model.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.*;

public class AppConfiguration {

    public static final String LOGIN_SCREEN_PATH = "/fxml/LoginScreen.fxml";
    public static final String MAIN_STYLESHEET_PATH = "/styles/Styles.css";
    public static final String APP_TITLE = "EduBreeze - Easy to use Biometric Education Management Information System";
    public static final String LOADING_ICON = "/images/loading-icon.gif";
    public static final String DATABASE_FILE = "/database/edubreeze.db";
    public static final String STUDENT_PERSONAL_INFO_SCREEN = "/fxml/student/StudentPersonalInfo.fxml";
    public static final String LOADING_SCREEN = "/fxml/LoadingScreen.fxml";
    public static final String STUDENT_LIST_SCREEN = "/fxml/student/StudentList.fxml";
    public static final String STUDENT_BIOMETRIC_SCREEN = "/fxml/student/StudentBiometric.fxml";
    public static final String STUDENT_ACADEMIC_PERFORMANCE_SCREEN = "/fxml/student/StudentAcademicPerformance.fxml";
    public static final String INITIAL_SYNC_KEY = "INITIAL_SYNC";
    public static final String LOGIN_ERROR_DIALOG_HEADER = "Login Failed";
    public static final String WRONG_USERNAME_AND_PASSWORD = "Wrong username and/or password.";
    public static final String PLEASE_ENTER_CORRECT_CREDENTIALS = "Please, enter correct username and password.";
    public static final String MISSING_LOGIN_CREDENTIALS = "Missing login credentials";
    public static final String PLEASE_ENTER_MISSING_CREDENTIALS = "Please, enter username and password before login.";
    public static final String LOGIN_SQL_ERROR = "Database error occurred after login";
    public static final String LOGIN_SQL_ERROR_DETAIL = "Database error occurred while trying offline login or to save logged in user details.";
    public static final String LOGIN_IN_PROGRESS_TEXT = "Logging in ...";
    public static final String CHECK_INITIAL_SYNC_ERROR_HEADER = "Check Initial Sync Status Error";
    public static final String INITIAL_SYNC_STATUS_CHECK_SQL_ERROR = "Database error occurred while trying to check initial sync status";
    public static final String CHANGE_SCREEN_ERROR_HEADER = "Change Screen Error";
    public static final String CHANGE_SCREEN_ERROR_DETAIL = "An error occurred when trying to change screen.";
    public static final String ADD_SUBJECT_MANUALLY = "Add subject manually";

    private static UUID currentStudentId;
    private static int currentSchoolId;

    public static final String getDatabaseFileUrl() {
        return "jdbc:h2:~" + AppConfiguration.DATABASE_FILE + ";IGNORECASE=TRUE";
    }

    public static ObservableList<String> getStudentClassList() {
        return FXCollections.observableArrayList(
                "JSS 1",
                "JSS 2",
                "JSS 3",
                "SSS 1",
                "SSS 2",
                "SSS 3"
        );
    }

    public static ObservableList<String> getStudentClassSectionTypes() {
        return FXCollections.observableArrayList("Art", "Commerce", "Science", "None");
    }

    public static ObservableList<String> getReligionList() {
        return FXCollections.observableArrayList("Christianity", "Islam", "None");
    }

    public static ObservableList<String> getAcademicSessions() {
        return FXCollections.observableArrayList(
                "2019/2020",
                "2018/2019",
                "2017/2018",
                "2016/2017",
                "2015/2016",
                "2014/2015",
                "2013/2014",
                "2012/2013",
                "2011/2012",
                "2010/2011",
                "2009/2010",
                "2008/2009",
                "2007/2008",
                "2006/2007",
                "2005/2006",
                "2004/2005"
        );
    }

    public static ObservableList<String> getTerms() {
        return FXCollections.observableArrayList("1st Term", "2nd Term", "3rd Term");
    }

    public  static ObservableList<String> getSubjects() {
        String[] lists = {
                "English",
                "Mathematics",
                "Civic Education",
                "Biology",
                "Physics",
                "Commerce",
                "Literature in English",
                "History",
                "Chemistry",
                "Book Keeping and Accounting",
                "Geography",
                "Islamic Religious Studies",
                "Animal Husbandry",
                "Marketing",
                "Catering",
                "Agricultural Science",
                "Economics",
                "Hausa"
        };

        List<String> arrayLists = new ArrayList<>(Arrays.asList(lists));
        Collections.sort(arrayLists);
        arrayLists.add(0, ADD_SUBJECT_MANUALLY);

        return FXCollections.observableArrayList(arrayLists);
    }

    public static void setCurrentlyEditedStudentId(UUID studentId) {
        currentStudentId = studentId;
    }

    public static Student getCurrentlyEditedStudent() throws SQLException{
        return Student.find(currentStudentId);
    }

    public static void setCurrentSchoolId(int schoolId) {
        currentSchoolId = schoolId;
    }

    /**
     * Used to track currently selected school to avoid, selecting current school
     * all the time.
     * @return
     */
    public static School getCurrentSchool() throws SQLException {
        return School.find(currentSchoolId);
    }
}
