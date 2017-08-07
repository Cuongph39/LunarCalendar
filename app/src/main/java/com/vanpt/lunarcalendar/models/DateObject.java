package com.vanpt.lunarcalendar.models;

/**
 * Created by vanpt on 11/23/2016.
 */

public class DateObject {
    private int day;
    private int month;
    private int year;
    private int leap = 0;
    private int hourOfDay = 0;
    private int minute = 0;

    public DateObject(int day, int month, int year) throws Exception {
        this.setDay(day);
        this.setMonth(month);
        this.year = year;
    }

    public DateObject(int day, int month, int year, int leap) throws Exception {
        this.setDay(day);
        this.setMonth(month);
        this.year = year;
        this.leap = leap;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) throws Exception {
        if (day < 1 || day > 31) {
            throw new Exception("day must be >= 1 and <=31");
        }
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) throws Exception {
        if (month > 12 || month < 1) {
            throw new Exception("month must be >= 1 and <= 12");
        }
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getLeap() {
        return leap;
    }

    public void setLeap(int leap) {
        this.leap = leap;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
