package com.example.local_loop.Event;

import static com.example.local_loop.Event.EventDetailsActivity.EXTRA_SOURCE;

import com.example.local_loop.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    public interface OnEventClickListener {
        void onClick(Event event);
        void onLongClick(Event event);
        void onRenameClick(Event event);
    }

    private List<Event> events;
    private OnEventClickListener listener;
    private final Context context;
    private final String source;
    private String attendeeID = null;

    public EventAdapter(String source, List<Event> eventList, Context context) {
        this.source = source;
        this.events = eventList;
        this.context = context;
    }

    public EventAdapter(String source, String attendeeID, List<Event> eventList, Context context) {
        this.source = source;
        this.attendeeID = attendeeID;
        this.events = eventList;
        this.context = context;
    }

    public EventAdapter(String source, List<Event> events, Context context, OnEventClickListener listener) {
        this.source = source;
        this.events = events;
        this.context = context;
        this.listener = listener;
    }

    private boolean deleteMode = false;
    private List<Integer> selectedEventIds = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void setDeleteMode(boolean enabled) {
        this.deleteMode = enabled;
        notifyDataSetChanged();
    }

    private boolean editMode = false;

    @SuppressLint("NotifyDataSetChanged")
    public void setEditMode(boolean enabled) {
        this.editMode = enabled;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSelectedEventIds(List<Integer> selectedIds) {
        this.selectedEventIds = selectedIds;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.eventButton.setText(event.getTitle());

        if (deleteMode) {
            // In delete mode: toggle selection & highlight selected
            holder.eventButton.setOnClickListener(v -> {
                int id = event.getID();
                if (selectedEventIds.contains(id)) {
                    selectedEventIds.remove(Integer.valueOf(id));
                    v.setBackgroundColor(Color.LTGRAY);
                } else {
                    selectedEventIds.add(id);
                    v.setBackgroundColor(Color.TRANSPARENT);
                }
            });

            if (selectedEventIds.contains(event.getID())) {
                holder.eventButton.setBackgroundColor(Color.TRANSPARENT);
            } else {
                holder.eventButton.setBackgroundColor(Color.LTGRAY);
            }

        } else if (editMode) {
            // In edit mode: click triggers rename
            holder.eventButton.setOnClickListener(v -> {
                if (listener != null) listener.onRenameClick(event);
            });
            holder.eventButton.setBackgroundColor(Color.LTGRAY);

        } else {
        holder.eventButton.setOnClickListener(v -> {
            // If listener is defined, you can use it OR launch detail screen here
            Intent intent = new Intent(context, EventDetailsActivity.class);
            intent.putExtra(EXTRA_SOURCE, source);
            intent.putExtra("sourceContext", context.getClass().getName());
            intent.putExtra("eventId", event.getID());
            intent.putExtra("title", event.getTitle());
            intent.putExtra("description", event.getDescription());
            intent.putExtra("fee", String.valueOf(event.getFee()));
            intent.putExtra("datetime", event.getDateTime());
            intent.putExtra("categoryId", event.getCategoryId());
            intent.putExtra("organizer", event.getOrganizer());
            if (attendeeID != null){
                intent.putExtra("attendeeId", attendeeID);
            }
            context.startActivity(intent);
        });

        holder.eventButton.setOnLongClickListener(v -> {
            if (listener != null) listener.onLongClick(event);
            return true;
        });

        holder.eventButton.setBackgroundColor(Color.TRANSPARENT);
    }

}

    @Override
    public int getItemCount() {
        return events.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateEvents(List<Event> newEvents) {
        this.events = newEvents;
        notifyDataSetChanged();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        Button eventButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventButton = itemView.findViewById(R.id.eventButton);
        }
    }
}
