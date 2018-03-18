package com.edubreeze.model.properties;

import com.edubreeze.model.StudentAcademicTerm;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

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
}
