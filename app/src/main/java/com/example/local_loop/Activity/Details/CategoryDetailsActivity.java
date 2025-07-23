package com.example.local_loop.Activity.Details;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.Adapters.DisplayItemAdapter;
import com.example.local_loop.Helpers.DatabaseHelper;
import com.example.local_loop.Helpers.DisplayItem;
import com.example.local_loop.Helpers.ViewMode;
import com.example.local_loop.Models.Account;
import com.example.local_loop.R;
import com.example.local_loop.Models.Category;
import com.example.local_loop.Models.Event;

import java.util.ArrayList;
import java.util.List;



public class CategoryDetailsActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private View decorView;
    private ViewMode mode;
    private Account session;
    Category category;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);

        TextView categoryTitleText = findViewById(R.id.categoryTitleText);
        TextView categoryDescriptionText = findViewById(R.id.categoryDescriptionText);
        RecyclerView eventRecyclerView = findViewById(R.id.eventRecyclerView);

        dbHelper = new DatabaseHelper(this);
        session = getIntent().getParcelableExtra("user", Account.class);
        mode = ViewMode.valueOf(getIntent().getStringExtra(ViewMode.VIEW.name()));
        Bundle itemBundle = getIntent().getBundleExtra("category");

        if (session == null || itemBundle==null) {
            Log.d("USER_EVENT_A","Session or bundle is null");
            finish();
            return;
        }
        // Load and display category details
        category = Category.fromBundle(itemBundle);
        categoryTitleText.setText(category.getTitle());
        categoryDescriptionText.setText(category.getDescription());


        // Load and display events in that category
        List<Event> events = dbHelper.getEventByCategory(category.getID());
        TextView noEventsTextView = findViewById(R.id.noEventsDetailsTextView);

        if (events == null || events.isEmpty()) {
            noEventsTextView.setVisibility(View.VISIBLE);
        } else {
            noEventsTextView.setVisibility(View.GONE);
        }
        eventRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        DisplayItemAdapter eventAdapter = getDisplayItemAdapter(events);
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
    private DisplayItemAdapter getDisplayItemAdapter(List<Event> events) {
        assert events != null;
        List<DisplayItem> displayItems = new ArrayList<>(events);  // Events implement DisplayItem
        // Optional: Handle long press if needed
        // Not needed in CategoryDetailsActivity
        return new DisplayItemAdapter(displayItems, new DisplayItemAdapter.OnItemClickListener() {
            @Override
            public void onClick(DisplayItem item) {
                if (item instanceof Event) {
                    Event event = (Event) item;
                    Intent intent = new Intent(CategoryDetailsActivity.this, EventDetailsActivity.class);
                    intent.putExtra("user",session);
                    intent.putExtra("event", event.toBundle());
                    intent.putExtra(ViewMode.VIEW.name(), mode.name());
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