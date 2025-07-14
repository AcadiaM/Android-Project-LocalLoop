package com.example.local_loop.Event;

import static com.example.local_loop.Event.EventDetailsActivity.EXTRA_SOURCE;

import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.R;
import com.example.local_loop.Welcome.WelcomePage;
import com.example.local_loop.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;


public class UserEventActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private DatabaseHelper dbHelper;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_user_list);

        dbHelper = new DatabaseHelper(this);

        // Get category info passed from CategoryActivity
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
    private void loadEventsForUser(){
        List<Event> events = dbHelper.getAllEvents();
        Toast.makeText(this, "Loading events for user: " + userName, Toast.LENGTH_SHORT).show();
        eventAdapter.updateEvents(events);

    }

    //Method to handle back button press which redirects to previous page, in this case, the WelcomePage
    public void backButtonPressed(View view) {
        Intent intent = new Intent(UserEventActivity.this, WelcomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
