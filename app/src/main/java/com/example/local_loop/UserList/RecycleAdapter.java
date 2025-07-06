package com.example.local_loop.UserList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
        holder.firstView.setText(users.get(position).getFirstName());
        holder.lastView.setText(users.get(position).getLastName());
        holder.userView.setText(users.get(position).getUsername());
        holder.emailView.setText(users.get(position).getEmail());
        holder.typeView.setText(users.get(position).getRole());
        //The imageButton images do not show on the emulator. Right-most is delete, left-most is disable.
        holder.delete.setOnClickListener(v->{
            String email = holder.emailView.getText().toString();
            onDelete(email);
        });
        holder.disable.setOnClickListener(v -> {
            String email = holder.emailView.getText().toString();
            onDisable(email);
        });
        //Not currently implemented to UI, but shows in database.
    }

    public void onDisable(String email){
        DatabaseHelper db = new DatabaseHelper(context);
        switch (db.isActive(email)) {
            case 1:
                //The user is active and must be deactivated.
                db.deactivateUser(email);
                notifyDataSetChanged();
                String username1 = "";
                for (User user: users){
                    if (user.getEmail().equals(email)){
                        username1 = user.getUsername();
                        break;
                    }
                }
                Toast.makeText(context, "User " + username1 + " is now disabled", Toast.LENGTH_SHORT).show();
                break;
            case 0:
                //The user is inactive and must be reactivated.
                db.reactivateUser(email);
                notifyDataSetChanged();
                String username2 = "";
                for (User user: users){
                    if (user.getEmail().equals(email)){
                        username2 = user.getUsername();
                        break;
                    }
                }
                Toast.makeText(context, "User " + username2 + " is now enabled", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void onDelete(String email){
        DatabaseHelper db = new DatabaseHelper(context);
        db.deleteUser(email);
        this.deleteEntry(email);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

}
