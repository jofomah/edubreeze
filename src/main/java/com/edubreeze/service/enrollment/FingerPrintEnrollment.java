package com.edubreeze.service.enrollment;

import com.digitalpersona.uareu.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class FingerPrintEnrollment {

    public enum FingerType {
        RIGHT_THUMB("Right Thumb Finger"),
        RIGHT_INDEX("Right Index Finger"),
        RIGHT_MIDDLE("Right Middle Finger"),
        RIGHT_RING("Right Ring Finger"),
        RIGHT_PINKY("Right Pinky Finger"),
        LEFT_THUMB("Left Thumb Finger"),
        LEFT_INDEX("Left Index Finger"),
        LEFT_MIDDLE("Left Middle Finger"),
        LEFT_RING("Left Ring Finger"),
        LEFT_PINKY("Left Pinky Finger"),
        NO_FINGER("No Finger");


        private String fingerName;

        FingerType(String fpName) {
            this.fingerName = fpName;
        }

        public String fingerName() {
            return fingerName;
        }

        public static FingerType getFromFingerName(String fingerName) {
            for(FingerType type : FingerPrintEnrollment.FingerType.values()) {
                if(type.fingerName().equalsIgnoreCase(fingerName)) {
                    return type;
                }
            }
            return null;
        }
    }

    public static List<Reader> getReaders() throws UareUException {
        ReaderCollection readerCollection = UareUGlobal.GetReaderCollection();
        // get available readers
        readerCollection.GetReaders();

        // retrieve readers and build list
        List<Reader> readers = new ArrayList<>();
        for (int i = 0; i < readerCollection.size(); i++) {
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
