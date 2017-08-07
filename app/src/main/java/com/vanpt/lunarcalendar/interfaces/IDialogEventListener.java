package com.vanpt.lunarcalendar.interfaces;

import android.app.DialogFragment;

/**
 * Created by vanpt on 12/6/2016.
 */

public interface IDialogEventListener {
    void onDialogPositiveClick(DialogFragment dialog);
    void onDialogNegativeClick(DialogFragment dialog);
}
