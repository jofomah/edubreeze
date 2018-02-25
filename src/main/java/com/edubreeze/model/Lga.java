package com.edubreeze.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * An administrative location class for Local Government Areas (Based on Nigeria's admin levels)
 */
@DatabaseTable(tableName = "lgas")
public class Lga {

    @DatabaseField(id = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(canBeNull = false, foreign = true)
    private State state;

    public Lga() {
        // ORMLite needs a no-arg constructor
    }

    public Lga(int id, String name, State state) {
        this.id = id;
        this.name = name;
        this.state = state;
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

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
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
        return id == ((Lga) other).getId();
    }
}
