package com.example.local_loop.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.Account.Account;
import com.example.local_loop.Helpers.DatabaseHelper;
import com.example.local_loop.Helpers.MODE;
import com.example.local_loop.R;

import java.util.ArrayList;
import java.util.List;

public class UserDisplayAdapter extends RecyclerView.Adapter<UserDisplayViewHolder> {

    public interface OnUserClickListener {
        void onClick(Account user);
        void onDeleteClick(Account user);
        void onDisableClick(Account user);
    }

    private final Context context;
    private final List<Account> users;
    private final List<Account> selectedUsers = new ArrayList<>();
    private final OnUserClickListener listener;
    private final DatabaseHelper db;
    private MODE mode = MODE.DEFAULT;
    private final int eventId;

    public UserDisplayAdapter(Context context, List<Account> users, MODE mode, int eventId, OnUserClickListener listener) {
        this.context = context;
        this.users = users;
        this.mode = mode;
        this.listener = listener;
        this.eventId = eventId;
        this.db = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public UserDisplayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = (mode == MODE.ATTENDEE) ? R.layout.attendee_list_layout : R.layout.user_list_layout;
        return new UserDisplayViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserDisplayViewHolder holder, int position) {
        Account user = users.get(position);

        holder.firstView.setText(user.getFirstName());
        holder.lastView.setText(user.getLastName());
        holder.userView.setText(user.getUsername());
        holder.emailView.setText(user.getEmail());
        holder.typeView.setText(user.getRole());

        switch (mode) {
            case DELETE:
                onDeleteMode(holder, user);
                break;
            case EDIT:
                onEditMode(holder, user);
                break;
            case ATTENDEE:
                onAttendeeMode(holder, user);
                break;
            default:
                onNormalMode(holder, user);
                break;
        }
    }

    private void onDeleteMode(@NonNull UserDisplayViewHolder holder, Account user) {
        holder.itemView.setOnClickListener(v -> {
            if (selectedUsers.contains(user)) {
                selectedUsers.remove(user);
                holder.itemView.setBackgroundColor(Color.LTGRAY);
            } else {
                selectedUsers.add(user);
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        holder.itemView.setBackgroundColor(
                selectedUsers.contains(user) ? Color.TRANSPARENT : Color.LTGRAY
        );
    }

    private void onEditMode(@NonNull UserDisplayViewHolder holder, Account user) {
        holder.disable.setOnClickListener(v -> {
            if (listener != null) listener.onDisableClick(user);
        });
        holder.delete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(user);
        });

        holder.itemView.setBackgroundColor(Color.LTGRAY);
        updateDisableIcon(holder, user);
    }

    private void onAttendeeMode(@NonNull UserDisplayViewHolder holder, Account user) {
        holder.disable.setOnClickListener(v -> {
            db.updateRequestStatus(eventId, user.getUsername(), "Approved");
            deleteEntry(user.getEmail());
            Toast.makeText(context, "User " + user.getUsername() + " approved.", Toast.LENGTH_SHORT).show();
            notifyItemRemoved(holder.getAdapterPosition());
        });

        holder.delete.setOnClickListener(v -> {
            db.updateRequestStatus(eventId, user.getUsername(), "Refused");
            deleteEntry(user.getEmail());
            Toast.makeText(context, "User " + user.getUsername() + " refused.", Toast.LENGTH_SHORT).show();
            notifyItemRemoved(holder.getAdapterPosition());
        });

        holder.itemView.setBackgroundColor(Color.TRANSPARENT);
    }

    private void onNormalMode(@NonNull UserDisplayViewHolder holder, Account user) {
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(user);
        });
        holder.itemView.setBackgroundColor(Color.TRANSPARENT);
    }

    private void updateDisableIcon(@NonNull UserDisplayViewHolder holder, Account user) {
        if (db.isActive(user.getUsername()) == 0) {
            holder.disable.setIconTint(ColorStateList.valueOf(Color.RED));
            holder.disable.setHint("User is disabled.");
        } else {
            holder.disable.setIconTint(ColorStateList.valueOf(Color.BLACK));
            holder.disable.setHint("User is not disabled.");
        }
    }

    private void deleteEntry(String email) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getEmail().equals(email)) {
                users.remove(i);
                break;
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setMode(MODE mode) {
        this.mode = mode;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateUsers(List<Account> newUsers) {
        users.clear();
        users.addAll(newUsers);
        notifyDataSetChanged();
    }

    public List<Account> getSelectedUsers() {
        return selectedUsers;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
