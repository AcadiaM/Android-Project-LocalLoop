package com.example.local_loop.UserList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.CreateAccount.User;
import com.example.local_loop.R;

import java.util.List;

public class RecycleAdapter extends RecyclerView.Adapter<userListViewer>{

    Context context;
    List<User> users;

    public RecycleAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public userListViewer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new userListViewer(LayoutInflater.from(context).inflate(R.layout.user_list_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull userListViewer holder, int position) {
        holder.firstView.setText(users.get(position).getFirstName());
        holder.lastView.setText(users.get(position).getLastName());
        holder.userView.setText(users.get(position).getUsername());
        holder.emailView.setText(users.get(position).getEmail());
        holder.typeView.setText(users.get(position).getRole());

    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
