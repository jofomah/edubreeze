package com.edubreeze.utils;

import com.edubreeze.model.AcademicRecord;
import com.edubreeze.model.StudentAcademicTerm;
import com.edubreeze.model.properties.AcademicRecordProperty;
import com.edubreeze.model.properties.StudentAcademicTermProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class DataUtil {

    public static ObservableList<StudentAcademicTermProperty>  convertToAcademicTermTableRowData(List<StudentAcademicTerm> academicTermList) {
        List<StudentAcademicTermProperty> academicTerms = new ArrayList<>();
        for(StudentAcademicTerm academicTerm : academicTermList) {
            academicTerms.add(new StudentAcademicTermProperty(
                    new SimpleStringProperty(academicTerm.getYear()),
                    new SimpleStringProperty(academicTerm.getTerm()),
                    new SimpleIntegerProperty(academicTerm.getDaysPresent()),
                    new SimpleIntegerProperty(academicTerm.getDaysAbsent())
            ));
        }
        return FXCollections.observableArrayList(academicTerms);
    }

    public static ObservableList<AcademicRecordProperty> convertToAcademicRecordTableRowData(List<AcademicRecord> academicRecords) {
        List<AcademicRecordProperty> academicRecordProperties = new ArrayList<>();
        for(AcademicRecord academicRecord : academicRecords) {
            AcademicRecordProperty row = new AcademicRecordProperty(
                    new SimpleStringProperty(academicRecord.getAcademicTerm().getYear() + " - " + academicRecord.getAcademicTerm().getTerm() ),
                    new SimpleStringProperty(academicRecord.getSubject()),
                    new SimpleIntegerProperty(academicRecord.getContinuousAssessmentScore()),
                    new SimpleIntegerProperty(academicRecord.getExamScore()),
                    new SimpleIntegerProperty(academicRecord.getTotalScore())
            );

            academicRecordProperties.add(row);
        }

        return FXCollections.observableList(academicRecordProperties);
    }
}
