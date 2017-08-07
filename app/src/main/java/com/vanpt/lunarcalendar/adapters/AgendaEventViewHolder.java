package com.vanpt.lunarcalendar.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.vanpt.lunarcalendar.R;
import com.vanpt.lunarcalendar.activities.MainActivity;
import com.vanpt.lunarcalendar.activities.ViewEventActivity;
import com.vanpt.lunarcalendar.data.MyDbHandler;
import com.vanpt.lunarcalendar.interfaces.OnItemClickListener;
import com.vanpt.lunarcalendar.models.DateObject;
import com.vanpt.lunarcalendar.models.EventObject;
import com.vanpt.lunarcalendar.utils.DateConverter;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by vanpt on 12/8/2016.
 */

public class AgendaEventViewHolder extends RecyclerView.ViewHolder {
    private final TextView itemDay;
    private final TextView itemDate;
    private Context context;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private EventRecyclerAdapter adapter;

    public AgendaEventViewHolder(View itemView) {
        super(itemView);
        itemDay = (TextView) itemView.findViewById(R.id.textViewDay);
        itemDate = (TextView) itemView.findViewById(R.id.textViewDate);
    }

    public AgendaEventViewHolder(Context context, View v) {
        this(v);
        this.context = context;
        recyclerView = (RecyclerView) itemView.findViewById(R.id.agenda_event_recycler_view);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
    }

    public Context getContext() {
        return context;
    }

    public void bind(final DateObject dateObject, final OnItemClickListener listener) {
        Calendar cal = Calendar.getInstance();
        cal.set(dateObject.getYear(), dateObject.getMonth() - 1, dateObject.getDay());
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        itemDay.setText(DateConverter.VN_DAYS[dayOfWeek - 1]);
        itemDate.setText(String.format("Ngày %s tháng %s năm %s",
                dateObject.getDay(),
                dateObject.getMonth(),
                DateConverter.convertToLunarYear(dateObject.getYear())));
        if (dayOfWeek == 1) {
            itemDay.setTextColor(Color.RED);
            itemDate.setTextColor(Color.RED);
        } else if (dayOfWeek == 7) {
            itemDay.setTextColor(Color.BLUE);
            itemDate.setTextColor(Color.BLUE);
        }
        MyDbHandler dbHandler = new MyDbHandler(this.context, null, null, 1);
        EventObject[] results = dbHandler.findEvent(dateObject.getDay(), dateObject.getMonth(), dateObject.getYear());
        ArrayList<EventObject> events = new ArrayList<>();
        for (int i = 0; i<results.length; i++) {
            events.add(results[i]);
        }
        adapter = new EventRecyclerAdapter(this.context, events, new OnItemClickListener() {
            @Override
            public void onItemClick(EventObject item) {
                Intent intent = new Intent(AgendaEventViewHolder.this.context, ViewEventActivity.class);
                intent.putExtra("id", item.getId());
                ((MainActivity)AgendaEventViewHolder.this.context).startActivityForResult(intent, MainActivity.REQUEST_CODE);
            }
        });
        recyclerView.setAdapter(adapter);
    }

}
