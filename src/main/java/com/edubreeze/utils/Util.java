package com.edubreeze.utils;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Util {

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
}
