package com.edubreeze.service;

import com.edubreeze.config.AppConfiguration;
import com.edubreeze.database.DatabaseConnectionInterface;
import com.edubreeze.database.DatabaseHelper;
import com.edubreeze.model.AppStatus;
import com.edubreeze.model.Lga;
import com.edubreeze.model.School;
import com.edubreeze.model.State;
import com.edubreeze.net.ApiClient;
import com.edubreeze.net.exceptions.ApiClientException;
import com.edubreeze.service.exceptions.NullApiResponseException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.concurrent.Callable;

public class ApplicationService {

    private String apiToken;
    private ApiClient apiClient;

    public ApplicationService(String apiToken, ApiClient apiClient) {
        this.apiToken = apiToken;
        this.apiClient = apiClient;
    }

    public boolean hasCompletedInitialSync() throws SQLException{

        final Dao<AppStatus, String> appStatusDao = DatabaseHelper.getAppStatusDao();

        AppStatus initialSync = appStatusDao.queryForId(AppConfiguration.INITIAL_SYNC_KEY);

        return (initialSync != null && Boolean.valueOf(initialSync.getValue()));
    }

    public JSONArray loadSchoolsByLga(Lga lga) throws ApiClientException {
        return apiClient.getSchoolsByLga(apiToken, lga.getId());
    }

    public JSONArray loadSchoolsByState(State state) throws ApiClientException {
        return apiClient.getSchoolsByState(apiToken, state.getId());
    }

