package com.vanpt.lunarcalendar.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.vanpt.lunarcalendar.R;
import com.vanpt.lunarcalendar.interfaces.OnItemClickListener;
import com.vanpt.lunarcalendar.models.EventObject;
import com.vanpt.lunarcalendar.utils.DateConverter;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by vanpt on 11/24/2016.
 */

public class EventViewHolder extends RecyclerView.ViewHolder {
    private Context context = null;
    public TextView itemColor;
    public TextView itemName;
    public TextView itemTime;
    public TextView itemLocation;

    public EventViewHolder(View itemView) {
        super(itemView);
        itemColor = (TextView) itemView.findViewById(R.id.textViewEventColor);
        itemName = (TextView) itemView.findViewById(R.id.textViewEventName);
        itemTime = (TextView) itemView.findViewById(R.id.textViewEventTime);
        itemLocation = (TextView) itemView.findViewById(R.id.textViewEventLocation);
    }

    public EventViewHolder(Context context, View itemView) {
        this(itemView);
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void bind(final EventObject eventObject, final OnItemClickListener listener) {
        this.itemColor.setBackgroundColor(context.getResources().getColor(eventObject.getColor()));
        this.itemName.setText(eventObject.getName());
        if (eventObject.isAllDayEvent()) {
            this.itemTime.setText(this.context.getResources().getText(R.string.all_day_event));
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(eventObject.getFromDate());
            String strFrom = String.format("%tT ngày %s tháng %s năm %s",
                    cal,
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.MONTH) + 1,
                    DateConverter.convertToLunarYear(cal.get(Calendar.YEAR)));
            cal.setTime(eventObject.getToDate());
            String strTo = String.format("%tT ngày %s tháng %s năm %s",
                    cal,
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.MONTH) + 1,
                    DateConverter.convertToLunarYear(cal.get(Calendar.YEAR)));
            itemTime.setText(strFrom + " - " + strTo);
        }
        this.itemLocation.setText(eventObject.getLocation());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(eventObject);
            }
        });
    }
}
