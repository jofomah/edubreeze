package com.edubreeze.database.dao.observers;

import com.j256.ormlite.dao.Dao;

public class StudentDaoObserver implements Dao.DaoObserver {
    @Override
    public void onChange() {
        System.out.println("Student dao changed, run sync here");
    }
}
