package com.edubreeze.service.enrollment;

import com.digitalpersona.uareu.*;
import javafx.application.Platform;

public class EnrollmentThread
        extends Thread
        implements Engine.EnrollmentCallback {
    public static final String ACT_PROMPT = "enrollment_prompt";
    public static final String ACT_CAPTURE = "enrollment_capture";
    public static final String ACT_FEATURES = "enrollment_features";
    public static final String ACT_DONE = "enrollment_done";
    public static final String ACT_CANCELED = "enrollment_canceled";

    public class EnrollmentEvent {
        private static final long serialVersionUID = 102;

        public Reader.CaptureResult capture_result;
        public Reader.Status reader_status;
        public UareUException exception;
        public Fmd enrollment_fmd;
        public String action;
        public Object source;

        public EnrollmentEvent(Object src, String act, Fmd fmd, Reader.CaptureResult cr, Reader.Status st, UareUException ex) {
            source = src;
            action = act;
            capture_result = cr;
            reader_status = st;
            exception = ex;
            enrollment_fmd = fmd;
        }
    }

    private final Reader fingerprintReader;
    private CaptureThread captureThread;
    private EnrollmentActionListener enrollmentEventListener;
    private boolean isCancelled;
    private boolean isStreaming = false;

    public EnrollmentThread(Reader reader, EnrollmentActionListener listener) {
        fingerprintReader = reader;
        enrollmentEventListener = listener;
    }

    public Engine.PreEnrollmentFmd GetFmd(Fmd.Format format) {
        Engine.PreEnrollmentFmd prefmd = null;

        while (null == prefmd && !isCancelled) {
            //start capture thread
            captureThread = new CaptureThread(fingerprintReader, isStreaming, Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT);
            captureThread.start(null);

            //prompt for finger
            SendToListener(ACT_PROMPT, null, null, null, null);

            //wait till done
            captureThread.join(0);

            //check result
            CaptureThread.CaptureEvent evt = captureThread.getLastCaptureEvent();
            if (null != evt.capture_result) {
                if (Reader.CaptureQuality.CANCELED == evt.capture_result.quality) {
                    //capture canceled, return null
                    break;
                } else if (null != evt.capture_result.image && Reader.CaptureQuality.GOOD == evt.capture_result.quality) {
                    //acquire engine
                    Engine engine = UareUGlobal.GetEngine();

                    try {
                        //extract features
                        Fmd fmd = engine.CreateFmd(evt.capture_result.image, Fmd.Format.ANSI_378_2004);

                        //return prefmd
                        prefmd = new Engine.PreEnrollmentFmd();
                        prefmd.fmd = fmd;
                        prefmd.view_index = 0;

                        //send success
                        SendToListener(ACT_FEATURES, null, evt.capture_result, null, null);

                    } catch (UareUException e) {
                        //send extraction error
                        SendToListener(ACT_FEATURES, null, null, null, e);
                    }
                } else {
                    //send quality result
                    SendToListener(ACT_CAPTURE, null, evt.capture_result, evt.reader_status, evt.exception);
                }
            } else {
                //send capture error
                SendToListener(ACT_CAPTURE, null, evt.capture_result, evt.reader_status, evt.exception);
            }
        }

        return prefmd;
    }

    public void cancel() {
        isCancelled = true;
        if (null != captureThread) {
            captureThread.cancel();
        }
    }

    private void SendToListener(String action, Fmd fmd, Reader.CaptureResult cr, Reader.Status st, UareUException ex) {
        if (null == enrollmentEventListener || null == action || action.equals("")) return;

        final EnrollmentEvent evt = new EnrollmentEvent(this, action, fmd, cr, st, ex);

        Platform.runLater(() -> enrollmentEventListener.handleEnrollmentAction(evt));
    }

    public void run() {
        //acquire engine
        Engine engine = UareUGlobal.GetEngine();

        try {
            isCancelled = false;
            while (!isCancelled) {
                //run enrollment
                Fmd fmd = engine.CreateEnrollmentFmd(Fmd.Format.ANSI_378_2004, this);

                //send result
                if (null != fmd) {
                    Reader.CaptureResult captureResult = null;
                    if(captureThread != null) {
                        CaptureThread.CaptureEvent evt = captureThread.getLastCaptureEvent();
                        captureResult = (evt != null)? evt.capture_result: null;
                    }

                    SendToListener(ACT_DONE, fmd, captureResult, null, null);
                } else {
                    SendToListener(ACT_CANCELED, null, null, null, null);
                    break;
                }
            }
        } catch (UareUException e) {
            SendToListener(ACT_DONE, null, null, null, e);
        }
    }
}
