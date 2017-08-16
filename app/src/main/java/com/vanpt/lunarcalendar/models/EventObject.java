package com.vanpt.lunarcalendar.models;

import com.vanpt.lunarcalendar.R;

import java.util.Date;

/**
 * Created by vanpt on 11/24/2016.
 */

public class EventObject {
    private int id;
    private String name;
    private int color;
    private String location;
    private Date fromDate = new Date();
    private Date toDate = new Date();
    private boolean isAllDayEvent = false;
    private RepetitionTypeEnum repetitionType = RepetitionTypeEnum.ONCE;

    public EventObject(String name) {
        this.name = name;
        this.color = R.color.colorRed;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public boolean isAllDayEvent() {
        return isAllDayEvent;
    }

    public void setAllDayEvent(boolean allDayEvent) {
        isAllDayEvent = allDayEvent;
    }

    public RepetitionTypeEnum getRepetitionType() {
        return repetitionType;
    }

    public void setRepetitionType(RepetitionTypeEnum repetitionType) {
        this.repetitionType = repetitionType;
    }

    public void setRepetitionType(int value) {
        switch (value) {
            case 0:
                this.repetitionType = RepetitionTypeEnum.ONCE;
                break;
            case 1:
                this.repetitionType = RepetitionTypeEnum.DAILY;
                break;
            case 2:
                this.repetitionType = RepetitionTypeEnum.WEEKLY;
                break;
            case 3:
                this.repetitionType = RepetitionTypeEnum.MONTHLY;
                break;
            case 4:
                this.repetitionType = RepetitionTypeEnum.YEARLY;
                break;
            default:
                this.repetitionType = RepetitionTypeEnum.ONCE;
                break;
        }
    }
}
