package com.edubreeze.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.ForeignCollectionField;

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
