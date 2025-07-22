package com.example.local_loop.Adapters;

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

import com.example.local_loop.Helpers.RequestStatus;
import com.example.local_loop.Helpers.ViewMode;
import com.example.local_loop.Models.Account;
import com.example.local_loop.Helpers.DatabaseHelper;
import com.example.local_loop.Models.User;
import com.example.local_loop.R;

import java.util.List;

public class UserDisplayAdapter extends RecyclerView.Adapter<UserDisplayViewHolder> {

    private final Context context;
    private final List<Account> users;
    private final DatabaseHelper db;

    private final ViewMode mode; //Assuming its checking between Organizer and Admin
    private final int eventId;  // Only relevant for attendee mode
    TextView noUsersTextView;

    public UserDisplayAdapter(Context context, List<Account> users, ViewMode mode, int eventId, TextView noUsersTextView) {
        this.context = context;
        this.users = users;
        this.mode = mode;
        this.eventId = eventId;
        this.noUsersTextView = noUsersTextView;
        this.db = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public UserDisplayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = R.layout.user_list_layout;
        if (mode == ViewMode.PARTICIPANT_MGMT) {
            layoutId = R.layout.attendee_list_layout;
        }
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

        if (mode == ViewMode.PARTICIPANT_MGMT) {
            holder.disable.setOnClickListener(v -> {
                approve(eventId, user);
                deleteEntry(user);
                notifyItemRemoved(holder.getAdapterPosition());
            });

            holder.delete.setOnClickListener(v -> {
                refuse(eventId, user);
                deleteEntry(user);
                notifyItemRemoved(holder.getAdapterPosition());
            });

        } else {
            holder.delete.setOnClickListener(v -> deleteUser(user, holder.getAdapterPosition()));

            holder.disable.setOnClickListener(v -> toggleUserActiveStatus(holder, user, holder.getAdapterPosition()));

            if (db.getUserState(user.getUserID()) == 0) {
                holder.disable.setIconTint(ColorStateList.valueOf(Color.RED));
                holder.disable.setHint("User is disabled.");
            } else {
                holder.disable.setIconTint(ColorStateList.valueOf(Color.BLACK));
                holder.disable.setHint("User is not disabled.");
            }
        }
    }

    private void approve(int eventId, Account user) {
        db.updateRequestStatus(eventId, user.getUserID(), RequestStatus.APPROVED);
        Toast.makeText(context, "User " + user.getUsername() + " approved.", Toast.LENGTH_SHORT).show();
        updateEmptyViewVisibility();
    }

    private void refuse(int eventId, Account user) {
        db.updateRequestStatus(eventId, user.getUserID(), RequestStatus.REFUSED);
        Toast.makeText(context, "User " + user.getUsername() + " refused.", Toast.LENGTH_SHORT).show();
        updateEmptyViewVisibility();
    }

    private void toggleUserActiveStatus(UserDisplayViewHolder holder, Account user, int pos) {
        //Get user state, and toggle the value
        int status = db.getUserState(user.getUserID()) ==1 ? 0:1;

        if ( status == 1) {
            holder.disable.setIconTint(ColorStateList.valueOf(Color.RED));
            Toast.makeText(context, "User " + user.getUsername() + " disabled.", Toast.LENGTH_SHORT).show();
        } else {

            holder.disable.setIconTint(ColorStateList.valueOf(Color.BLACK));
            Toast.makeText(context, "User " + user.getUsername() + " enabled.", Toast.LENGTH_SHORT).show();
        }
        db.setUserState(user.getUserID(),status);
        notifyItemChanged(pos);
    }

    private void updateEmptyViewVisibility() {
        if (users == null || users.isEmpty()) {
            noUsersTextView.setVisibility(View.VISIBLE);
        } else {
            noUsersTextView.setVisibility(View.GONE);
        }
    }


    private void deleteUser(Account user, int pos) {
        db.deleteUser(user.getUserID());
        deleteEntry(user);
        Toast.makeText(context, "User " + user.getUsername() + " deleted.", Toast.LENGTH_SHORT).show();
        notifyItemRemoved(pos);
        updateEmptyViewVisibility();
    }

    public void deleteEntry(Account user) {
        users.remove(user);
        updateEmptyViewVisibility();
    }


    @Override
    public int getItemCount() {
        return users.size();
    }
}