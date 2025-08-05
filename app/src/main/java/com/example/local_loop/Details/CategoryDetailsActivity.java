package com.example.local_loop.Details;


import static com.example.local_loop.Details.EventDetailsActivity.EXTRA_SOURCE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.Activity.DisplayItemActivity;
import com.example.local_loop.Helpers.DatabaseHelper;
import com.example.local_loop.Helpers.DisplayItem;
import com.example.local_loop.R;
import com.example.local_loop.UserContent.Category;
import com.example.local_loop.UserContent.Event;

import java.util.ArrayList;
import java.util.List;



public class CategoryDetailsActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
    private View decorView;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);

        TextView categoryTitleText = findViewById(R.id.categoryTitleText);
        TextView categoryDescriptionText = findViewById(R.id.categoryDescriptionText);
        RecyclerView eventRecyclerView = findViewById(R.id.eventRecyclerView);

        dbHelper = new DatabaseHelper(this);

        // Get category ID passed via intent
        int categoryId = getIntent().getIntExtra("categoryId", -1);
        if (categoryId == -1) {
            Toast.makeText(this, "Invalid category", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load and display category details
        Category category = dbHelper.getCategoryById(categoryId);
        if (category != null) {
            categoryTitleText.setText(category.getName());
            categoryDescriptionText.setText(category.getDescription());
        }

        // Load and display events in that category
        List<Event> events = dbHelper.getEventsByCategoryId(categoryId);
        TextView noEventsTextView = findViewById(R.id.noEventsDetailsTextView);

        if (events == null || events.isEmpty()) {
            noEventsTextView.setVisibility(View.VISIBLE);
        } else {
            noEventsTextView.setVisibility(View.GONE);
        }
        eventRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        DisplayItemActivity.DisplayItemAdapter eventAdapter = getDisplayItemAdapter(events);

        eventRecyclerView.setAdapter(eventAdapter);

        //this is to hide the system bars and make the app immersive
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if (visibility == 0) {
                decorView.setSystemUiVisibility(hideSystemBars());
            }
        });

        Button backButton = findViewById(R.id.categoryDetailsBackButton);
        backButton.setOnClickListener(v -> onBackPressed());
    }

    @NonNull
    private DisplayItemActivity.DisplayItemAdapter getDisplayItemAdapter(List<Event> events) {
        assert events != null;
        List<DisplayItem> displayItems = new ArrayList<>(events);
        return new DisplayItemActivity.DisplayItemAdapter(displayItems, new DisplayItemActivity.DisplayItemAdapter.OnItemClickListener() {
            @Override
            public void onClick(DisplayItem item) {
                if (item instanceof Event) {
                    Event event = (Event) item;
                    Intent intent = new Intent(CategoryDetailsActivity.this, EventDetailsActivity.class);
                    intent.putExtra(EXTRA_SOURCE, getIntent().getStringExtra(EXTRA_SOURCE));
                    intent.putExtra("sourceContext", getIntent().getStringExtra("sourceContext"));
                    intent.putExtra("eventId", event.getID());
                    intent.putExtra("title", event.getName());
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
}