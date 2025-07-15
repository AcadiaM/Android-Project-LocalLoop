package com.example.local_loop.Event;

import static com.example.local_loop.Event.EventDetailsActivity.EXTRA_SOURCE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.R;
import com.example.local_loop.Welcome.WelcomePage;
import com.example.local_loop.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class UserMyEvent extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private DatabaseHelper dbHelper;
    private String userName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_my_events);

        dbHelper = new DatabaseHelper(this);

        String categoryName = getIntent().getStringExtra("categoryName");
        userName = getIntent().getStringExtra("username");

        setTitle("Events - " + (categoryName != null ? categoryName : ""));

        RecyclerView eventRecyclerView = findViewById(R.id.recyclerViewEvents);
        eventRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        eventAdapter = new EventAdapter(getIntent().getStringExtra(EXTRA_SOURCE), userName, new ArrayList<>(), this);
        eventRecyclerView.setAdapter(eventAdapter);

        loadEventsForUser();
    }

    //Method to load the events for the user from the database
    private void loadEventsForUser() {
        List<Event> events = dbHelper.getEventsUserRequested(userName);
        eventAdapter.updateEvents(events);
    }


    public void backButtonPressed(View view) {
        Intent intent = new Intent(UserMyEvent.this, WelcomePage.class);
        intent.putExtra("username", userName);
        intent.putExtra("userType", getIntent().getStringExtra("userType"));

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
