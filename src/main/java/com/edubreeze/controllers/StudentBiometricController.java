package com.edubreeze.controllers;

import com.digitalpersona.uareu.*;
import com.edubreeze.config.AppConfiguration;
import com.edubreeze.service.WebCamService;
import com.edubreeze.service.enrollment.*;
import com.edubreeze.utils.Util;
import com.github.sarxos.webcam.Webcam;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class StudentBiometricController implements Initializable {

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

    private ObservableList<Reader> fingerprintReaders = FXCollections.emptyObservableList();
    private Reader fingerPrintReader;
    private final Fid.Format fingerprintImageFormat = Fid.Format.ANSI_381_2004;
    private final Reader.ImageProcessing fingerprintImageProc = Reader.ImageProcessing.IMG_PROC_DEFAULT;
    private ObjectProperty<Image> fingerprintImageProperty = new SimpleObjectProperty<Image>();
    private CaptureTask captureFingerPrintTask;
    private EnrollmentTask enrollmentTask = null;

    private ObservableList<Webcam> webcams = FXCollections.emptyObservableList();
    private Webcam webCam = null;
    private boolean stopCamera = false;
    private BufferedImage grabbedImage;
    private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<Image>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {

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

        webcamsComboBox.setConverter(new StringConverter() {
            @Override
            public String toString(Object object) {
                return (object != null) ? ((Webcam) object).getDevice().getName() : "";
            }

            @Override
            public Object fromString(String string) {
                return null;
            }
        });

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
                                Platform.runLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        studentPassportImageView.setImage(ref.get());
                                        cancelCaptureImageButton.setDisable(false);
                                        captureImageButton.setDisable(true);
                                        stopCamera = true;
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                };

                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        webCamStatusLabel.textProperty().bind(task.messageProperty());
                    }
                });

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
                    if (newValue == null) {
                        fingerprintReaderStatusLabel.setText("Please select a reader from the list.");
                        return;
                    }

                    if (enrollmentTask != null) {
                        enrollmentTask.cancel();
                    }

                    boolean isStreaming = false;

                    Reader reader = (Reader) newValue;

                    fingerprintReaderStatusLabel.setText("Selected reader: " + reader.GetDescription().id.product_name);

                    try {
                        enrollmentTask = new EnrollmentTask(reader, isStreaming);
                    } catch(UareUException ex) {
                        System.out.println("Enrollment Task ......");
                    }

                    enrollmentTask.valueProperty().addListener(new ChangeListener<EnrollmentEvent>() {
                        @Override
                        public void changed(
                                ObservableValue<? extends EnrollmentEvent> observable,
                                EnrollmentEvent oldValue,
                                EnrollmentEvent newValue
                        ) {

                            processEnrollmentEventUpdate(newValue);

                        }
                    });

                    enrollmentTask.setOnSucceeded(t -> {
                        System.out.println("Successful enrollment" + t.getSource().getValue());
                    });

                    enrollmentTask.setOnFailed(f -> {
                        f.getSource().getException().printStackTrace();
                    });

                    enrollmentTask.setOnCancelled(f -> {
                        System.out.println("Enrollment cancelled...");
                    });

                    //studentFingerprintImageView.imageProperty().unbind();
                    //studentFingerprintImageView.imageProperty().bind(captureFingerprintService.valueFingerprintImageProperty());

                    new Thread(enrollmentTask).start();

                });

        captureFingerPrintButton.setOnAction(event -> {

        });

        refreshFingerprintReadersButton.setOnAction(event -> {
            refreshFingerprintReaders();
        });
    }

    private void processEnrollmentEventUpdate(EnrollmentEvent enrollmentEvent) {
        String action = enrollmentEvent.action;
        Reader.Status readerStatus  = enrollmentEvent.readerStatus;
        Fmd fmd = enrollmentEvent.fmd;
        CaptureTask.CaptureTaskResult ct = enrollmentEvent.captureTaskResult;

        if(action.equals(EnrollmentEvent.ACT_PROMPT)){
            fingerprintReaderStatusLabel.setText("Place any finger on the reader.");
        }
        else if(action.equals(EnrollmentEvent.ACT_CAPTURE)){
            if(ct == null) {
                System.out.println("ACT CapTURE result is null");
            } else if(null != ct.captureResult){
                System.out.println("Quality : " + ct.captureResult.quality);
            }
            else if(null != readerStatus){
                System.out.println("Reader Status: " + readerStatus.status.name());
            }
        }
        else if(action.equals(EnrollmentEvent.ACT_FEATURES)){
            System.out.println("Feature extraction ...");
        }
        else if(action.equals(EnrollmentEvent.ACT_DONE)){
            if(null == fmd){
                String str = String.format("    enrollment template created, size: %d\n\n\n",fmd.getData().length);
                fingerprintReaderStatusLabel.setText(str);
            }
            else{
                System.out.println("Enrollment template creation error");
                // MessageBox.DpError("Enrollment template creation", evt.exception);
            }
        }
        else if(action.equals(EnrollmentEvent.ACT_CANCELED)){
            //canceled, destroy dialog
           System.out.println("Stop enrollment");
        }
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

        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                webCamStatusLabel.textProperty().bind(webCamTask.messageProperty());
                cancelCaptureImageButton.setDisable(true);
                captureImageButton.setDisable(false);
            }
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

                            Platform.runLater(new Runnable() {

                                @Override
                                public void run() {
                                    imageProperty.set(ref.get());
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }
        };

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                captureImageButton.setDisable(false);
                webCamStatusLabel.textProperty().bind(task.messageProperty());
                studentPassportImageView.imageProperty().bind(imageProperty);
            }
        });


        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();


    }
}
