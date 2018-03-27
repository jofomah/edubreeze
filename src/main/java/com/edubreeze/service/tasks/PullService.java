package com.edubreeze.service.tasks;

import com.edubreeze.database.DatabaseHelper;
import com.edubreeze.model.AcademicRecord;
import com.edubreeze.model.Student;
import com.edubreeze.model.StudentAcademicTerm;
import com.edubreeze.model.StudentFingerprint;
import com.edubreeze.net.ApiClient;
import com.edubreeze.net.exceptions.ApiClientException;
import com.edubreeze.service.SyncService;
import com.edubreeze.service.exceptions.SyncStillRunningException;
import com.j256.ormlite.misc.TransactionManager;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public final class PullService extends Service<Integer> {
    private Task<Integer> pullTask = null;
    private static PullService pullService = null;
    private String apiToken;
    private int schoolId;
    private IntegerProperty count = new SimpleIntegerProperty();

    private PullService(String apiToken, int schoolId) {
        this.apiToken = apiToken;
        this.schoolId = schoolId;
    }

    public final void setCount(Integer value) {
        count.set(value);
    }

    public final Integer getCount() {
        return count.get();
    }

    public final IntegerProperty countProperty() {
        return count;
    }

    @Override
    protected Task<Integer> createTask() {
        PullService instance = this;

        pullTask = new Task<Integer>() {
            protected Integer call() throws Exception {
                ApiClient apiClient = new ApiClient();
                SyncService syncService = new SyncService();
                int delayThread = 500; // milliseconds

                try {
                    JSONArray studentArray = apiClient.getStudentsBySchool(instance.apiToken, instance.schoolId);
                    int studentSize = studentArray.length();

                    for (int studentIndex = 0; studentIndex < studentSize; studentIndex++) {
                        if(isCancelled()) {
                           break;
                        }
                        try {
                            try {
                                // just to add little bit of delay
                                Thread.sleep(delayThread);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            JSONObject studentObject = studentArray.optJSONObject(studentIndex);

                            Student student = syncService.studentFrom(studentObject);

                            TransactionManager.callInTransaction(
                                    DatabaseHelper.getDatabaseConnection().getConnectionSource(),
                                    new Callable<Boolean>() {
                                        public Boolean call() throws Exception {

                                            if (student.canSavePersonalInfo()) {
                                                /**
                                                 * Save pulled student info
                                                 */
                                                student.savePullSync();

                                                /**
                                                 * Save Student fingerprints
                                                 */
                                                List<StudentFingerprint> studentFingerprints = new ArrayList<>();
                                                JSONArray fingerprintArray = studentObject.optJSONArray(SyncService.FINGERPRINTS_KEY);
                                                if (fingerprintArray != null) {
                                                    studentFingerprints = syncService.fingerprintsFrom(fingerprintArray);
                                                }

                                                for (StudentFingerprint fingerprint : studentFingerprints) {
                                                    if (fingerprint.canSaveBiometric()) {
                                                        fingerprint.savePullSync();
                                                    }
                                                }

                                                /**
                                                 * Save Student Academic Terms and their related Academic Records
                                                 */
                                                JSONArray academicTermArray = studentObject.optJSONArray(SyncService.ACADEMIC_TERM_KEY);
                                                if (academicTermArray != null) {
                                                    int academicTermSize = academicTermArray.length();
                                                    for (int academicTermIndex = 0; academicTermIndex < academicTermSize; academicTermIndex++) {
                                                        JSONObject academicTermObject = academicTermArray.optJSONObject(academicTermIndex);
                                                        StudentAcademicTerm academicTerm = syncService.academicTermFrom(academicTermObject);
                                                        if (academicTerm.canSave()) {

                                                            academicTerm.savePullSync();

                                                            /**
                                                             * Save Student Academic Term's Academic Records
                                                             */
                                                            JSONArray academicRecordArray = academicTermObject.optJSONArray(SyncService.ACADEMIC_RECORDS_KEY);
                                                            if (academicRecordArray != null) {
                                                                int academicRecordSize = academicRecordArray.length();
                                                                for (int academicRecordIndex = 0; academicRecordIndex < academicRecordSize; academicRecordIndex++) {
                                                                    JSONObject academicRecordObject = academicRecordArray.optJSONObject(academicRecordIndex);
                                                                    AcademicRecord academicRecord = syncService.academicRecordFrom(academicTerm, academicRecordObject);

                                                                    if (academicRecord.canSave()) {
                                                                        academicRecord.savePullSync();

                                                                        count.set(getCount() + 1);
                                                                    }
                                                                }
                                                            }
                                                        }

                                                    }
                                                }
                                            }
                                            return true;
                                        }
                                    });

                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        } finally {

                            final int currentStudentIndex = (studentIndex + 1);

                            updateProgress(currentStudentIndex, studentSize);
                        }
                    }

                } catch (ApiClientException ex) {
                    ex.printStackTrace();
                }

                return getCount();
            }
        };

        return pullTask;
    }

    public final static PullService getPullService(String apiToken, int schoolId) throws SyncStillRunningException {
        if (pullService != null && pullService.isRunning()) {
            throw new SyncStillRunningException("Pull service is currently running");
        }

        if (pullService == null) {
            pullService = new PullService(apiToken, schoolId);

        } else {
            pullService.apiToken = apiToken;
            pullService.schoolId = schoolId;
        }

        return pullService;
    }

    public static PullService getPullService() {
        return pullService;
    }
}
