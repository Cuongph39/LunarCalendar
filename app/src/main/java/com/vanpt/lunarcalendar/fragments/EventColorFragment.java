package com.vanpt.lunarcalendar.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.vanpt.lunarcalendar.R;
import com.vanpt.lunarcalendar.interfaces.IDialogEventListener;

/**
 * Created by vanpt on 11/26/2016.
 */

public class EventColorFragment extends DialogFragment implements View.OnClickListener{

    private int selectedColor = R.color.colorRed;
    IDialogEventListener mListener;

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnRed) {
            selectedColor = R.color.colorRed;
        } else if (id == R.id.btnBlue) {
            selectedColor = R.color.colorBlue;
        } else if (id == R.id.btnBrown) {
            selectedColor = R.color.colorBrown;
        } else if (id == R.id.btnGreen) {
            selectedColor = R.color.colorGreen;
        } else if (id == R.id.btnLightBlue) {
            selectedColor = R.color.colorLightBlue;
        } else if (id == R.id.btnOrange) {
            selectedColor = R.color.colorOrange;
        } else if (id == R.id.btnPink) {
            selectedColor = R.color.colorPink;
        } else  if (id == R.id.btnPurple) {
            selectedColor = R.color.colorPurple;
        } else if (id == R.id.btnYellow) {
            selectedColor = R.color.colorYellow;
        }
    }

    public int getSelectedColor() {
        return selectedColor;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_event_color, null);
        Button btnRed = (Button) v.findViewById(R.id.btnRed);
        btnRed.setOnClickListener(this);
        Button btnBlue = (Button) v.findViewById(R.id.btnBlue);
        btnBlue.setOnClickListener(this);
        Button btnBrown = (Button) v.findViewById(R.id.btnBrown);
        btnBrown.setOnClickListener(this);
        Button btnGreen = (Button) v.findViewById(R.id.btnGreen);
        btnGreen.setOnClickListener(this);
        Button btnLightBlue = (Button) v.findViewById(R.id.btnLightBlue);
        btnLightBlue.setOnClickListener(this);
        Button btnPink = (Button) v.findViewById(R.id.btnPink);
        btnPink.setOnClickListener(this);
        Button btnPurple = (Button) v.findViewById(R.id.btnPurple);
        btnPurple.setOnClickListener(this);
        Button btnOrange = (Button) v.findViewById(R.id.btnOrange);
        btnOrange.setOnClickListener(this);
        Button btnYellow = (Button) v.findViewById(R.id.btnYellow);
        btnYellow.setOnClickListener(this);

        builder.setView(v)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDialogPositiveClick(EventColorFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDialogNegativeClick(EventColorFragment.this);
                    }
                });
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
}
