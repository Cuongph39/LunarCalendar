package com.vanpt.lunarcalendar.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.vanpt.lunarcalendar.R;
import com.vanpt.lunarcalendar.interfaces.IDialogEventListener;
import com.vanpt.lunarcalendar.interfaces.IOnColorSetListener;
import com.vanpt.lunarcalendar.models.DateObject;
import com.vanpt.lunarcalendar.models.EventObject;
import com.vanpt.lunarcalendar.utils.DateConverter;

import java.util.Calendar;

/**
 * Created by vanpt on 12/6/2016.
 */

public class EventPropertiesFragment
        extends DialogFragment
        implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        IOnColorSetListener {
    IDialogEventListener mListener;
    private Context context;
    private int currentPickerId;
    private DateObject startDateSolar;
    private DateObject endDateSolar;
    private DateObject startDateLunar;
    private DateObject endDateLunar;
    private Button btnPickDateStart;
    private Button btnPickTimeStart;
    private Button btnPickDateEnd;
    private Button btnPickTimeEnd;
    private EditText editTextEventName;
    private EditText editTextEventLocation;
    private Spinner spinnerRepetition;
    private EventObject event;
    private CheckBox chkAllDay;
    private View view;

    public EventPropertiesFragment() throws Exception {
        event = new EventObject("Sự kiện mới");
        Calendar cal = Calendar.getInstance();
        startDateSolar = new DateObject(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
        startDateSolar.setHourOfDay(8);
        startDateSolar.setMinute(0);
        endDateSolar = new DateObject(startDateSolar.getDay(), startDateSolar.getMonth(), startDateSolar.getYear());
        endDateSolar.setHourOfDay(20);
        endDateSolar.setMinute(0);
        startDateLunar = DateConverter.convertSolar2Lunar(startDateSolar, 7);
        endDateLunar = DateConverter.convertSolar2Lunar(endDateSolar, 7);
    }

    public EventObject getEvent() {
        return event;
    }

    public void setEvent(EventObject event) throws Exception {
        this.event = event;
        Calendar cal = Calendar.getInstance();
        cal.setTime(event.getFromDate());
        startDateSolar.setYear(cal.get(Calendar.YEAR));
        startDateSolar.setMonth(cal.get(Calendar.MONTH) + 1);
        startDateSolar.setDay(cal.get(Calendar.DAY_OF_MONTH));
        startDateSolar.setHourOfDay(cal.get(Calendar.HOUR_OF_DAY));
        startDateSolar.setMinute(cal.get(Calendar.MINUTE));
        startDateLunar = DateConverter.convertSolar2Lunar(startDateSolar, 7);
        cal.setTime(event.getToDate());
        endDateSolar.setYear(cal.get(Calendar.YEAR));
        endDateSolar.setMonth(cal.get(Calendar.MONTH) + 1);
        endDateSolar.setDay(cal.get(Calendar.DAY_OF_MONTH));
        endDateSolar.setHourOfDay(cal.get(Calendar.HOUR_OF_DAY));
        endDateSolar.setMinute(cal.get(Calendar.MINUTE));
        endDateLunar = DateConverter.convertSolar2Lunar(endDateSolar, 7);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_event_properties, null);
        this.view = v;
        Button btnChooseColor = (Button) v.findViewById(R.id.btnChooseColor);
        btnChooseColor.setBackgroundColor(getResources().getColor(event.getColor()));
        btnChooseColor.setOnClickListener(this);

        editTextEventName = (EditText) v.findViewById(R.id.editTextEventName);
        editTextEventName.setText(event.getName());

        editTextEventLocation = (EditText) v.findViewById(R.id.editTextLocation);
        editTextEventLocation.setText(event.getLocation());

        chkAllDay = (CheckBox) v.findViewById(R.id.chkAllDay);
        chkAllDay.setChecked(event.isAllDayEvent());

        spinnerRepetition = (Spinner) v.findViewById(R.id.spinnerRepetition);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context,
                R.array.repetition_types,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRepetition.setAdapter(adapter);
        spinnerRepetition.setSelection(event.getRepetitionType().getValue());

        Calendar cal = Calendar.getInstance();

        cal.set(startDateLunar.getYear(), startDateLunar.getMonth() - 1, startDateLunar.getDay(),
                startDateLunar.getHourOfDay(), startDateLunar.getMinute());
        btnPickDateStart = (Button) v.findViewById(R.id.btnPickDateStart);
        btnPickDateStart.setOnClickListener(this);
        btnPickDateStart.setText(
                String.format("%s, %s tháng %s, %s",
                        DateConverter.VN_DAYS[cal.get(Calendar.DAY_OF_WEEK) - 1],
                        startDateLunar.getDay(),
                        startDateLunar.getMonth(),
                        DateConverter.convertToLunarYear(startDateLunar.getYear()))
        );

        btnPickTimeStart = (Button) v.findViewById(R.id.btnPickTimeStart);
        btnPickTimeStart.setOnClickListener(this);
        btnPickTimeStart.setText(
                String.format("%tT", cal)
        );

        cal.set(endDateLunar.getYear(), endDateLunar.getMonth() - 1, endDateLunar.getDay(),
                endDateLunar.getHourOfDay(), endDateLunar.getMinute());
        btnPickDateEnd = (Button) v.findViewById(R.id.btnPickDateEnd);
        btnPickDateEnd.setOnClickListener(this);
        btnPickDateEnd.setText(
                String.format("%s, %s tháng %s, %s",
                        DateConverter.VN_DAYS[cal.get(Calendar.DAY_OF_WEEK) - 1],
                        endDateLunar.getDay(),
                        endDateLunar.getMonth(),
                        DateConverter.convertToLunarYear(endDateLunar.getYear()))
        );

        btnPickTimeEnd = (Button) v.findViewById(R.id.btnPickTimeEnd);
        btnPickTimeEnd.setOnClickListener(this);
        btnPickTimeEnd.setText(
                String.format("%tT", cal)
        );

        builder.setView(v)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        event.setName(editTextEventName.getText().toString());
                        event.setLocation(editTextEventLocation.getText().toString());
                        Calendar cal = Calendar.getInstance();
                        cal.set(startDateSolar.getYear(), startDateSolar.getMonth() - 1, startDateSolar.getDay(),
                                startDateSolar.getHourOfDay(), startDateSolar.getMinute());
                        event.setFromDate(cal.getTime());
                        cal.set(endDateSolar.getYear(), endDateSolar.getMonth() - 1, endDateSolar.getDay(),
                                endDateSolar.getHourOfDay(), endDateSolar.getMinute());
                        event.setToDate(cal.getTime());
                        event.setAllDayEvent(chkAllDay.isChecked());
                        int pos = spinnerRepetition.getSelectedItemPosition();
                        event.setRepetitionType(pos);
                        mListener.onDialogPositiveClick(EventPropertiesFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDialogNegativeClick(EventPropertiesFragment.this);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
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
        this.context = activity;
        try {
            mListener = (IDialogEventListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement IDialogEventListener");
        }
    }

    @Override
    public void onClick(View view) {
        currentPickerId = view.getId();
        if (currentPickerId == R.id.btnPickDateStart) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this.context,
                    this,
                    startDateSolar.getYear(), startDateSolar.getMonth() - 1, startDateSolar.getDay());
            datePickerDialog.show();
        } else if (currentPickerId == R.id.btnPickDateEnd) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this.context,
                    this,
                    endDateSolar.getYear(), endDateSolar.getMonth() - 1, endDateSolar.getDay());
            datePickerDialog.show();
        } else if (currentPickerId == R.id.btnPickTimeStart){
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this.context,
                    this,
                    startDateSolar.getHourOfDay(), startDateSolar.getMinute(), true
            );
            timePickerDialog.show();
        } else if (currentPickerId == R.id.btnPickTimeEnd) {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this.context,
                    this,
                    endDateSolar.getHourOfDay(), endDateSolar.getMinute(), true
            );
            timePickerDialog.show();
        } else if (currentPickerId == R.id.btnChooseColor) {
            EventColorFragment eventColorFragment = new EventColorFragment();
            eventColorFragment.setSelectedColor(this.event.getColor());
            eventColorFragment.setColorSetListener(this);
            eventColorFragment.show(getFragmentManager(), "EventColorFragment");
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);
            if (currentPickerId == R.id.btnPickDateStart) {
                startDateSolar.setYear(year);
                startDateSolar.setMonth(month + 1);
                startDateSolar.setDay(day);
                startDateLunar = DateConverter.convertSolar2Lunar(startDateSolar, 7);
                btnPickDateStart.setText(
                        String.format("%s, %s tháng %s, %s",
                                DateConverter.VN_DAYS[cal.get(Calendar.DAY_OF_WEEK) - 1],
                                startDateLunar.getDay(),
                                startDateLunar.getMonth(),
                                DateConverter.convertToLunarYear(startDateLunar.getYear()))
                );
            } else if (currentPickerId == R.id.btnPickDateEnd) {
                endDateSolar.setYear(year);
                endDateSolar.setMonth(month + 1);
                endDateLunar.setDay(day);
                endDateLunar = DateConverter.convertSolar2Lunar(endDateSolar, 7);
                btnPickDateEnd.setText(
                        String.format("%s, %s tháng %s, %s",
                                DateConverter.VN_DAYS[cal.get(Calendar.DAY_OF_WEEK) - 1],
                                endDateLunar.getDay(),
                                endDateLunar.getMonth(),
                                DateConverter.convertToLunarYear(endDateLunar.getYear()))
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        if (currentPickerId == R.id.btnPickTimeStart) {
            startDateSolar.setHourOfDay(hourOfDay);
            startDateSolar.setMinute(minute);
            startDateLunar.setHourOfDay(hourOfDay);
            startDateLunar.setMinute(minute);
        } else if (currentPickerId == R.id.btnPickTimeEnd) {
            endDateSolar.setHourOfDay(hourOfDay);
            endDateSolar.setMinute(minute);
            endDateLunar.setHourOfDay(hourOfDay);
            endDateLunar.setMinute(minute);
        }
    }

    @Override
    public void onColorSet(int color) {
        Button btnChooseColor = (Button) view.findViewById(R.id.btnChooseColor);
        event.setColor(color);
        btnChooseColor.setBackgroundColor(getResources().getColor(event.getColor()));
    }
}
