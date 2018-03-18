package com.edubreeze.utils;

import com.github.sarxos.webcam.Webcam;
import javafx.util.StringConverter;

public class WebcamStringConverter extends StringConverter {
    @Override
    public String toString(Object object) {
        return (object != null) ? ((Webcam) object).getDevice().getName() : "";
    }

    @Override
    public Object fromString(String string) {
        return null;
    }
}
