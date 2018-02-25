package com.edubreeze.config;

import com.edubreeze.model.User;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.spring.DaoFactory;
import com.j256.ormlite.support.ConnectionSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AppConfiguration {

    private static final String fs = File.separator;
    public static final String API_BASE_URL_KEY = "ApiBaseUrl";
    public static final String LOGIN_SCREEN_PATH = "/fxml/LoginScreen.fxml";
    public static final String MAIN_STYLESHEET_PATH = "/styles/Styles.css";
    public static final String APP_TITLE = "EduBreeze - Easy to use Biometric Education Management Information System";
    public static final String LOADING_ICON = "/images/loading-icon.gif";
    public static final String DATABASE_FILE = "/database/edubreeze.db";
    public static final String STUDENT_PERSONAL_INFO_SCREEN = "/fxml/student/StudentPersonalInfo.fxml";

    private static final String CONFIG_FILE = "edubreeze.properties";
    private static  final String CONFIG_DIR = "config";
    private static final String CONFIG_PATH = CONFIG_DIR + fs + CONFIG_FILE;
    private static final Properties props = new Properties();

    public static final void init() throws IOException {
        InputStream is = null;

        // First try loading from the current directory, meaning config files in same folder as app will
        // override app packaged config
        try {
            File f = new File(CONFIG_FILE);
            is = new FileInputStream(f);
        } catch (Exception e) {
            is = null;
        }

        if (is == null) {
            // Try loading from classpath
            ClassLoader classLoader = AppConfiguration.class.getClassLoader();
            is = classLoader.getResourceAsStream(CONFIG_PATH);
        }

        // Try loading properties from the file (if found)
        props.load(is);

    }

    public static final String getByKey(String key, String defaultValue)
    {
        return props.getProperty(key, defaultValue);
    }

    public static final String getByKey(String key)
    {
        return getByKey(key, null);
    }

    public static final String getDatabaseFileUrl()
    {
        return "jdbc:h2:~" + AppConfiguration.DATABASE_FILE + ";";
    }

    public  static final List<Dao<?, ?>> getDaoList(ConnectionSource connectionSource) throws SQLException
    {
        List<Dao<?, ?>> daos = new ArrayList<Dao<?, ?>>();

        daos.add(DaoFactory.createDao(connectionSource, User.class));

        return daos;
    }
}
