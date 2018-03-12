package com.edubreeze.utils;

import com.edubreeze.model.Lga;
import com.edubreeze.model.School;
import com.edubreeze.model.State;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Optional;

public class Util {

    public static void showInfo(String title, String headerText, String contentText) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(title);
                alert.setHeaderText(headerText);
                alert.setContentText(contentText);

                alert.showAndWait();
            }
        });
    }

    public static void showErrorDialog(final String title, final String headerText, final String contentBody) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(title);
                alert.setHeaderText(headerText);
                alert.setContentText(contentBody);

                alert.showAndWait();
            }
        });
    }

    public static void showExceptionDialogBox(
            final Exception ex,
            final String title,
            final String headerText
    ) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
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
            }
        });
    }

    public static void changeScreen(final Stage stage, final String screenFxmlResourcePath) throws IOException{
        final Parent root = FXMLLoader.load(Util.class.getClass().getResource(screenFxmlResourcePath));
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //create a new scene with root and set the stage
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
        });
    }

    public static School showSelectSchoolDialog()
    {
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

            selectStateCombo.setConverter(new StringConverter() {
                @Override
                public String toString(Object object) {
                    if(object == null) {
                        return "";
                    }
                    return ((State) object).getName();
                }

                @Override
                public Object fromString(String string) {
                    return null;
                }
            });

            selectStateCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                State selectedState = ((State)newVal);
                if(selectedState == null) {
                    // set selected LGA to
                    selectLgaCombo.getItems().clear();
                    return;
                }

                //set LGA list
                selectLgaCombo.setItems(FXCollections.observableList(new ArrayList<Lga>(selectedState.getLgas())));

                selectSchoolCombo.setItems(FXCollections.observableList(new ArrayList<School>()));
            });

            selectLgaCombo.setConverter(new StringConverter() {
                @Override
                public String toString(Object object) {
                   if (object == null) {
                       return "";
                   }

                   return ((Lga)object).getName();
                }

                @Override
                public Object fromString(String string) {
                    return null;
                }
            });

            selectLgaCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                Lga selectedLga = ((Lga)newVal);
                if(selectedLga == null) {
                    // set selected LGA to nothing
                    selectSchoolCombo.getItems().clear();
                    return;
                }

                selectSchoolCombo.setItems(FXCollections.observableList(new ArrayList<School>(selectedLga.getSchools())));
            });

            selectSchoolCombo.setConverter(new StringConverter() {
                @Override
                public String toString(Object object) {
                    return (object == null)? "" : ((School)object).getName();
                }

                @Override
                public Object fromString(String string) {
                    return null;
                }
            });

            gridContentPane.add(new Label("State:"), 0, 0);
            gridContentPane.add(selectStateCombo, 1, 0);

            gridContentPane.add(new Label("LGA:"), 0, 1);
            gridContentPane.add(selectLgaCombo, 1, 1);

            gridContentPane.add(new Label("School:"), 0, 2);
            gridContentPane.add(selectSchoolCombo, 1, 2);

            alert.getDialogPane().setContent(gridContentPane);

            alert.setResizable(true);
            alert.getDialogPane().setPrefSize(250, 250);

            Optional<ButtonType> result = alert.showAndWait();
            if ( result.get() == ButtonType.OK )
            {
                if(selectSchoolCombo.getValue() != null) {
                    selectedSchool =  (School)selectSchoolCombo.getValue();
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
