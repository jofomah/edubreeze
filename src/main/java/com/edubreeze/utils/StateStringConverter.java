package com.edubreeze.utils;

import com.edubreeze.model.State;
import javafx.util.StringConverter;

public class StateStringConverter extends StringConverter {
    @Override
    public String toString(Object object) {
        return (object == null) ? "" : ((State) object).getName();
    }

    @Override
    public Object fromString(String string) {
        return null;
    }
}
