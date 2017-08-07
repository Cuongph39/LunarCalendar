package com.vanpt.lunarcalendar.interfaces;

import com.vanpt.lunarcalendar.models.DateObject;

/**
 * Created by vanpt on 11/23/2016.
 */

public interface ICommand {
    void execute(DateObject date) throws Exception;
}