    public boolean loadStates(final ProgressBar loadingProgressBar, final Label progressText, final DatabaseConnectionInterface dbConnection) throws ApiClientException, NullApiResponseException, SQLException, Exception {

        double value = 0.1;
        String status = "Fetching states and LGAs list from remote server ...";
        ApplicationService.updateDataSyncProgress(loadingProgressBar, progressText, value, status);

        final JSONArray resultSet = apiClient.getStates(apiToken);

        value = 0.2;
        status = "Fetched states and LGAs list, processing data ...";
        ApplicationService.updateDataSyncProgress(loadingProgressBar, progressText, value, status);

        if (resultSet == null) {
            throw new NullApiResponseException("Get States API call returned null data");
        }

        // instantiate daos
        final Dao<State, Integer> stateDao = DaoManager.createDao(dbConnection.getConnectionSource(), State.class);
        final Dao<Lga, Integer> lgaDao = DaoManager.createDao(dbConnection.getConnectionSource(), Lga.class);
        final Dao<School, Integer> schoolDao = DaoManager.createDao(dbConnection.getConnectionSource(), School.class);
        final Dao<AppStatus, String> appStatusDao = DaoManager.createDao(dbConnection.getConnectionSource(), AppStatus.class);

        return stateDao.callBatchTasks(new Callable<Boolean>() {
            public Boolean call() throws SQLException, ApiClientException {
                double progressValue = 0.3;
                String statusText = "";

                // insert state and their LGAs data one at a time
                for (int index = 0; index < resultSet.length(); index++) {
                    JSONObject stateJson = resultSet.optJSONObject(index);

                    String stateName = stateJson.optString("name", null);
                    Integer id = stateJson.optInt("id", -1);


                    if (stateName == null || id == -1) {
                        // skip
                        System.out.println("State does not have correct name and/or id, State: " + stateName + ", id: " + id);
                        continue;
                    }


                    State state = new State(id, stateName);
                    stateDao.createOrUpdate(state);

                    statusText = "Saved State: " + stateName + " data pulled from remote server ...";
                    ApplicationService.updateDataSyncProgress(loadingProgressBar, progressText, progressValue, statusText);

                    // create state LGAS
                    JSONArray lgaJsonArray = stateJson.optJSONArray("lgas");

                    if(lgaJsonArray == null) {
                        System.out.println("State is missing 'lgas' field, State: " + stateName + ", Id: " + id);
                        continue;
                    }

                    for(int lgaIndex = 0; lgaIndex < lgaJsonArray.length(); lgaIndex++)
                    {
                        JSONObject lgaJson = lgaJsonArray.optJSONObject(lgaIndex);

                        String lgaName = lgaJson.optString("name", null);
                        int lgaId = lgaJson.optInt("id", -1);

                        if(lgaName == null || lgaId == -1) {
                            System.out.println("LGA does not have correct name and/or id: LGA: " + lgaName + ", id: " + id);
                            continue;
                        }

                        Lga lga = new Lga(lgaId, lgaName, state);
                        lgaDao.createOrUpdate(lga);
                        statusText = "Saved LGA: " + lgaName + " ( state: " + stateName+ ") record pulled from remote server ...";
                        ApplicationService.updateDataSyncProgress(loadingProgressBar, progressText, progressValue, statusText);

                        // import LGA schools
                        statusText = "Fetching LGA: " + lgaName + " ( state: " + stateName+ ") school list from remote server ...";
                        ApplicationService.updateDataSyncProgress(loadingProgressBar, progressText, progressValue, statusText);
                    }

                    statusText = "Fetching State: " + stateName+ " school list from remote server ...";
                    ApplicationService.updateDataSyncProgress(loadingProgressBar, progressText, progressValue, statusText);

                    JSONArray stateSchoolsJson = loadSchoolsByState(state);

                    statusText = "Fetched State: " + stateName+ " school list from remote server ...";
                    ApplicationService.updateDataSyncProgress(loadingProgressBar, progressText, progressValue, statusText);

                    if(stateSchoolsJson == null) {
                        System.out.println("State schools request returns null");
                        continue;
                    }

                    for(int schoolIndex = 0; schoolIndex < stateSchoolsJson.length(); schoolIndex++) {
                        JSONObject schoolJson = stateSchoolsJson.optJSONObject(schoolIndex);

                        if(schoolJson == null) {
                            System.out.println("School is null");
                            continue;
                        }

                        int schoolId = schoolJson.optInt("id", -1);
                        String schoolName = schoolJson.optString("school_name");
                        String schoolCode = schoolJson.optString("name_code", "");
                        int schoolLgaId = schoolJson.optInt("lga_id", -1);
                        Double schoolLongitude = schoolJson.optDouble("longitude", 0);
                        Double schoolLatitude = schoolJson.optDouble("latitude", 0);
                        int numberOfClasses = schoolJson.optInt("classroom_count", 0);
                        int numberOfToilets = schoolJson.optInt("toilets", 0);
                        int numberOfUrinals = schoolJson.optInt("urinals", 0);
                        boolean hasElectricity = schoolJson.optBoolean("electricity", false);
                        boolean hasDrinkingWater = schoolJson.optBoolean("drinking_water", false);
                        Lga schoolLga = lgaDao.queryForId(schoolLgaId);

                        School school = new School();
                        school.setId(schoolId);
                        school.setName(schoolName);
                        school.setCode(schoolCode);
                        school.setLga(schoolLga);
                        school.setLatitude(schoolLatitude);
                        school.setLongitude(schoolLongitude);
                        school.setNumberOfClasses(numberOfClasses);
                        school.setNumberOfToilets(numberOfToilets);
                        school.setNumberOfUrinals(numberOfUrinals);
                        school.setDrinkingWater(hasElectricity);
                        school.setDrinkingWater(hasDrinkingWater);

                        if(school.canSave()) {
                            statusText = "Saving State: " + stateName+ " school, Name: " + schoolName + ", LGA:  " + schoolLga.getName() + ".";
                            ApplicationService.updateDataSyncProgress(loadingProgressBar, progressText, progressValue, statusText);
                            schoolDao.createOrUpdate(school);
                        }

                    }

                    progressValue += (0.6 / resultSet.length());

                    statusText = "Completed saving State: " + stateName + " data pulled from remote server ...";
                    ApplicationService.updateDataSyncProgress(loadingProgressBar, progressText, progressValue, statusText);
                }

                progressValue = 1;
                statusText = "Completed initial data pull from remote server";
                ApplicationService.updateDataSyncProgress(loadingProgressBar, progressText, progressValue, statusText);

                AppStatus initialSyncStatus = new AppStatus(AppConfiguration.INITIAL_SYNC_KEY, String.valueOf(true));
                appStatusDao.createOrUpdate(initialSyncStatus);

                return true;
            }
        });

    }

    private static void updateDataSyncProgress(final ProgressBar loadingProgressBar, final Label progressText, final double progressValue, final String statusText) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                loadingProgressBar.setProgress(progressValue);
                progressText.setText(statusText);
            }
        });
    }
}
