package com.vanpt.lunarcalendar.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vanpt.lunarcalendar.R;
import com.vanpt.lunarcalendar.interfaces.OnItemClickListener;
import com.vanpt.lunarcalendar.models.EventObject;

import java.util.List;

/**
 * Created by vanpt on 11/24/2016.
 */

public class EventRecyclerAdapter extends RecyclerView.Adapter<EventViewHolder> {
    private final Context context;
    private final List<EventObject> items;
    private final OnItemClickListener listener;

    public EventRecyclerAdapter(Context context, List<EventObject> items, OnItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_event, parent, false);
        EventViewHolder viewHolder = new EventViewHolder(context, v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}