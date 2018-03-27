package com.edubreeze;

import com.edubreeze.config.AppConfiguration;
import com.edubreeze.database.DatabaseConnectionInterface;
import com.edubreeze.database.H2DatabaseConnection;
import com.edubreeze.database.TableSchemaManager;
import com.edubreeze.service.tasks.MonitorInternetConnectionTask;
import com.edubreeze.utils.ExceptionTracker;
import com.edubreeze.utils.Util;
import io.sentry.Sentry;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.sql.SQLException;


public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        String loginScreenPath = AppConfiguration.LOGIN_SCREEN_PATH;
        String mainStyleSheetPath = AppConfiguration.MAIN_STYLESHEET_PATH;

        Parent root = FXMLLoader.load(getClass().getResource(loginScreenPath));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(mainStyleSheetPath);

        stage.setTitle(AppConfiguration.APP_TITLE);


        Image appIcon = new Image(getClass().getResourceAsStream(AppConfiguration.APP_ICON));
        stage.getIcons().add(appIcon);

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
        /**
         * Setup Sentry
         */
        setupSentry();

        try {
            DatabaseConnectionInterface dbConnection = new H2DatabaseConnection(AppConfiguration.getDatabaseFileUrl());
            TableSchemaManager tableSchemaManager = new TableSchemaManager();
            tableSchemaManager.initDatabase(dbConnection);

        } catch (SQLException ex) {
            ExceptionTracker.track(ex);

            Util.showExceptionDialogBox(ex, "Database Setup Error", "An error occurred while initializing app database.");
        }

        /**
         * Start network changes detector
         */
        startNetworkMonitoringTask();

        // load app GUI
        try {

            launch(args);

        } catch (Exception ex) {
            ExceptionTracker.track(ex);

            Util.showExceptionDialogBox(ex,
                    "App Launch Error",
                    "A runtime error occurred."
            );
        }
    }

    private static void startNetworkMonitoringTask() {
        MonitorInternetConnectionTask networkTask = new MonitorInternetConnectionTask();
        networkTask.start();
    }

    private static void setupSentry() {
        Sentry.init(AppConfiguration.SENTRY_DSN + "?buffer.dir=edubreeze-sentry&buffer.size=500");
    }
}
