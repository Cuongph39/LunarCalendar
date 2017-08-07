package com.vanpt.lunarcalendar.models;

/**
 * Created by vanpt on 12/3/2016.
 */

public enum RepetitionTypeEnum {
    ONCE(0),
    DAILY(1),
    WEEKLY(2),
    MONTHLY(3),
    YEARLY(4);

    private int value;

    RepetitionTypeEnum(int value) {

        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
