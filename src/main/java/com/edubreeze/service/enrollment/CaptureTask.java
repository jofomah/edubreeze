package com.edubreeze.service.enrollment;

import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.UareUException;
import javafx.concurrent.Task;

public class CaptureTask extends Task<CaptureTask.CaptureTaskResult> {
    public final static String ACT_CAPTURE = "capture_task_captured";
    private final static int WAIT_TIME_IN_MILLISECONDS = 100;

    private Reader fingerprintReader = null;
    private boolean shouldStream;
    private Fid.Format imageFormat;
    private Reader.ImageProcessing imageProcessing;
    private CaptureTaskResult lastCaptureTaskResult;

    public CaptureTask(Reader fpReader, boolean isStream, Fid.Format imgFormat, Reader.ImageProcessing imgProc) {
        fingerprintReader = fpReader;
        shouldStream = isStream;
        imageFormat = imgFormat;
        imageProcessing = imgProc;
    }

    public static class CaptureTaskResult {
        public final Reader.CaptureResult captureResult;
        public final String action;
        public final Reader.Status readerStatus;

        public CaptureTaskResult(String act, Reader.CaptureResult cr, Reader.Status st) {
            captureResult = cr;
            action = act;
            readerStatus = st;
        }
    }

    @Override
    protected CaptureTaskResult call() throws ReaderFailureException, UareUException {
        if (shouldStream) {
            stream();
        } else {
            capture();
        }

        return lastCaptureTaskResult;
    }

    private Reader.Status getDeviceStatus() throws UareUException, ReaderFailureException {
        //wait for reader to become ready
        boolean isDeviceReady = false;
        Reader.Status rs = null;

        while (!isDeviceReady && !isCancelled()) {
            rs = fingerprintReader.GetStatus();

            if (Reader.ReaderStatus.BUSY == rs.status) {
                //if busy, wait a bit
                try {
                    Thread.sleep(WAIT_TIME_IN_MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }

            } else if (Reader.ReaderStatus.READY == rs.status || Reader.ReaderStatus.NEED_CALIBRATION == rs.status) {
                //ready for capture
                isDeviceReady = true;

            } else {
                //reader failure
                throw new ReaderFailureException("Fingerprint reader failed exception");
            }
        }

        return rs;
    }

    private void stream() throws ReaderFailureException, UareUException {
        Reader.Status readerStatus = getDeviceStatus();

        if (readerStatus != null) {
            //start streaming
            fingerprintReader.StartStreaming();

            //get images
            while (!isCancelled() && shouldStream) {
                Reader.CaptureResult cr = fingerprintReader.GetStreamImage(imageFormat, imageProcessing, 500);

                broadcastCaptureResult(ACT_CAPTURE, cr, readerStatus);
            }

            //stop streaming
            fingerprintReader.StopStreaming();
        }
    }

    private void capture() throws ReaderFailureException, UareUException {
        Reader.Status readerStatus = getDeviceStatus();
        if (readerStatus != null) {
            Reader.CaptureResult cr = fingerprintReader.Capture(imageFormat, imageProcessing, 500, -1);

            broadcastCaptureResult(ACT_CAPTURE, cr, readerStatus);
        }
    }

    private void broadcastCaptureResult(String action, Reader.CaptureResult cr, Reader.Status rs) {
        lastCaptureTaskResult = new CaptureTaskResult(action, cr, rs);

        updateValue(lastCaptureTaskResult);
    }
}
