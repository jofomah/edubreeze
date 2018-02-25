package com.edubreeze.model;

import com.j256.ormlite.field.DatabaseField;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class Student {

    @DatabaseField(generatedId = true, allowGeneratedIdInsert=true)
    private UUID autoId;

    @DatabaseField(canBeNull = false)
    private String firstName;

    @DatabaseField(canBeNull = false)
    private String lastName;

    @DatabaseField(canBeNull = false)
    private String gender;

    // due to Java restriction, i can not name a variable "class"
    @DatabaseField(canBeNull = false)
    private String currentClass;

    @DatabaseField(canBeNull = false, foreign = true)
    private School school;

    @DatabaseField(canBeNull = false, foreign = true)
    private Lga lga;

    @DatabaseField(canBeNull = false, foreign = true)
    private State state;

    @DatabaseField
    private Date dateOfBirth;

    @DatabaseField
    private Date dateEnrolled;

    @DatabaseField
    private String contactPersonName;

    @DatabaseField
    private String contactPersonPhoneNumber;

    @DatabaseField
    private String contactPersonAddress;

    @DatabaseField
    private String religion;

    public Student() {
        // ORMLite needs a no-arg constructor
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCurrentClass() {
        return currentClass;
    }

    public void setCurrentClass(String currentClass) {
        this.currentClass = currentClass;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public Lga getLga() {
        return lga;
    }

    public void setLga(Lga lga) {
        this.lga = lga;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Date getDateEnrolled() {
        return dateEnrolled;
    }

    public void setDateEnrolled(Date dateEnrolled) {
        this.dateEnrolled = dateEnrolled;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getContactPersonPhoneNumber() {
        return contactPersonPhoneNumber;
    }

    public void setContactPersonPhoneNumber(String contactPersonPhoneNumber) {
        this.contactPersonPhoneNumber = contactPersonPhoneNumber;
    }

    public String getContactPersonAddress() {
        return contactPersonAddress;
    }

    public void setContactPersonAddress(String contactPersonAddress) {
        this.contactPersonAddress = contactPersonAddress;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public UUID getAutoId() {
        return autoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(autoId, student.getAutoId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(autoId);
    }
}
