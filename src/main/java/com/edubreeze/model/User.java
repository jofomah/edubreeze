package com.edubreeze.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Objects;

@DatabaseTable(tableName = "users")
public class User {

    @DatabaseField(id = true)
    private String username;

    @DatabaseField(canBeNull = false)
    private String password;

    @DatabaseField
    private String apiToken;

    public User() {
        // ORMLite needs a no-arg constructor
    }

    public User(String username, String password, String apiToken) {
        this.username = username;
        this.password = password;
        this.apiToken = apiToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.getUsername());
    }

    @Override
    public int hashCode() {

        return Objects.hash(username);
    }
}
