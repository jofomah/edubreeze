package com.edubreeze.controllers;

import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.UareUException;
import com.edubreeze.config.AppConfiguration;
import com.edubreeze.model.Student;
import com.edubreeze.model.StudentFingerprint;
import com.edubreeze.service.LoginService;
import com.edubreeze.service.WebCamService;
import com.edubreeze.service.enrollment.EnrollmentActionListener;
import com.edubreeze.service.enrollment.EnrollmentThread;
import com.edubreeze.service.enrollment.FingerPrintEnrollment;
import com.edubreeze.service.enrollment.ReaderStringConverter;
import com.edubreeze.utils.ImageUtil;
import com.edubreeze.utils.Util;
import com.edubreeze.utils.WebcamStringConverter;
import com.github.sarxos.webcam.Webcam;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class StudentBiometricController implements Initializable, EnrollmentActionListener {

    @FXML
    private ComboBox webcamsComboBox;

    @FXML
    private ImageView studentPassportImageView;

    @FXML
    private Button captureImageButton;

    @FXML
    private Label webCamStatusLabel;

    @FXML
    private Button cancelCaptureImageButton;

    @FXML
    private ComboBox fingerprintReadersComboBox;

    @FXML
    private Button refreshFingerprintReadersButton;

    @FXML
    private Label fingerprintReaderStatusLabel;

    @FXML
    private ImageView studentFingerprintImageView;

    @FXML
    private Button captureFingerPrintButton;

    @FXML
    private Button previousButton;

    @FXML
    private Button saveAndExitButton;

    @FXML
    private ComboBox<FingerPrintEnrollment.FingerType> selectFingerCombo;

    @FXML
    private Label capturedFingerprintStatusLabel;

    @FXML
    private Button gobackHomeButton;

    private ObservableList<Webcam> webcams = FXCollections.emptyObservableList();
    private Webcam webCam = null;
    private boolean stopCamera = false;
    private BufferedImage grabbedImage;
    private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();

    private EnrollmentThread enrollmentThread = null;
    private boolean enrollmentJustStarted = true;
    private final HashMap<FingerPrintEnrollment.FingerType, EnrollmentThread.EnrollmentEvent> fingerprintEnrollmentMap = new HashMap<>();
    private Reader currentReader = null;
    private static final int MAX_NUMBER_OF_FINGERPRINTS = 2;

    private Student currentStudent = null;
    private FingerPrintEnrollment.FingerType currentFingerType = null;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            currentStudent = AppConfiguration.getCurrentlyEditedStudent();
            if (currentStudent != null) {
                loadStudentDataToForm();
            }

        } catch (SQLException ex) {
            Util.showExceptionDialogBox(ex, "Get Student Record for Edit Error", "An error occurred while trying to fetch student data been edited.");
        }

        setupFingerprintList();

        webCamStatusLabel.setText("Status: Searching for web cams available on the system.");
        webcams = FXCollections.observableList(WebCamService.getWebcams());

        if (webcams.size() == 0) {
            webCamStatusLabel.setText("Status: No web cam found on this system, please connect one or contact support.");
        } else {
            webCamStatusLabel.setText("Status: Found " + webcams.size() + " web cam(s), please select one.");
        }

        captureImageButton.setDisable(true);
        cancelCaptureImageButton.setDisable(true);

        webcamsComboBox.setItems(webcams);

        webcamsComboBox.setConverter(new WebcamStringConverter());

        webcamsComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            Webcam selectedWebCam = null;

            if (newVal != null) {
                selectedWebCam = (Webcam) newVal;
            }

            initializeWebCam(selectedWebCam);
        });

        captureImageButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stopCamera = false;
                Task<Void> task = new Task<Void>() {

                    @Override
                    protected Void call() throws Exception {

                        final AtomicReference<WritableImage> ref = new AtomicReference<>();

                        try {
                            if (!stopCamera && (grabbedImage = webCam.getImage()) != null) {
                                updateMessage("Status: preparing captured image...");
                                ref.set(SwingFXUtils.toFXImage(grabbedImage, ref.get()));
                                grabbedImage.flush();

                                studentPassportImageView.imageProperty().unbind();
                                webCam.close();

                                updateMessage("Status: Displaying capture image.");
                                Platform.runLater(() -> {
                                    studentPassportImageView.setImage(ref.get());
                                    cancelCaptureImageButton.setDisable(false);
                                    captureImageButton.setDisable(true);
                                    stopCamera = true;
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                };

                Platform.runLater(() -> webCamStatusLabel.textProperty().bind(task.messageProperty()));

                Thread th = new Thread(task);
                th.setDaemon(true);
                th.start();
            }
        });

        cancelCaptureImageButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stopCamera = false;
                Object value = webcamsComboBox.getValue();
                if (value == null) {
                    return;
                }

                initializeWebCam((Webcam) value);
            }
        });

        previousButton.setOnAction(event -> {
            try {
                Util.changeScreen((Stage) previousButton.getScene().getWindow(), AppConfiguration.STUDENT_ACADEMIC_PERFORMANCE_SCREEN);
            } catch (IOException ex) {
                Util.showExceptionDialogBox(ex, "Change Screen Error", "An error occurred while trying to change from Student biometric screen.");
            }
        });

        saveAndExitButton.setOnAction(event -> {
            boolean studentImageSet = setStudentImage();

            if (!studentImageSet) {
                Util.showErrorDialog(
                        "Missing Student Image",
                        "Student Image Not Set",
                        "Please, capture student image"
                );
                return;
            }

            try {
                currentStudent.save(LoginService.getCurrentLoggedInUser());
            } catch (SQLException ex) {
                Util.showExceptionDialogBox(
                        ex,
                        "Save Student Biometric Record Error",
                        "An error occurred while trying to save student image data."
                );
                return;
            }

            try {
                Util.changeScreen((Stage) saveAndExitButton.getScene().getWindow(), AppConfiguration.STUDENT_LIST_SCREEN);
            } catch (IOException ex) {
                Util.showExceptionDialogBox(ex, "Change Screen Error", "An error occurred while trying to change from Student biometric screen.");
            }

            /*
            List<StudentFingerprint> studentFingerprints = prepareStudentFingerprints();
            if(!canSaveStudentFingerprints(studentFingerprints)) {
                Util.showErrorDialog(
                        "Missing Student Complete Student Fingerprints",
                        "Student Fingerprints are not Set or Incomplete.",
                        "Please, reset and capture student fingerprints."
                );
                return;
            }


            try {
                currentStudent.save(LoginService.getCurrentLoggedInUser());
            } catch (SQLException ex) {
                Util.showExceptionDialogBox(
                        ex,
                        "Save Student Biometric Record Error",
                        "An error occurred while trying to save student image data."
                );
                return;
            }

            int count = 0;
            try {
                for(StudentFingerprint fp : studentFingerprints) {
                    fp.save(LoginService.getCurrentLoggedInUser());
                    count++;
                }
            } catch(SQLException ex) {
                Util.showExceptionDialogBox(
                        ex,
                        "Save Student Biometric Record Error",
                        "An error occurred while trying to save student fingerprints data, only " +
                                count + "/" + studentFingerprints.size() + " were saved."
                );
                return;
            }
            */
        });

        List<FingerPrintEnrollment.FingerType> fingerTypes = Arrays.asList(FingerPrintEnrollment.FingerType.values());
        selectFingerCombo.setItems(FXCollections.observableArrayList(fingerTypes));
        selectFingerCombo.valueProperty().addListener(((observable, oldValue, newValue) -> {
            currentFingerType =  newValue;

            if(currentFingerType == null) {
                fingerprintReaderStatusLabel.setText("Please select finger type to be captured.");
                return;
            }

            startFingerprintCapture();

        }));

        gobackHomeButton.setOnAction(event -> {
            try {
                Util.changeScreen((Stage) gobackHomeButton.getScene().getWindow(), AppConfiguration.STUDENT_LIST_SCREEN);
            } catch (IOException ex) {
                Util.showExceptionDialogBox(ex, "Change Screen Error", "An error occurred while trying to change from Student Biometric screen.");
            }
        });

    }

    private boolean canSaveStudentFingerprints(List<StudentFingerprint> fingerprints) {
        boolean hasCompleteFingers = fingerprints != null && fingerprints.size() == MAX_NUMBER_OF_FINGERPRINTS;

        for(StudentFingerprint fp : fingerprints) {
            boolean canSaveBiometrics = fp.canSaveBiometric();
            if(!canSaveBiometrics) {
                return canSaveBiometrics;
            }
        }

        return hasCompleteFingers;
    }

    private void loadStudentDataToForm() {
        byte[] studentImageByte = currentStudent.getStudentImage();
        if(studentImageByte != null) {
            try {
                Image studentImage = ImageUtil.convertToImage(studentImageByte);
                studentPassportImageView.setImage(studentImage);

                capturedFingerprintStatusLabel.setText("Student Fingerprints already captured: " +
                        currentStudent.getFingerprints().size() + "/" + MAX_NUMBER_OF_FINGERPRINTS
                );

            } catch(IOException ex) {
                Util.showExceptionDialogBox(
                        ex,
                        "Load Student Image Error",
                        "An error occurred while trying to load student passport."
                );
            }
        }

    }

    private List<StudentFingerprint> prepareStudentFingerprints() {
        if(fingerprintEnrollmentMap.size() < MAX_NUMBER_OF_FINGERPRINTS) {
            Util.showErrorDialog(
                    "Incomplete Student Fingerprint Capture",
                    "Student fingerprint capture is not complete.",
                    "Please cancel current fingerprint capture and capture " +  MAX_NUMBER_OF_FINGERPRINTS  + " fingerprints."
            );
            return new ArrayList<>();
        }

        List<StudentFingerprint>  fingerprints = new ArrayList<>(currentStudent.getFingerprints());
        List<StudentFingerprint>  newStudentFingerprints = new ArrayList<>(MAX_NUMBER_OF_FINGERPRINTS);

        int currentFingerprint = 0;
        for(Map.Entry<FingerPrintEnrollment.FingerType, EnrollmentThread.EnrollmentEvent> enrollmentEventEntry : fingerprintEnrollmentMap.entrySet()) {
            FingerPrintEnrollment.FingerType fpType = enrollmentEventEntry.getKey();
            EnrollmentThread.EnrollmentEvent enrollmentEvent = enrollmentEventEntry.getValue();

            // update existing student finger print if any, else create new one
            StudentFingerprint fingerprint;

            Image fingerprintImage = ImageUtil.convertFidToJavaFXImage(enrollmentEvent.capture_result.image);
            byte[] fpImageBytes;
            try {
                fpImageBytes = ImageUtil.convertToByteArray(ImageUtil.convertToBuffered(fingerprintImage));
            } catch(IOException ex) {
                Util.showExceptionDialogBox(ex, "Fingerprint Image Conversion Error", "An Error Occurred while converting fingerpint image");
                continue;
            }
            byte[] fpFmdBytes = enrollmentEvent.enrollment_fmd.getData();

            if(currentFingerprint < fingerprints.size() && (fingerprint = fingerprints.get(currentFingerprint)) != null) {
                fingerprint.setFmdBytes(fpFmdBytes);
                fingerprint.setFingerprintImageBytes(fpImageBytes);
                fingerprint.setFingerType(fpType.fingerName());

            } else {
                fingerprint = new StudentFingerprint(fpImageBytes, fpFmdBytes, fpType.fingerName(), currentStudent);
            }

            newStudentFingerprints.add(fingerprint);

            currentFingerprint++;
        }

        return newStudentFingerprints;
    }

    private boolean setStudentImage() {
        boolean wasSet = false;
        Image studentImage = studentPassportImageView.getImage();
        if(studentImage == null) {
            Util.showErrorDialog(
                    "Missing Student Passport",
                    "Please take/capture student passport.",
                    "Please take student passport before saving."
            );
            return wasSet;
        }
        BufferedImage bufImg = ImageUtil.convertToBuffered(studentImage);

        try{
            byte[] studentImageBytes = ImageUtil.convertToByteArray(bufImg);
            currentStudent.setStudentImage(studentImageBytes);

            wasSet = true;

        } catch (IOException ex) {
            Util.showExceptionDialogBox(ex, "Student Image Conversion Error", "An error occurred while converting student image");
        }

        return wasSet;
    }

    private void setupFingerprintList() {

        fingerprintReadersComboBox.setConverter(new ReaderStringConverter());

        // populates fingerprint reader selection drop down.
        refreshFingerprintReaders();

        /**
         * TODO: consider the following:
         * 1. do not cancel service, setReader(), close previous reader in service and open new one?
         *   cons: but then it becomes complex to track progress in current service, like how many fingers have been created
         */
        fingerprintReadersComboBox.valueProperty()
                .addListener((observable, oldValue, newValue) -> {
                    currentReader = (Reader) newValue;
                    Reader oldReader = (Reader) oldValue;

                    if (currentReader == null) {
                        fingerprintReaderStatusLabel.setText("Please select a reader from the list.");
                        return;
                    }

                    try {

                        if(oldReader != null) {
                            oldReader.Close();
                        }

                    }  catch (UareUException ex) {
                        Util.showExceptionDialogBox(ex, "Open Fingerprint Reader Error", "Please, unplug fingerprint, plug back, refresh list and try again.");
                        return;
                    }

                    fingerprintReaderStatusLabel.setText("Selected reader: " + currentReader.GetDescription().id.product_name);

                    //open reader
                    try {
                        currentReader.Open(Reader.Priority.COOPERATIVE);
                    } catch (UareUException ex) {
                        Util.showExceptionDialogBox(ex, "Open Fingerprint Reader Error", "Please, unplug fingerprint, plug back, refresh list and try again.");
                        return;
                    }

                    startFingerprintCapture();

                });

        captureFingerPrintButton.setOnAction(event -> {
            startFingerprintCapture();
        });

        refreshFingerprintReadersButton.setOnAction(event -> {
            refreshFingerprintReaders();
        });
    }

    private void startFingerprintCapture() {
        if(currentReader == null) {
            fingerprintReaderStatusLabel.setText("Please select finger print reader to be used.");
            return;
        }

        if(currentFingerType == null) {
            fingerprintReaderStatusLabel.setText("Please, select finger type to be captured.");
            return;
        }

        //stop enrollment thread
        if (enrollmentThread != null) {
            enrollmentThread.cancel();
        }

        enrollmentThread = new EnrollmentThread(currentReader, this);

        //start enrollment thread
        enrollmentThread.start();

        enrollmentThread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
            Util.showExceptionDialogBox(exception, "Enrollment Thread Exception", "An error occurred during enrollment");
        });
    }

    private void refreshFingerprintReaders() {
        try {
            List<Reader> readers = FingerPrintEnrollment.getReaders();

            if (readers.size() > 0) {
                fingerprintReaderStatusLabel.setText("Found " + readers.size() + "  fingerprint reader(s), please select one.");
            } else {
                fingerprintReaderStatusLabel.setText("No fingerprint device found, please connect one and click refresh list.");
            }

            fingerprintReadersComboBox.getSelectionModel().clearSelection();
            fingerprintReadersComboBox.setItems(FXCollections.observableArrayList(readers));

        } catch (UareUException ex) {
            Util.showExceptionDialogBox(ex, "Get Fingerprint Readers Error", "FingerPrintEnrollment.getReaders()");
        }
    }

    public void initializeWebCam(final Webcam newWebCam) {

        Task<Void> webCamTask = new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                if (webCam != null) {
                    updateMessage("Status: Disposing previous selected web cam.");
                    disposeWebCamCamera();
                }

                if (newWebCam == null) {
                    updateMessage("Status: No web cam is selected, please select one.");
                    return null;
                }

                webCam = newWebCam;
                webCam.open();
                updateMessage("Status: starting web cam...");

                startWebCamStream();

                return null;
            }
        };

        Thread webCamThread = new Thread(webCamTask);
        webCamThread.setDaemon(true);
        webCamThread.start();

        Platform.runLater(() -> {
            webCamStatusLabel.textProperty().bind(webCamTask.messageProperty());
            cancelCaptureImageButton.setDisable(true);
            captureImageButton.setDisable(false);
        });
    }

    protected void disposeWebCamCamera() {
        stopCamera = true;
        webCam.close();
        cancelCaptureImageButton.setDisable(true);
    }

    protected void startWebCamStream() {

        stopCamera = false;

        Task<Void> task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                final AtomicReference<WritableImage> ref = new AtomicReference<>();
                BufferedImage img = null;
                updateMessage("Status: starting web cam ..");

                while (!stopCamera && !isCancelled()) {
                    try {
                        if ((img = webCam.getImage()) != null) {
                            updateMessage("Status: ready to capture image.");
                            ref.set(SwingFXUtils.toFXImage(img, ref.get()));
                            img.flush();

                            Platform.runLater(() -> imageProperty.set(ref.get()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }
        };

        Platform.runLater(() -> {
            captureImageButton.setDisable(false);
            webCamStatusLabel.textProperty().bind(task.messageProperty());
            studentPassportImageView.imageProperty().bind(imageProperty);
        });


        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    @Override
    public void handleEnrollmentAction(EnrollmentThread.EnrollmentEvent enrollmentEvent) {
        if(currentFingerType == null) {
            fingerprintReaderStatusLabel.setText("Please select finger to be captured");
            enrollmentThread.cancel();
            return;
        }

        if (enrollmentEvent.action.equals(EnrollmentThread.ACT_PROMPT)) {
            if (enrollmentJustStarted) {
                fingerprintReaderStatusLabel.setText("New enrollment, put " + currentFingerType.fingerName() + " on the reader.");
            } else {
                fingerprintReaderStatusLabel.setText("Put the same " + currentFingerType.fingerName() + " on the reader.");
            }
            enrollmentJustStarted = false;
        } else if (enrollmentEvent.action.equals(EnrollmentThread.ACT_CAPTURE)) {
            Platform.runLater(() -> {
                if (enrollmentEvent.capture_result != null) {
                    Util.showInfo(
                            "Fingerprint Enrollment Error",
                            "Bad Image Quality",
                            "Image Quality is poor: " + enrollmentEvent.capture_result.quality + ", please try again"

                    );
                } else if (enrollmentEvent.exception != null) {
                    Util.showExceptionDialogBox(enrollmentEvent.exception, "Capture Error", enrollmentEvent.exception.getMessage());
                } else if (enrollmentEvent.reader_status != null) {
                    Util.showInfo(
                            "Fingerprint Enrollment Error",
                            "Bad Reader Status",
                            "Reader status is bad, unplug, refresh list, plug back, refresh list and try again"

                    );
                }
            });

            enrollmentJustStarted = false;
        } else if (enrollmentEvent.action.equals(EnrollmentThread.ACT_FEATURES)) {
            Platform.runLater(() -> {
                if (enrollmentEvent.exception == null) {
                    showImage(enrollmentEvent.capture_result);
                    fingerprintReaderStatusLabel.setText("fingerprint captured, features extracted");
                } else {
                    Util.showExceptionDialogBox(enrollmentEvent.exception, "Fingerprint Enrollment Error", "Feature Extraction");
                }
            });
            enrollmentJustStarted = false;

        } else if (enrollmentEvent.action.equals(EnrollmentThread.ACT_DONE)) {
            if (enrollmentEvent.exception == null) {
                Platform.runLater(() -> {
                    setEnrollment(enrollmentEvent);
                    String str = String.format("Enrollment template created, size: %d", enrollmentEvent.enrollment_fmd.getData().length);
                    fingerprintReaderStatusLabel.setText(str);
                });
            } else {
                Platform.runLater(() -> {
                    Util.showExceptionDialogBox(enrollmentEvent.exception, "Fingerprint Enrollment Error", "Enrollment Template Creation");
                });
            }
            enrollmentJustStarted = true;
        } else if (enrollmentEvent.action.equals(EnrollmentThread.ACT_CANCELED)) {
            //canceled
            enrollmentThread.cancel();
            fingerprintReaderStatusLabel.setText("Capture cancelled, start again by clicking capture button.");
        }

        //cancel enrollment if any exception or bad reader status
        if (enrollmentEvent.exception != null) {
            enrollmentThread.cancel();
            fingerprintReaderStatusLabel.setText("Capture cancelled due to an error, start again by clicking capture button.");

        } else if (null != enrollmentEvent.reader_status &&
                Reader.ReaderStatus.READY != enrollmentEvent.reader_status.status &&
                Reader.ReaderStatus.NEED_CALIBRATION != enrollmentEvent.reader_status.status
                ) {

            enrollmentThread.cancel();
            fingerprintReaderStatusLabel.setText("Capture cancelled due to an error, start again by clicking capture button.");
        }
    }

    private void setEnrollment(EnrollmentThread.EnrollmentEvent enrollmentEvent) {
        fingerprintEnrollmentMap.put(currentFingerType, enrollmentEvent);
        /**
         * If max number of fingerprints have been captured, if current enrollment finger type has been captured, remove it,
         * else if finger print map has next, remove the next key, so we capture only two fingerprints
         */

        capturedFingerprintStatusLabel.setText("Captured " + fingerprintEnrollmentMap.size() + "/" + MAX_NUMBER_OF_FINGERPRINTS);
        // reset current finger print type
        selectFingerCombo.getSelectionModel().clearSelection();
        currentFingerType = null;

        if(fingerprintEnrollmentMap.size() == MAX_NUMBER_OF_FINGERPRINTS) {
            selectFingerCombo.setDisable(true);
            fingerprintReadersComboBox.setDisable(true);
            captureFingerPrintButton.setDisable(true);
            fingerprintReaderStatusLabel.setText("Captured required number of fingerprints, to recapture, click cancel.");
        }

    }

    private void showImage(Reader.CaptureResult captureResult) {
        if(captureResult != null && captureResult.image != null) {
            /*
            Fid.Fiv view = captureResult.image.getViews()[0];

            final AtomicReference<WritableImage> ref = new AtomicReference<>();
            BufferedImage img = new BufferedImage(view.getWidth(), view.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            img.getRaster().setDataElements(0, 0, view.getWidth(), view.getHeight(), view.getImageData());
            ref.set(SwingFXUtils.toFXImage(img, ref.get()));
            img.flush();
            */
            studentFingerprintImageView.setImage(ImageUtil.convertFidToJavaFXImage(captureResult.image));
        }
    }
}
