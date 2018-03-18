package com.edubreeze.model.properties;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class StudentAcademicTermProperty {

    private final SimpleStringProperty year;
    private final SimpleStringProperty term;
    private final SimpleIntegerProperty daysPresent;
    private final SimpleIntegerProperty daysAbsent;

    public StudentAcademicTermProperty(SimpleStringProperty year, SimpleStringProperty term, SimpleIntegerProperty daysPresent, SimpleIntegerProperty daysAbsent) {
        this.year = year;
        this.term = term;
        this.daysPresent = daysPresent;
        this.daysAbsent = daysAbsent;
    }

    public String getYear() {
        return year.get();
    }

    public SimpleStringProperty yearProperty() {
        return year;
    }

    public void setYear(String year) {
        this.year.set(year);
    }

    public String getTerm() {
        return term.get();
    }

    public SimpleStringProperty termProperty() {
        return term;
    }

    public void setTerm(String term) {
        this.term.set(term);
    }

    public int getDaysPresent() {
        return daysPresent.get();
    }

    public SimpleIntegerProperty daysPresentProperty() {
        return daysPresent;
    }

    public void setDaysPresent(int daysPresent) {
        this.daysPresent.set(daysPresent);
    }

    public int getDaysAbsent() {
        return daysAbsent.get();
    }

    public SimpleIntegerProperty daysAbsentProperty() {
        return daysAbsent;
    }

    public void setDaysAbsent(int daysAbsent) {
        this.daysAbsent.set(daysAbsent);
    }

    public static List<TableColumn> getAcademicTermTableColumns() {
        TableColumn yearCol = new TableColumn("Academic Year");
        yearCol.setCellValueFactory(
                new PropertyValueFactory<AcademicRecordProperty, String>("year"));

        TableColumn termCol = new TableColumn("Term");
        termCol.setCellValueFactory(
                new PropertyValueFactory<AcademicRecordProperty, String>("term"));

        TableColumn daysPresentCol = new TableColumn("Days Present");
        daysPresentCol.setCellValueFactory(
                new PropertyValueFactory<AcademicRecordProperty, Integer>("daysPresent"));

        TableColumn daysAbsentCol = new TableColumn("Days Absent");
        daysAbsentCol.setCellValueFactory(
                new PropertyValueFactory<AcademicRecordProperty, Integer>("daysAbsent"));

        List<TableColumn> tableColumns = new ArrayList<>();
        tableColumns.add(yearCol);
        tableColumns.add(termCol);
        tableColumns.add(daysAbsentCol);
        tableColumns.add(daysPresentCol);

        return tableColumns;
    }
}
