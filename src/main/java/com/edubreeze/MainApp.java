package com.edubreeze;

import com.edubreeze.database.DatabaseConnectionInterface;
import com.edubreeze.database.H2DatabaseConnection;
import com.edubreeze.database.TableSchemaManager;
import com.edubreeze.model.State;
import com.edubreeze.utils.Util;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Screen;
import javafx.stage.Stage;
import com.edubreeze.config.AppConfiguration;

import java.io.IOException;
import java.sql.SQLException;


public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        String loginScreenPath = AppConfiguration.LOGIN_SCREEN_PATH;
        String mainStyleSheetPath = AppConfiguration.MAIN_STYLESHEET_PATH;

        Parent root = FXMLLoader.load(getClass().getResource(loginScreenPath));

        System.out.println("javafx.runtime.version: " + System.getProperties().get("javafx.runtime.version"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(mainStyleSheetPath);

        stage.setTitle(AppConfiguration.APP_TITLE);

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());

        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            DatabaseConnectionInterface dbConnection = new H2DatabaseConnection(AppConfiguration.getDatabaseFileUrl());
            TableSchemaManager tableSchemaManager = new TableSchemaManager();
            tableSchemaManager.initDatabase(dbConnection);

        } catch (SQLException ex) {
            Util.showExceptionDialogBox(ex, "Database Setup Error", "An error occurred while initializing app database.");
        }

        // load app GUI
        try {
            launch(args);
        } catch (Exception ex) {
            Util.showExceptionDialogBox(ex,
                    "App Launch Error",
                    "A runtime error occurred."
            );
        }

    }

}
