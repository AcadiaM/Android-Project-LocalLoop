package com.example.local_loop.Account;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.Helpers.MODE;
import com.example.local_loop.Helpers.DatabaseHelper;
import com.example.local_loop.R;
import com.example.local_loop.Adapters.UserDisplayAdapter;

import java.util.ArrayList;
import java.util.List;

public class DisplayUserActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserDisplayAdapter adapter;
    private List<Account> users;
    private DatabaseHelper db;
    private MODE mode = MODE.DEFAULT;
    private boolean isAttendeeMode = false;
    private int eventId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user);

        db = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.userRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch from intent if this is attendee mode
        if (getIntent() != null && getIntent().hasExtra("isAttendeeMode")) {
            isAttendeeMode = getIntent().getBooleanExtra("isAttendeeMode", false);
            eventId = getIntent().getIntExtra("eventId", -1);
        }

        loadUsers();
    }

    private void loadUsers() {
        if (isAttendeeMode && eventId != -1) {
            users = db.getPendingAttendeesForEvent(eventId);
        } else {
            users = db.getAllUsers();
        }

        if (users == null) users = new ArrayList<>();

        adapter = new UserDisplayAdapter(this, users, isAttendeeMode, eventId);
        recyclerView.setAdapter(adapter);
    }

    public void switchMode(View view) {
        if (mode == MODE.DEFAULT) {
            mode = MODE.DELETE;
            Toast.makeText(this, "Delete Mode", Toast.LENGTH_SHORT).show();
        } else if (mode == MODE.DELETE) {
            mode = MODE.EDIT;
            Toast.makeText(this, "Edit Mode", Toast.LENGTH_SHORT).show();
        } else {
            mode = MODE.DEFAULT;
            Toast.makeText(this, "Default Mode", Toast.LENGTH_SHORT).show();
        }
        // You can expand this to notify adapter if it supports multiple modes
    }
}

