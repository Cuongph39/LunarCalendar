package com.vanpt.lunarcalendar.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.vanpt.lunarcalendar.R;
import com.vanpt.lunarcalendar.interfaces.IDialogEventListener;

/**
 * Created by vanpt on 12/7/2016.
 */

public class GoToLunarDateFragment extends DialogFragment {

    private IDialogEventListener mListener;
    private EditText editTextLunarDate;
    private String lunarDateString;

    public String getLunarDateString() {
        return lunarDateString;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_go_to_lunar_date, null);
        editTextLunarDate = (EditText) v.findViewById(R.id.editTextLunarDate);
        builder.setView(v)
                .setTitle("Nhập ngày Âm lịch")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        lunarDateString = editTextLunarDate.getText().toString();
                        mListener.onDialogPositiveClick(GoToLunarDateFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDialogNegativeClick(GoToLunarDateFragment.this);
                    }
                });;
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (IDialogEventListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement IDialogEventListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (IDialogEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement IDialogEventListener");
        }
    }
}
