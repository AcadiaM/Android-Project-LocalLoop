package com.example.local_loop.UserList;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.CreateAccount.User;
import com.example.local_loop.R;
import com.example.local_loop.database.DatabaseHelper;

import java.util.List;

public class RecycleAdapter extends RecyclerView.Adapter<UserListViewer>{

    Context context;
    List<User> users;

    public RecycleAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
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
    public UserListViewer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserListViewer(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewer holder, int position) {
        holder.firstView.setText("First: " + users.get(position).getFirstName());
        holder.lastView.setText("Last: " + users.get(position).getLastName());
        holder.userView.setText("User: " +users.get(position).getUsername());
        holder.emailView.setText("Email: " + users.get(position).getEmail());
        holder.typeView.setText("Type: " + users.get(position).getRole());
        holder.delete.setOnClickListener(v->{
            String email = holder.emailView.getText().toString();
            onDelete(email, holder.getAdapterPosition());
        });
        holder.disable.setOnClickListener(v -> {
            String email = holder.emailView.getText().toString();
            String username = holder.userView.getText().toString();
            onDisable(holder, username, holder.getAdapterPosition());
        });
        DatabaseHelper db = new DatabaseHelper(context);
        if (db.isActive(users.get(position).getUsername()) == 0) {
            holder.disable.setIconTint(ColorStateList.valueOf(Color.RED));
            holder.disable.setHint("User is disabled.");
        } else {
            holder.disable.setIconTint(ColorStateList.valueOf(Color.BLACK));
            holder.disable.setHint("User is not disabled.");
        }
    }

    /**
     * @param holder Reference to the current view holder.
     * @param username The username of the user being modified.
     * @param pos The position of the user being modified in the recycle adapter.
     *
     * Disabled or re-enables the selected user when clicking the disable button. Displays the button's icon in red when user is disabled and displays Toast indicating a change was made.
     */
    public void onDisable(UserListViewer holder, String username, int pos){
        DatabaseHelper db = new DatabaseHelper(context);
        switch (db.isActive(username)) {
            case 1:
                //The user is active and must be deactivated.
                db.deactivateUser(username);
                holder.disable.setIconTint(ColorStateList.valueOf(Color.RED));
                Toast.makeText(context.getApplicationContext(), "User " + username + " is now disabled.", Toast.LENGTH_SHORT).show();
                notifyItemChanged(pos);
                break;
            case 0:
                //The user is inactive and must be reactivated.
                db.reactivateUser(username);
                holder.disable.setIconTint(ColorStateList.valueOf(Color.BLACK));
                Toast.makeText(context.getApplicationContext(), "User " + username + " is now enabled.", Toast.LENGTH_SHORT).show();
                notifyItemChanged(pos);
                break;
        }
    }

    public void onDelete(String email, int pos){
        DatabaseHelper db = new DatabaseHelper(context);
        String username = db.getUsernameByEmail(email);
        db.deleteUser(email);
        this.deleteEntry(email);
        Toast.makeText(context.getApplicationContext(), "User " + username + " was deleted.", Toast.LENGTH_SHORT).show();
        notifyItemRemoved(pos);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

}
