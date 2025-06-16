package com.example.local_loop.UserList;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.R;
import com.example.local_loop.database.DatabaseHelper;

public class UserListViewer extends RecyclerView.ViewHolder {

    TextView firstView, lastView, userView, emailView, typeView;
    ImageButton disable, delete;

    public UserListViewer(@NonNull View itemView) {
        super(itemView);
        firstView = itemView.findViewById(R.id.FirstName);
        lastView = itemView.findViewById(R.id.LastName);
        userView = itemView.findViewById(R.id.Username);
        emailView = itemView.findViewById(R.id.Email);
        typeView = itemView.findViewById(R.id.Type);
        disable = itemView.findViewById(R.id.disable);
        delete = itemView.findViewById(R.id.delete);
    }
}