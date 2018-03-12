package com.edubreeze.service.enrollment;

import com.digitalpersona.uareu.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class FingerPrintEnrollment {

    public static final String ACT_PROMPT   = "enrollment_prompt";
    public static final String ACT_CAPTURE  = "enrollment_capture";
    public static final String ACT_FEATURES = "enrollment_features";
    public static final String ACT_DONE     = "enrollment_done";
    public static final String ACT_CANCELED = "enrollment_canceled";


    public static List<Reader> getReaders() throws UareUException {
        ReaderCollection readerCollection = UareUGlobal.GetReaderCollection();
        // get available readers
        readerCollection.GetReaders();

        // retrieve readers and build list
        List<Reader> readers = new ArrayList<>();
        for(int i = 0; i < readerCollection.size(); i++){
            readers.add(readerCollection.get(i));
        }
        return readers;
    }

    public static void destroyReaders() throws UareUException {
        UareUGlobal.DestroyReaderCollection();
    }

    public static Engine GetEngine() {
        return UareUGlobal.GetEngine();
    }
}
