package com.edubreeze.utils;

import com.edubreeze.model.Lga;
import javafx.util.StringConverter;

public class LgaStringConverter extends StringConverter {
    @Override
    public String toString(Object object) {
        return (object == null) ? "" : ((Lga) object).getName();
    }

    @Override
    public Object fromString(String string) {
        return null;
    }
}
