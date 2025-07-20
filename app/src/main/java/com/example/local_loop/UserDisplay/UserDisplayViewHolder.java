package com.example.local_loop.UserDisplay;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.R;

public class UserDisplayViewHolder extends RecyclerView.ViewHolder {

    TextView firstView, lastView, userView, emailView, typeView;
    com.google.android.material.button.MaterialButton disable, delete;

    public UserDisplayViewHolder(@NonNull View itemView) {
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