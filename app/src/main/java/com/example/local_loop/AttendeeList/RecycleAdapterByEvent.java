package com.example.local_loop.AttendeeList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.CreateAccount.User;
import com.example.local_loop.R;
import com.example.local_loop.database.DatabaseHelper;

import java.util.List;

public class RecycleAdapterByEvent extends RecyclerView.Adapter<AttendeeListViewer> {

        Context context;
        List<User> users;
        int eventId;

    public RecycleAdapterByEvent(Context context, List < User > users, int eventId) {
        this.context = context;
        this.users = users;
        this.eventId = eventId;
    }
    public void deleteEntry(String email) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getEmail().equals(email)) {
                users.remove(i);
                break;
            }
        }
    }

        @NonNull
        @Override
        public AttendeeListViewer onCreateViewHolder (@NonNull ViewGroup parent,int viewType){
        return new AttendeeListViewer(LayoutInflater.from(parent.getContext()).inflate(R.layout.attendee_list_layout, parent, false));
    }

        @Override
        public void onBindViewHolder (@NonNull AttendeeListViewer holder,int position){
            holder.firstView.setText(users.get(position).getFirstName());
            holder.lastView.setText(users.get(position).getLastName());
            holder.userView.setText(users.get(position).getUsername());
            holder.emailView.setText(users.get(position).getEmail());
            holder.typeView.setText(users.get(position).getRole());
            holder.delete.setOnClickListener(v -> {
                onApprove(eventId, users.get(holder.getAdapterPosition()).getUsername());
                deleteEntry(users.get(holder.getAdapterPosition()).getEmail());
                notifyItemRemoved(holder.getAdapterPosition());
            });

            holder.disable.setOnClickListener(v -> {
                onRefuse(eventId, users.get(holder.getAdapterPosition()).getUsername());
                deleteEntry(users.get(holder.getAdapterPosition()).getEmail());
                notifyItemRemoved(holder.getAdapterPosition());
            });
            DatabaseHelper db = new DatabaseHelper(context);
        }

        public void onApprove (int eventId, String attendeeId){
        DatabaseHelper db = new DatabaseHelper(context);
        db.updateStatus(eventId, attendeeId, "Approved");
        Toast.makeText(context.getApplicationContext(), "User " + attendeeId + " was approved.", Toast.LENGTH_SHORT).show();
        }

        public void onRefuse (int eventId, String attendeeId){
        DatabaseHelper db = new DatabaseHelper(context);
        db.updateStatus(eventId, attendeeId, "Refused");
        Toast.makeText(context.getApplicationContext(), "User " + attendeeId + " was refused.", Toast.LENGTH_SHORT).show();
    }
        @Override
        public int getItemCount () {
        return users.size();
    }

}
