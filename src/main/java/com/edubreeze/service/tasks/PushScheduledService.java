package com.edubreeze.service.tasks;

import com.edubreeze.model.Student;
import com.edubreeze.model.exceptions.MissingStudentDataException;
import com.edubreeze.net.ApiClient;
import com.edubreeze.net.exceptions.ApiClientException;
import com.edubreeze.service.SyncService;
import com.edubreeze.service.exceptions.NullPushResultDataException;
import com.edubreeze.service.exceptions.SyncStillRunningException;
import com.edubreeze.utils.ExceptionTracker;
import com.edubreeze.utils.Util;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PushScheduledService extends Service<Integer> {

    private IntegerProperty count = new SimpleIntegerProperty();

    private List<Student> studentsDueForPush;
    private String apiToken;

    public PushScheduledService(String apiToken, List<Student> students) {
        studentsDueForPush  = students;
        this.apiToken = apiToken;
    }

    public PushScheduledService(String apiToken){
        this.apiToken = apiToken;
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

    protected Task<Integer> createTask() {
        return new Task<Integer>() {
            protected Integer call() {

                if(studentsDueForPush == null) {
                    studentsDueForPush = new ArrayList<>();
                    try {
                        studentsDueForPush = Student.getAll(); //.getStudentsDueForSync();
                    } catch (SQLException ex) {
                        Util.showExceptionDialogBox(ex, "Get Students Due for Sync Error", ex.getMessage());
                    }
                }

                // reset count
                setCount(0);

                SyncService syncService = new SyncService();
                ApiClient apiClient = new ApiClient();

                updateTitle("Push student records started...");

                if(studentsDueForPush.size() == 0) {
                    try {
                        updateTitle("No record due for push.");
                        Thread.sleep(5000);
                    }catch(InterruptedException ex) {

                    }

                    return 0;
                }

                System.out.println(studentsDueForPush.size());

                for (Student student : studentsDueForPush) {
                    if (isCancelled()) {
                        break;
                    }

                    try {

                        JSONObject studentPayload = syncService.convertToStudentPayload(student);

                        JSONArray fingerprints = syncService.convertToFingerprintsPayload(new ArrayList<>(student.getFingerprints()));
                        if (fingerprints.length() >= 0) {
                            studentPayload.put(SyncService.FINGERPRINTS_KEY, fingerprints);
                        }

                        studentPayload.put(SyncService.ACADEMIC_TERM_KEY,
                                syncService.convertToAcademicTermsPayload(
                                    new ArrayList<>(student.getAcademicTerms())
                                )
                        );

                        JSONObject pushResponse = apiClient.pushStudent(apiToken, studentPayload);
                        JSONObject result = pushResponse.optJSONObject("data");

                        if (result == null) {
                            throw new NullPushResultDataException("Push response does not have data property. Student Id: " + student.getAutoId());
                        }

                        String studentId = result.optString(SyncService.UUID_KEY);
                        Long lastSyncedTimestamp = result.optLong(SyncService.LAST_SYNCED_AT_KEY);

                        if (studentId == null) {
                            throw new NullPushResultDataException("Push response's 'data.uuid' is null . Student Id: " + student.getAutoId());
                        }

                        if (lastSyncedTimestamp == -1) {
                            throw new NullPushResultDataException("Push response's 'data.last_synced_at' timestamp is not set . Student Id: " + student.getAutoId());
                        }

                        Date lastSyncedAt = new Date(TimeUnit.SECONDS.toMillis(lastSyncedTimestamp));

                        Student.saveSyncResult(UUID.fromString(studentId), lastSyncedAt);

                        count.set(getCount() + 1);

                        updateTitle("Pushed " + getCount() + " record{s) successfully");

                        updateProgress(count.get(), studentsDueForPush.size());

                    } catch (ApiClientException | NullPushResultDataException | MissingStudentDataException | SQLException ex) {
                        System.out.println(ex.getMessage());
                        ExceptionTracker.track(ex);
                    }
                }

                return getCount();
            }
        };
    }
}
