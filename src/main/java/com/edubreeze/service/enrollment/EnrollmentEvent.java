package com.edubreeze.service.enrollment;

import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.Reader;

public class EnrollmentEvent{

    public static final String ACT_PROMPT   = "enrollment_prompt";
    public static final String ACT_CAPTURE  = "enrollment_capture";
    public static final String ACT_FEATURES = "enrollment_features";
    public static final String ACT_DONE     = "enrollment_done";
    public static final String ACT_CANCELED = "enrollment_canceled";

    public final String action;
    public final Fmd fmd;
    public final CaptureTask.CaptureTaskResult captureTaskResult;
    public final Reader.Status readerStatus;

    public EnrollmentEvent(String act, Fmd fmd, CaptureTask.CaptureTaskResult cr, Reader.Status st) {
        action = act;
        this.fmd = fmd;
        captureTaskResult = cr;
        readerStatus = st;
    }
}
