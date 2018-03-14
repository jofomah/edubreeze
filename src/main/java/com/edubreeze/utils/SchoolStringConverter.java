package com.edubreeze.utils;

import com.edubreeze.model.School;
import javafx.util.StringConverter;

public class SchoolStringConverter extends StringConverter {
    @Override
    public String toString(Object object) {
        return (object == null) ? "" : ((School) object).getName();
    }

    @Override
    public Object fromString(String string) {
        return null;
    }
}
