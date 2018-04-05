package com.edubreeze.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtil {

    public static int getAge(Date dateOfBirth) {
        LocalDate dob = convertDate(dateOfBirth);
        LocalDate now = LocalDate.now(ZoneId.systemDefault());

        Period period = Period.between(dob, now);

        return period.getYears();
    }

    public static LocalDate convertDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Date getDateFromTimestampInSeconds(Long timestamp) {
        if(timestamp == null) {
            return null;
        }

        long secondsThreshold = 1000000000;

        if(timestamp == 0) {
            return new Date(0);
        }

        if(timestamp <= secondsThreshold) {
            return new Date(TimeUnit.MILLISECONDS.convert(timestamp, TimeUnit.SECONDS));
        }

       return new Date(timestamp);
    }

    public static Long convertDateToSeconds(Date date) {
        if(date == null) {
            return 0L;
        }

        return date.toInstant().getEpochSecond();
    }
}
