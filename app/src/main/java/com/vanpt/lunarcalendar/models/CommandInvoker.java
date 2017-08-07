package com.vanpt.lunarcalendar.models;

import com.vanpt.lunarcalendar.interfaces.ICommand;

import java.util.Hashtable;

/**
 * Created by vanpt on 11/24/2016.
 */

public class CommandInvoker {
    private Hashtable<Integer, ICommand> commands = new Hashtable<>();

    public void setCommand(int id, ICommand command) {
        if (!commands.containsKey(id)) {
            commands.put(id, command);
        }
    }

    public void executeCommand(int id, DateObject date) throws Exception {
        if (commands.containsKey(id)) {
            commands.get(id).execute(date);
        }
    }
}
