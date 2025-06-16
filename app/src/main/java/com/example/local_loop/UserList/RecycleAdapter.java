package com.example.local_loop.UserList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        return new UserListViewer(LayoutInflater.from(context).inflate(R.layout.user_list_layout, parent, false));
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
            TextView emailText = holder.emailView;
            DatabaseHelper db = new DatabaseHelper(context);
            db.deleteUser(emailText.getText().toString());
            this.deleteEntry(emailText.getText().toString());
            notifyDataSetChanged();
        });
        holder.disable.setOnClickListener(v -> {
            TextView emailText = holder.emailView;
            DatabaseHelper db = new DatabaseHelper(context);
            db.deactivateUser(emailText.getText().toString());
            notifyDataSetChanged();
        });
        //Not currently implemented to UI, but shows in database.
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

}
