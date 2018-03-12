package com.edubreeze.service.enrollment;

import com.digitalpersona.uareu.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;

public class EnrollmentTask extends Task<EnrollmentEvent> implements Engine.EnrollmentCallback{

    private Reader fingerprintReader;
    private boolean isStreaming;
    private CaptureTask captureTask;
    private CaptureTask.CaptureTaskResult captureTaskResult = null;

    public EnrollmentTask (Reader reader, boolean shouldStream) throws UareUException {
        fingerprintReader = reader;
        isStreaming = shouldStream;

        openReader();
    }

    public void setCaptureTaskResult(CaptureTask.CaptureTaskResult captureTaskResult) {
        this.captureTaskResult = captureTaskResult;
    }

    private void openReader() throws UareUException {
        fingerprintReader.Open(Reader.Priority.COOPERATIVE);

        Reader.Capabilities readerCapabilities = fingerprintReader.GetCapabilities();
        if (!readerCapabilities.can_stream) {
            isStreaming = false;
        }
    }

    @Override
    protected EnrollmentEvent call() throws UareUException {
        //acquire engine
        Engine engine = FingerPrintEnrollment.GetEngine();

        while(!isCancelled()){
            //run enrollment
            Fmd fmd = engine.CreateEnrollmentFmd(Fmd.Format.ANSI_378_2004, this);

            //send result
            if(null != fmd){
                broadcastEnrollmentEvent(EnrollmentEvent.ACT_DONE, fmd, null, null);
            }
            else{
                broadcastEnrollmentEvent(EnrollmentEvent.ACT_CANCELED, null, null, null);
                break;
            }
        }

        return this.getValue();
    }

    @Override
    public Engine.PreEnrollmentFmd GetFmd(Fmd.Format format) {
        Engine.PreEnrollmentFmd prefmd = null;

        while(null == prefmd && !isCancelled()){
            captureTaskResult = null;
            //start capture thread
            captureTask = new CaptureTask(fingerprintReader, isStreaming,Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT);

            captureTask.valueProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue != null) {
                    setCaptureTaskResult(newValue);
                }
            });

            // CaptureThread(m_reader, false, Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT);

            Thread taskThread = new Thread(captureTask);
            taskThread.start();

            // captureTask.start(null);

            //prompt for finger
            broadcastEnrollmentEvent(EnrollmentEvent.ACT_PROMPT, null, null, null);
            //updateTitle(EnrollmentEvent.ACT_PROMPT);

            // SendToListener(ACT_PROMPT, null, null, null, null);
            // System.out.println("Waiting");
            try {
                //wait till done
                taskThread.join(0);
                // System.out.println("Waited");
            }
            catch (InterruptedException ex) { ex.printStackTrace(System.out); }

            // System.out.println("should be seen after waited");
             // = captureTask. ();
            //CaptureTask.CaptureTaskResult captureTaskResult = this.captureTaskResult;
            if( captureTaskResult != null && captureTaskResult.captureResult != null){
                if(Reader.CaptureQuality.CANCELED == captureTaskResult.captureResult.quality){
                    //capture canceled, return null
                    break;
                }
                else if(captureTaskResult.captureResult.image != null && Reader.CaptureQuality.GOOD == captureTaskResult.captureResult.quality){
                    //acquire engine
                    Engine engine = UareUGlobal.GetEngine();

                    try{
                        //extract features
                        Fmd fmd = engine.CreateFmd(captureTaskResult.captureResult.image, Fmd.Format.ANSI_378_2004);

                        //return prefmd
                        prefmd = new Engine.PreEnrollmentFmd();
                        prefmd.fmd = fmd;
                        prefmd.view_index = 0;

                        //send success
                        broadcastEnrollmentEvent(EnrollmentEvent.ACT_FEATURES, null, null, null);
                        updateTitle(EnrollmentEvent.ACT_FEATURES);
                        // SendToListener(ACT_FEATURES, null, null, null, null);
                    }
                    catch(UareUException ex){
                        ex.printStackTrace(System.out);
                        //send extraction error
                       // SendToListener(ACT_FEATURES, null, null, null, e);
                    }
                }
                else{
                    //send quality result
                    broadcastEnrollmentEvent(EnrollmentEvent.ACT_CAPTURE, null, captureTaskResult, captureTaskResult.readerStatus);
                    // SendToListener(ACT_CAPTURE, null, evt.capture_result, evt.reader_status, evt.exception);
                }
            }
            else{
                //send capture error
                broadcastEnrollmentEvent(EnrollmentEvent.ACT_CAPTURE, null, null, null);
                // SendToListener(ACT_CAPTURE, null, evt.capture_result, evt.reader_status, evt.exception);
            }
        }

        return prefmd;
    }

    private void broadcastEnrollmentEvent(String action, Fmd fmd, CaptureTask.CaptureTaskResult cr, Reader.Status st) {
        final EnrollmentEvent enrollEvent = new EnrollmentEvent(action, fmd, cr, st);

        updateValue(enrollEvent);
    }
}
