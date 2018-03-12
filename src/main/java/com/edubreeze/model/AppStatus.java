package com.edubreeze.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Objects;

@DatabaseTable(tableName = "app_statuses")
public class AppStatus {

    @DatabaseField(id = true)
    private String key;

    @DatabaseField(canBeNull = false)
    private String value;

    public AppStatus() {
        // ORMLite needs a no-arg constructor
    }

    public AppStatus(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppStatus appStatus = (AppStatus) o;
        return Objects.equals(key, appStatus.getKey());
    }

    @Override
    public int hashCode() {

        return Objects.hash(key);
    }
}
