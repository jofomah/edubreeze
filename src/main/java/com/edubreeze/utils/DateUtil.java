package com.edubreeze.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class DateUtil {

    public static int getAge(Date dateOfBirth) {
        LocalDate dob = dateOfBirth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate now = LocalDate.now(ZoneId.systemDefault());

        Period period = Period.between(dob, now);

        return period.getYears();
    }
}
