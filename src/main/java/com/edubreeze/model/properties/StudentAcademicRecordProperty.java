package com.edubreeze.model.properties;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class StudentAcademicRecordProperty {
    private final SimpleStringProperty subject;
    private final SimpleStringProperty year;
    private final SimpleStringProperty term;
    private final SimpleIntegerProperty daysPresent;
    private final SimpleIntegerProperty daysAbsent;
    private final SimpleIntegerProperty caScore;
    private final SimpleIntegerProperty examScore;
    private final SimpleIntegerProperty totalScore;

    public StudentAcademicRecordProperty(SimpleStringProperty subject, SimpleStringProperty year, SimpleStringProperty term, SimpleIntegerProperty daysPresent, SimpleIntegerProperty daysAbsent, SimpleIntegerProperty caScore, SimpleIntegerProperty examScore, SimpleIntegerProperty totalScore) {
        this.subject = subject;
        this.year = year;
        this.term = term;
        this.daysPresent = daysPresent;
        this.daysAbsent = daysAbsent;
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
}
