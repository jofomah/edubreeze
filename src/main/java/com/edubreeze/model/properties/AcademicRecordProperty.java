package com.edubreeze.model.properties;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class AcademicRecordProperty {
    private final SimpleStringProperty academicTerm;
    private final SimpleStringProperty subject;
    private final SimpleIntegerProperty caScore;
    private final SimpleIntegerProperty examScore;
    private final SimpleIntegerProperty totalScore;

    public AcademicRecordProperty(SimpleStringProperty academicTerm, SimpleStringProperty subject, SimpleIntegerProperty caScore, SimpleIntegerProperty examScore, SimpleIntegerProperty totalScore) {
        this.academicTerm = academicTerm;
        this.subject = subject;
        this.caScore = caScore;
        this.examScore = examScore;
        this.totalScore = totalScore;
    }

    public String getSubject() {
        return subject.get();
    }

    public SimpleStringProperty subjectProperty() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject.set(subject);
    }

    public int getCaScore() {
        return caScore.get();
    }

    public SimpleIntegerProperty caScoreProperty() {
        return caScore;
    }

    public void setCaScore(int caScore) {
        this.caScore.set(caScore);
    }

    public int getExamScore() {
        return examScore.get();
    }

    public SimpleIntegerProperty examScoreProperty() {
        return examScore;
    }

    public void setExamScore(int examScore) {
        this.examScore.set(examScore);
    }

    public int getTotalScore() {
        return totalScore.get();
    }

    public SimpleIntegerProperty totalScoreProperty() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore.set(totalScore);
    }

    public String getAcademicTerm() {
        return academicTerm.get();
    }

    public SimpleStringProperty academicTermProperty() {
        return academicTerm;
    }

    public void setAcademicTerm(String academicTerm) {
        this.academicTerm.set(academicTerm);
    }

    public static List<TableColumn> getAcademicRecordsTableColumns() {
        TableColumn academicTermCol = new TableColumn("Academic term");
        academicTermCol.setCellValueFactory(
                new PropertyValueFactory<AcademicRecordProperty, String>("academicTerm"));

        TableColumn subjectCol = new TableColumn("Subject");
        subjectCol.setCellValueFactory(
                new PropertyValueFactory<AcademicRecordProperty, String>("subject"));

        TableColumn caCol = new TableColumn("C.A");
        caCol.setCellValueFactory(
                new PropertyValueFactory<AcademicRecordProperty, Integer>("caScore"));

        TableColumn examCol = new TableColumn("Exam");
        examCol.setCellValueFactory(
                new PropertyValueFactory<AcademicRecordProperty, Integer>("examScore"));

        TableColumn totalCol = new TableColumn("Total");
        totalCol.setCellValueFactory(
                new PropertyValueFactory<AcademicRecordProperty, Integer>("totalScore"));

        List<TableColumn> tableColumns = new ArrayList<>();

        tableColumns.add(academicTermCol);
        tableColumns.add(subjectCol);
        tableColumns.add(caCol);
        tableColumns.add(examCol);
        tableColumns.add(totalCol);

        return tableColumns;

    }
 }
