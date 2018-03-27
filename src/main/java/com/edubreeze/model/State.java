package com.edubreeze.model;

import com.edubreeze.database.DatabaseHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.util.List;

@DatabaseTable(tableName = "states")
public class State {

    @DatabaseField(id = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String name;

    @ForeignCollectionField(eager = false)
    private ForeignCollection<Lga> lgas;

    public State() {
        // ORMLite needs a no-arg constructor
    }

    public State(int id, String name) {
        this.id = id;
        this.name = name;
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

    public ForeignCollection<Lga> getLgas() {
        return lgas;
    }

    public static List<State> getStates() throws SQLException {
        Dao<State, Integer> stateDao = DatabaseHelper.getStateDao();
        return stateDao.queryForAll();
    }

    public static State find(int stateId) throws SQLException{
        return  DatabaseHelper.getStateDao().queryForId(stateId);
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
        return id == ((State) other).getId();
    }
}
