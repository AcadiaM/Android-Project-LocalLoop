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
    private View decorView;

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

        //this is to hide the system bars and make the app immersive
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

    }


    //this method is called when the activity gains or loses focus
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }
    private int hideSystemBars(){
        return (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
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
