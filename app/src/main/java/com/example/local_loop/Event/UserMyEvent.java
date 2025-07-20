package com.example.local_loop.Event;

import static com.example.local_loop.Event.EventDetailsActivity.EXTRA_SOURCE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.Category.DisplayItem;
import com.example.local_loop.Category.DisplayItemActivity;
import com.example.local_loop.Category.DisplayItemAdapter;
import com.example.local_loop.R;
import com.example.local_loop.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class UserMyEvent extends AppCompatActivity {
    private DisplayItemAdapter eventAdapter;
    private DatabaseHelper dbHelper;
    private String username;
    private View decorView;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_my_events);

        dbHelper = new DatabaseHelper(this);

        username = getIntent().getStringExtra("username");

        RecyclerView eventRecyclerView = findViewById(R.id.recyclerViewEvents);
        eventRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        eventAdapter = new DisplayItemAdapter(new ArrayList<>(), new DisplayItemAdapter.OnItemClickListener() {
            @Override
            public void onClick(DisplayItem item) {
                if (item instanceof Event) {
                    Event event = (Event) item;
                    Intent intent = new Intent(UserMyEvent.this, EventDetailsActivity.class);
                    intent.putExtra(EXTRA_SOURCE, getIntent().getStringExtra(EXTRA_SOURCE));
                    intent.putExtra("sourceContext", UserMyEvent.class.getSimpleName());
                    intent.putExtra("attendeeId", username);
                    intent.putExtra("eventId", event.getID());
                    intent.putExtra("title", event.getTitle());
                    intent.putExtra("description", event.getDescription());
                    intent.putExtra("fee", String.valueOf(event.getFee()));
                    intent.putExtra("datetime", event.getDateTime());
                    intent.putExtra("categoryId", event.getCategoryId());
                    intent.putExtra("organizer", event.getOrganizer());
                    startActivity(intent);
                }
            }

            @Override
            public void onLongClick(DisplayItem item) {
                // Optional: Handle long press if needed
            }

            @Override
            public void onRenameClick(DisplayItem item) {
                // Not needed in CategoryDetailsActivity
            }
        });

        eventRecyclerView.setAdapter(eventAdapter);

        loadEventsForUser();

        //this is to hide the system bars and make the app immersive
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if (visibility == 0) {
                decorView.setSystemUiVisibility(hideSystemBars());
            }
        });

        Button backButton = findViewById(R.id.myEventsBackButton);
        backButton.setOnClickListener(v -> onBackPressed());
    }


    //this method is called when the activity gains or loses focus
    @SuppressWarnings("deprecation")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }

    @SuppressWarnings("deprecation")
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
        List<Event> events = dbHelper.getEventsUserRequested(username);
        TextView noMyEventsTextView = findViewById(R.id.noMyEventsTextView);
        if (events == null || events.isEmpty()) {
            noMyEventsTextView.setVisibility(View.VISIBLE);
        } else {
            noMyEventsTextView.setVisibility(View.GONE);
        }
        assert events != null;
        List<DisplayItem> displayItems = new ArrayList<>(events);
        eventAdapter.updateItems(displayItems);
    }

}
