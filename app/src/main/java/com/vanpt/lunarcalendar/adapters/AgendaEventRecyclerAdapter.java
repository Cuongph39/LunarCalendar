package com.vanpt.lunarcalendar.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vanpt.lunarcalendar.R;
import com.vanpt.lunarcalendar.interfaces.OnItemClickListener;
import com.vanpt.lunarcalendar.models.DateObject;
import com.vanpt.lunarcalendar.models.EventObject;

import java.util.List;

/**
 * Created by vanpt on 12/8/2016.
 */

public class AgendaEventRecyclerAdapter extends RecyclerView.Adapter<AgendaEventViewHolder> {

    private final Context context;
    private final List<DateObject> items;
    private final OnItemClickListener listener;

    public AgendaEventRecyclerAdapter(Context context, List<DateObject> items, OnItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public AgendaEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_agenda_event, parent, false);
        AgendaEventViewHolder viewHolder = new AgendaEventViewHolder(context, v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AgendaEventViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
