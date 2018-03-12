package com.edubreeze.service.enrollment;

import com.digitalpersona.uareu.Reader;
import javafx.util.StringConverter;

public class ReaderStringConverter extends StringConverter<Reader> {
    @Override
    public String toString(Reader reader) {
        if(reader == null) {
            return "Reader not available";
        }
        if(reader.GetDescription().id != null && reader.GetDescription().id.product_name != null) {
            return reader.GetDescription().id.product_name;
        }
        return reader.GetDescription().name;
    }

    @Override
    public Reader fromString(String string) {
        return null;
    }
}
