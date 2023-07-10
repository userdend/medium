package com.boyd.myapplication.util;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateConfiguration {
    public String date;

    public DateConfiguration(String date) {
        this.date = date;
    }

    public String timestampInMilliseconds() {
        LocalDateTime localDateTime = LocalDateTime.now();
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        long dateInMilliseconds = zonedDateTime.toInstant().toEpochMilli();
        return String.valueOf(dateInMilliseconds);
    }

    public String timeDifference() {
        long dateInMilliseconds = date != null ? Long.parseLong(date) : 0;
        long timeDifference = ((new Date()).getTime() - dateInMilliseconds) / 1000;
        long singleDigit;
        String displayTime = "";
        if (timeDifference < 60) {
            displayTime = "Just now";
        }
        if (timeDifference > 60 && timeDifference < 3600) {
            singleDigit = timeDifference / 60;
            if (singleDigit > 1) {
                displayTime = timeDifference / 60 + " minutes ago";
            } else {
                displayTime = timeDifference / 60 + " minute ago";
            }
        }
        if (timeDifference > 3600 && timeDifference < 86400) {
            singleDigit = timeDifference / 3600;
            if (singleDigit > 1) {
                displayTime = timeDifference / 3600 + " hours ago";
            } else {
                displayTime = timeDifference / 3600 + " hour ago";
            }
        }
        if (timeDifference > 86400 && timeDifference < 604800) {
            singleDigit = timeDifference / 86400;
            if (singleDigit > 1) {
                displayTime = timeDifference / 86400 + " days ago";
            } else {
                displayTime = timeDifference / 86400 + " day ago";
            }
        }
        if (timeDifference > 604800) {
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("E, MMM dd yyyy");
            Date dateNormal = new Date(dateInMilliseconds);
            displayTime = dateFormat.format(dateNormal);
        }
        return displayTime + "";
    }
}
