package com.edubreeze.utils;

import com.edubreeze.model.StudentAcademicTerm;
import javafx.util.StringConverter;

public class StudentAcademicTermStringConverter extends StringConverter {

    @Override
    public String toString(Object object) {
        if(object == null) {
            return "";
        }
        StudentAcademicTerm studentAcademicTerm = ((StudentAcademicTerm) object);

        return (studentAcademicTerm.getYear() + " - " + studentAcademicTerm.getTerm());
    }

    @Override
    public Object fromString(String string) {
        return null;
    }
}
