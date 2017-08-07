package com.vanpt.lunarcalendar.activities;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.vanpt.lunarcalendar.R;
import com.vanpt.lunarcalendar.data.MyDbHandler;
import com.vanpt.lunarcalendar.fragments.EventColorFragment;
import com.vanpt.lunarcalendar.fragments.EventPropertiesFragment;
import com.vanpt.lunarcalendar.interfaces.IDialogEventListener;
import com.vanpt.lunarcalendar.models.EventObject;
import com.vanpt.lunarcalendar.utils.DateConverter;

import java.util.Calendar;

public class ViewEventActivity extends AppCompatActivity
        implements IDialogEventListener {

    private int selectedColor = R.color.colorRed;
    private int eventId;
    private View layoutViewEvent;
    private EventObject event;
    private TextView textViewEventName;
    private TextView textViewEventTime;
    private TextView textViewEventLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        eventId = extras.getInt("id");
        MyDbHandler dbHandler = new MyDbHandler(this, null, null, 1);
        event = dbHandler.findEvent(eventId);
        selectedColor = event.getColor();
        layoutViewEvent = this.findViewById(R.id.layout_view_event);
        layoutViewEvent.setBackgroundColor(this.getResources().getColor(selectedColor));
        textViewEventName = (TextView) this.findViewById(R.id.textViewEventName);
        textViewEventTime = (TextView) this.findViewById(R.id.textViewEventTime);
        textViewEventLocation = (TextView) this.findViewById(R.id.textViewEventLocation);
        setEventInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_view_event_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.event_color) {
            EventColorFragment eventColor = new EventColorFragment();
            eventColor.show(getFragmentManager(), "EventColorFragment");
        } else if (id == R.id.event_delete) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Sự kiện")
            .setMessage("Xóa sự kiện?")
            .setCancelable(false)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    MyDbHandler dbHandler = new MyDbHandler(ViewEventActivity.this, null, null, 1);
                    dbHandler.deleteEvent(ViewEventActivity.this.eventId);
                    ViewEventActivity.this.finish();
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else if (id == R.id.event_edit) {
            try {
                EventPropertiesFragment eventProperties = new EventPropertiesFragment();
                eventProperties.setEvent(event);
                eventProperties.show(getFragmentManager(), "EventPropertiesFragment");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if (dialog instanceof EventColorFragment) {
            selectedColor = ((EventColorFragment) dialog).getSelectedColor();
            event.setColor(selectedColor);
            MyDbHandler dbHandler = new MyDbHandler(this, null, null, 1);
            dbHandler.updateEvent(event);
            layoutViewEvent.setBackgroundColor(this.getResources().getColor(selectedColor));
        } else if (dialog instanceof  EventPropertiesFragment) {
            EventPropertiesFragment eventProperties = (EventPropertiesFragment) dialog;
            MyDbHandler dbHandler = new MyDbHandler(this, null, null, 1);
            dbHandler.updateEvent(eventProperties.getEvent());
            setEventInfo();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        data.putExtra("selectedColor", selectedColor);
        data.putExtra("id", eventId);
        setResult(RESULT_OK, data);
        super.finish();
    }

    private void setEventInfo() {
        textViewEventName.setText(event.getName());
        textViewEventLocation.setText(event.getLocation());
        if (event.isAllDayEvent()) {
            textViewEventTime.setText(this.getResources().getText(R.string.all_day_event));
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(event.getFromDate());
            String strFrom = String.format("%tT ngày %s tháng %s năm %s",
                    cal,
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.MONTH) + 1,
                    DateConverter.convertToLunarYear(cal.get(Calendar.YEAR)));
            cal.setTime(event.getToDate());
            String strTo = String.format("%tT ngày %s tháng %s năm %s",
                    cal,
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.MONTH) + 1,
                    DateConverter.convertToLunarYear(cal.get(Calendar.YEAR)));
            textViewEventTime.setText(strFrom + " - " + strTo);
        }
    }
}
