package com.edubreeze.model;

import com.edubreeze.config.AppConfiguration;
import com.edubreeze.database.DatabaseHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@DatabaseTable(tableName = "schools")
public class School {

    @DatabaseField(id = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(canBeNull = false, foreign = true)
    private Lga lga;

    @DatabaseField
    private String code;

    @DatabaseField
    private Double longitude;

    @DatabaseField
    private Double latitude;

    @DatabaseField
    private int numberOfClasses;

    @DatabaseField
    private boolean electricity;

    @DatabaseField
    private boolean drinkingWater;

    @DatabaseField
    private int numberOfToilets;

    @DatabaseField
    private int numberOfUrinals;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public int getNumberOfClasses() {
        return numberOfClasses;
    }

    public void setNumberOfClasses(int numberOfClasses) {
        this.numberOfClasses = numberOfClasses;
    }

    public boolean isElectricity() {
        return electricity;
    }

    public void setElectricity(boolean electricity) {
        this.electricity = electricity;
    }

    public boolean isDrinkingWater() {
        return drinkingWater;
    }

    public void setDrinkingWater(boolean drinkingWater) {
        this.drinkingWater = drinkingWater;
    }

    public int getNumberOfToilets() {
        return numberOfToilets;
    }

    public void setNumberOfToilets(int numberOfToilets) {
        this.numberOfToilets = numberOfToilets;
    }

    public int getNumberOfUrinals() {
        return numberOfUrinals;
    }

    public void setNumberOfUrinals(int numberOfUrinals) {
        this.numberOfUrinals = numberOfUrinals;
    }

    public boolean canSave() {
        return (id > 0 && name != null & lga != null);
    }

    public School() {
        // ORMLite needs a no-arg constructor
    }

    public School(int id, String name, Lga lga) {
        this.id = id;
        this.name = name;
        this.lga = lga;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Lga getLga() {
        return lga;
    }

    public void setLga(Lga lga) {
        this.lga = lga;
    }

    public static List<School> getSchools() throws SQLException {
        Dao<School, Integer> schoolDao = DatabaseHelper.getSchoolDao();
        return schoolDao.queryForAll();
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != getClass()) {
            return false;
        }
        return id == ((School) other).getId();
    }

}
