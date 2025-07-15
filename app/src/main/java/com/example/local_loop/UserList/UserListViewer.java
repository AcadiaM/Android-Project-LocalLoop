package com.example.local_loop.UserList;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.R;

public class UserListViewer extends RecyclerView.ViewHolder {

    public TextView firstView;
    public TextView lastView;
    public TextView userView;
    public TextView emailView;
    public TextView typeView;
    public com.google.android.material.button.MaterialButton disable;
    public com.google.android.material.button.MaterialButton delete;

    public UserListViewer(@NonNull View itemView) {
        super(itemView);
        firstView = itemView.findViewById(R.id.FirstName);
        lastView = itemView.findViewById(R.id.LastName);
        userView = itemView.findViewById(R.id.Username);
        emailView = itemView.findViewById(R.id.Email);
        typeView = itemView.findViewById(R.id.Type);
        delete = itemView.findViewById(R.id.delete);
        disable = itemView.findViewById(R.id.disable);
    }
}