package com.edubreeze.service.enrollment;

import com.digitalpersona.uareu.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicReference;

public class CaptureService extends Service<CaptureTask.CaptureTaskResult> implements Engine.EnrollmentCallback{

    private final Reader fingerprintReader;
    private boolean isStreaming;
    private Fid.Format imageFormat;
    private Reader.ImageProcessing imageProcessing;
    private ObjectProperty<CaptureTask.CaptureTaskResult> fingerprintCaptureResult = new SimpleObjectProperty<>();
    private ObjectProperty<Image> fingerprintImageProperty = new SimpleObjectProperty<>();
    private boolean shouldEnroll = false;

    public CaptureService(Reader fpReader, boolean isStream, Fid.Format imgFormat, Reader.ImageProcessing imgProc) throws UareUException {
        fingerprintReader = fpReader;
        isStreaming = isStream;
        imageFormat = imgFormat;
        imageProcessing = imgProc;

        openReader();
    }

    public CaptureService(Reader fpReader, boolean isStream) throws UareUException {
        fingerprintReader = fpReader;
        isStreaming = isStream;
        imageFormat = Fid.Format.ANSI_381_2004;
        imageProcessing = Reader.ImageProcessing.IMG_PROC_DEFAULT;

        openReader();
    }

    @Override
    protected Task<CaptureTask.CaptureTaskResult> createTask() {
        fingerprintCaptureResult.unbind();

        CaptureTask newCaptureTask = new CaptureTask(fingerprintReader, isStreaming, imageFormat, imageProcessing);
        setCaptureTaskValueListener(newCaptureTask);

        return newCaptureTask;
    }














    public boolean shouldEnroll() {
        return shouldEnroll;
    }

    public void setShouldEnroll(boolean shouldEnroll) {
        this.shouldEnroll = shouldEnroll;
    }

    public ObjectProperty<Image> valueFingerprintImageProperty() {
        return fingerprintImageProperty;
    }

    private void openReader() throws UareUException {
        fingerprintReader.Open(Reader.Priority.COOPERATIVE);

        Reader.Capabilities readerCapabilities = fingerprintReader.GetCapabilities();
        if (!readerCapabilities.can_stream) {
            isStreaming = false;
        }
    }

    public void setCaptureTaskValueListener(Task<CaptureTask.CaptureTaskResult> captureTask) {
        captureTask.valueProperty().addListener((observable, oldValue, captureResult) -> {
            if (captureResult != null) {

                /**
                 * updates fingerprintImageProperty, which can be observed to form a streaming view
                 */
                broadcastCaptureResultImage(captureResult);

                /**
                 * process capture and extract features if image quality is ok
                 */

            }

        });
    }

    private void enrollCaptureResult(CaptureTask.CaptureTaskResult captureTaskResult) throws UareUException {

    }

    private void broadcastCaptureResultImage(CaptureTask.CaptureTaskResult captureResult) {
        Fid.Fiv view = captureResult.captureResult.image.getViews()[0];

        final AtomicReference<WritableImage> ref = new AtomicReference<>();
        BufferedImage img = new BufferedImage(view.getWidth(), view.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        img.getRaster().setDataElements(0, 0, view.getWidth(), view.getHeight(), view.getImageData());
        ref.set(SwingFXUtils.toFXImage(img, ref.get()));
        img.flush();

        fingerprintImageProperty.set(ref.get());
    }

    @Override
    public Engine.PreEnrollmentFmd GetFmd(Fmd.Format format) {
        Engine.PreEnrollmentFmd prefmd = null;

        while(null == prefmd && isRunning()){
            // TODO: update status string observable property and set EnrollmentEvent so caller can get current enrollment value.

            //prompt for finger
            // SendToListener(ACT_PROMPT, null, null, null, null);
            System.out.println("Please put your Left middle or index finger");

            CaptureTask.CaptureTaskResult captureTaskResult = fingerprintCaptureResult.get();
            if (captureTaskResult != null) {
                if(captureTaskResult.captureResult.image != null && Reader.CaptureQuality.GOOD == captureTaskResult.captureResult.quality) {
                    Engine engine = UareUGlobal.GetEngine();

                    try {
                        Fmd fmd = engine.CreateFmd(captureTaskResult.captureResult.image, Fmd.Format.ANSI_378_2004);

                        //return prefmd
                        prefmd = new Engine.PreEnrollmentFmd();
                        prefmd.fmd = fmd;
                        prefmd.view_index = 0;

                    } catch (UareUException ex) {
                        ex.printStackTrace(System.out);
                    }
                }
            }
        }

        return prefmd;
    }
}
