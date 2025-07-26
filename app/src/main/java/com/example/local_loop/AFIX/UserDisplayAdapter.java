package com.example.local_loop.AFIX;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.Account.User;
import com.example.local_loop.Helpers.DatabaseHelper;
import com.example.local_loop.R;


import java.util.List;

public class UserDisplayAdapter extends RecyclerView.Adapter<UserDisplayViewHolder> {

    private final Context context;
    private final List<User> users;
    private final DatabaseHelper db;
    private final boolean isAttendeeMode;
    private final int eventId;  // Only relevant for attendee mode
    TextView noUsersTextView;

    public UserDisplayAdapter(Context context, List<User> users, boolean isAttendeeMode, int eventId, TextView noUsersTextView) {
        this.context = context;
        this.users = users;
        this.isAttendeeMode = isAttendeeMode;
        this.eventId = eventId;
        this.noUsersTextView = noUsersTextView;
        this.db = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public UserDisplayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = R.layout.user_list_layout;
        if (isAttendeeMode) {
            layoutId = R.layout.attendee_list_layout;
        }
        return new UserDisplayViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserDisplayViewHolder holder, int position) {
        User user = users.get(position);
        holder.firstView.setText(user.getFirstName());
        holder.lastView.setText(user.getLastName());
        holder.userView.setText(user.getUsername());
        holder.emailView.setText(user.getEmail());
        holder.typeView.setText(user.getRole());

        if (isAttendeeMode) {
            holder.disable.setOnClickListener(v -> {
                approve(eventId, user.getUsername());
                deleteEntry(user.getEmail());
                notifyItemRemoved(holder.getAdapterPosition());
            });

            holder.delete.setOnClickListener(v -> {
                refuse(eventId, user.getUsername());
                deleteEntry(user.getEmail());
                notifyItemRemoved(holder.getAdapterPosition());
            });

        } else {
            holder.delete.setOnClickListener(v -> deleteUser(user.getEmail(), holder.getAdapterPosition()));

            holder.disable.setOnClickListener(v -> toggleUserActiveStatus(holder, user.getUsername(), holder.getAdapterPosition()));

            if (db.isActive(user.getUsername()) == 0) {
                holder.disable.setIconTint(ColorStateList.valueOf(Color.RED));
                holder.disable.setHint("User is disabled.");
            } else {
                holder.disable.setIconTint(ColorStateList.valueOf(Color.BLACK));
                holder.disable.setHint("User is not disabled.");
            }
        }
    }

    private void approve(int eventId, String attendeeId) {
        db.updateRequestStatus(eventId, attendeeId, "Approved");
        Toast.makeText(context, "User " + attendeeId + " approved.", Toast.LENGTH_SHORT).show();
        updateEmptyViewVisibility();
    }

    private void refuse(int eventId, String attendeeId) {
        db.updateRequestStatus(eventId, attendeeId, "Refused");
        Toast.makeText(context, "User " + attendeeId + " refused.", Toast.LENGTH_SHORT).show();
        updateEmptyViewVisibility();
    }

    private void toggleUserActiveStatus(UserDisplayViewHolder holder, String username, int pos) {
        if (db.isActive(username) == 1) {
            db.deactivateUser(username);
            holder.disable.setIconTint(ColorStateList.valueOf(Color.RED));
            Toast.makeText(context, "User " + username + " disabled.", Toast.LENGTH_SHORT).show();
        } else {
            db.reactivateUser(username);
            holder.disable.setIconTint(ColorStateList.valueOf(Color.BLACK));
            Toast.makeText(context, "User " + username + " enabled.", Toast.LENGTH_SHORT).show();
        }
        notifyItemChanged(pos);
    }

    private void updateEmptyViewVisibility() {
        if (users == null || users.isEmpty()) {
            noUsersTextView.setVisibility(View.VISIBLE);
        } else {
            noUsersTextView.setVisibility(View.GONE);
        }
    }


    private void deleteUser(String email, int pos) {
        String username = db.getUsernameByEmail(email);
        db.deleteUser(email);
        deleteEntry(email);
        Toast.makeText(context, "User " + username + " deleted.", Toast.LENGTH_SHORT).show();
        notifyItemRemoved(pos);
        updateEmptyViewVisibility();
    }

    public void deleteEntry(String email) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getEmail().equals(email)) {
                users.remove(i);
                break;
            }
        }
        updateEmptyViewVisibility();
    }


    @Override
    public int getItemCount() {
        return users.size();
    }
}