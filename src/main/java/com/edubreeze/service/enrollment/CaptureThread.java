package com.edubreeze.service.enrollment;

import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.UareUException;
import javafx.application.Platform;

public class CaptureThread extends Thread {
    public static final String ACT_CAPTURE = "capture_thread_captured";

    public class CaptureEvent {
        private static final long serialVersionUID = 101;

        public Reader.CaptureResult capture_result;
        public Reader.Status reader_status;
        public UareUException exception;
        public String action;
        public Object source;

        public CaptureEvent(Object src, String act, Reader.CaptureResult cr, Reader.Status st, UareUException ex) {
            source = src;
            action = act;
            capture_result = cr;
            reader_status = st;
            exception = ex;
        }
    }

    private CaptureActionListener captureListener;
    private boolean cancelled;
    private Reader fingerprintReader;
    private boolean shouldStream;
    private Fid.Format imageFormat;
    private Reader.ImageProcessing imageProcessing;
    private CaptureEvent lastCaptureEvent;

    public CaptureThread(Reader reader, boolean bStream, Fid.Format img_format, Reader.ImageProcessing img_proc) {
        cancelled = false;
        fingerprintReader = reader;
        shouldStream = bStream;
        imageFormat = img_format;
        imageProcessing = img_proc;
    }

    public void start(CaptureActionListener listener) {
        captureListener = listener;
        super.start();
    }

    public void join(int milliseconds) {
        try {
            super.join(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public CaptureEvent getLastCaptureEvent() {
        return lastCaptureEvent;
    }

    private void Capture() {
        try {
            //wait for reader to become ready
            boolean bReady = false;
            while (!bReady && !cancelled) {
                Reader.Status rs = fingerprintReader.GetStatus();
                if (Reader.ReaderStatus.BUSY == rs.status) {
                    //if busy, wait a bit
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                } else if (Reader.ReaderStatus.READY == rs.status || Reader.ReaderStatus.NEED_CALIBRATION == rs.status) {
                    //ready for capture
                    bReady = true;
                    break;
                } else {
                    //reader failure
                    NotifyListener(ACT_CAPTURE, null, rs, null);
                    break;
                }
            }
            if (cancelled) {
                Reader.CaptureResult cr = new Reader.CaptureResult();
                cr.quality = Reader.CaptureQuality.CANCELED;
                NotifyListener(ACT_CAPTURE, cr, null, null);
            }


            if (bReady) {
                //capture
                Reader.CaptureResult cr = fingerprintReader.Capture(imageFormat, imageProcessing, 500, -1);
                NotifyListener(ACT_CAPTURE, cr, null, null);
            }
        } catch (UareUException e) {
            NotifyListener(ACT_CAPTURE, null, null, e);
        }
    }

    private void Stream() {
        try {
            //wait for reader to become ready
            boolean isDeviceReady = false;
            while (!isDeviceReady && !cancelled) {
                Reader.Status rs = fingerprintReader.GetStatus();
                if (Reader.ReaderStatus.BUSY == rs.status) {
                    //if busy, wait a bit
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                } else if (Reader.ReaderStatus.READY == rs.status || Reader.ReaderStatus.NEED_CALIBRATION == rs.status) {
                    //ready for capture
                    isDeviceReady = true;
                    break;
                } else {
                    //reader failure
                    NotifyListener(ACT_CAPTURE, null, rs, null);
                    break;
                }
            }

            if (isDeviceReady) {
                //start streaming
                fingerprintReader.StartStreaming();

                //get images
                while (!cancelled) {
                    Reader.CaptureResult cr = fingerprintReader.GetStreamImage(imageFormat, imageProcessing, 500);
                    NotifyListener(ACT_CAPTURE, cr, null, null);
                }

                //stop streaming
                fingerprintReader.StopStreaming();
            }
        } catch (UareUException e) {
            NotifyListener(ACT_CAPTURE, null, null, e);
        }

        if (cancelled) {
            Reader.CaptureResult cr = new Reader.CaptureResult();
            cr.quality = Reader.CaptureQuality.CANCELED;
            NotifyListener(ACT_CAPTURE, cr, null, null);
        }
    }

    private void NotifyListener(String action, Reader.CaptureResult cr, Reader.Status st, UareUException ex) {
        final CaptureEvent evt = new CaptureEvent(this, action, cr, st, ex);

        //store last capture event
        lastCaptureEvent = evt;

        if (null == captureListener || null == action || action.equals("")) return;

        Platform.runLater(() -> captureListener.handleCaptureAction(evt));
    }

    public void cancel() {
        cancelled = true;
        try {
            if (!shouldStream) fingerprintReader.CancelCapture();
        } catch (UareUException e) {
        }
    }

    public void run() {
        if (shouldStream) {
            Stream();
        } else {
            Capture();
        }
    }
}

