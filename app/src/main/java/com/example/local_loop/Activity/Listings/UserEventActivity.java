package com.example.local_loop.Activity.Listings;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.Activity.Details.EventDetailsActivity;
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

public class UserEventActivity extends AppCompatActivity {

    private DisplayItemAdapter eventAdapter;
    private DatabaseHelper dbHelper;
    private ViewMode mode;
    private Account user;

    private EditText searchBar;
    private Spinner categorySpinner;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final long SEARCH_DELAY = 600; // ms
    private View decorView;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_user_list);  // Use shared layout

        dbHelper = new DatabaseHelper(this);
        user = getIntent().getParcelableExtra("user", Account.class);
        mode = ViewMode.valueOf(getIntent().getStringExtra(ViewMode.VIEW.name()));

        if(user == null || mode == null){
            Log.d("User/Mode Null", this.getLocalClassName());
        }

        RecyclerView eventRecyclerView = findViewById(R.id.recyclerViewEvents);
        eventRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        searchBar = findViewById(R.id.searchBar);
        categorySpinner = findViewById(R.id.categorySpinner);
        TextView titleTextView = findViewById(R.id.events);

        // Handle UI visibility
        if (mode == ViewMode.PARTICIPANT_EVENTS) {
            searchBar.setVisibility(View.GONE);
            categorySpinner.setVisibility(View.GONE);
            // Set the title or any text view you want:
            titleTextView.setText(R.string.my_events);
        } else {
            setupCategorySpinner();
            setupSearchBar();
        }
        eventAdapter = new DisplayItemAdapter(new ArrayList<>(), new DisplayItemAdapter.OnItemClickListener() {
            @Override
            public void onClick(DisplayItem item) {
                if (item instanceof Event) {
                    Event event = (Event) item;
                    Intent intent = new Intent(UserEventActivity.this, EventDetailsActivity.class);
                    intent.putExtra("user", user);
                    intent.putExtra("event", event.toBundle());
                    intent.putExtra(ViewMode.VIEW.name(), mode.name());
                    startActivity(intent);
                }
                // Do something with event
            }

            @Override
            public void onLongClick(DisplayItem item) {
                // Optional
            }

            @Override
            public void onRenameClick(DisplayItem item) {
                // Optional
            }
        });

        eventRecyclerView.setAdapter(eventAdapter);

        loadEvents();
        Button backButton = findViewById(R.id.browseEventBackButton);
        backButton.setOnClickListener(v -> onBackPressed());

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if (visibility == 0) {
                decorView.setSystemUiVisibility(hideSystemBars());
            }
        });
    }


    private void loadEvents() {
        List<Event> events;
        if(mode == ViewMode.PARTICIPANT_EVENTS){
            events = dbHelper.getParticipantEventRequests(user.getUserID());
        }
        else{
            String query = searchBar.getText().toString().trim();
            String selectedCategory = categorySpinner.getSelectedItem().toString();
            if (selectedCategory.equals("All")) selectedCategory = "";
            events = dbHelper.searchEvents(query, selectedCategory);
        }

        TextView emptyView = findViewById(R.id.noEventsUserTextView);
        emptyView.setVisibility(events == null || events.isEmpty() ? View.VISIBLE : View.GONE);
        assert events != null;
        eventAdapter.updateItems(new ArrayList<>(events));
    }

    //Method to load the events for the user from the database
    private void loadEventsForUser() {
        String query = searchBar.getText().toString().trim();
        String selectedCategory = categorySpinner.getSelectedItem().toString();

        if (selectedCategory.equals("All")) {
            selectedCategory = "";
        }

        List<Event> events = dbHelper.searchEvents(query, selectedCategory);
        TextView noEventsUserTextView = findViewById(R.id.noEventsUserTextView);
        if (events == null || events.isEmpty()) {
            noEventsUserTextView.setVisibility(View.VISIBLE);
        } else {
            noEventsUserTextView.setVisibility(View.GONE);
        }
        assert events != null;
        Toast.makeText(this, "Loading " + events.size() + " events for: " + user.getUsername(), Toast.LENGTH_SHORT).show();
        List<DisplayItem> displayItems = new ArrayList<>(events);
        eventAdapter.updateItems(displayItems);

    }

    private void setupCategorySpinner() {
        List<String> categoryNames = new ArrayList<>();
        categoryNames.add("All");

        for (Category c : dbHelper.getAllCategories()) {
            categoryNames.add(c.getTitle());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categoryNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);

        categorySpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                loadEventsForUser(); // Filtered by selected category
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });
    }

    private void setupSearchBar() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> loadEventsForUser();  // Debounced query
                handler.postDelayed(searchRunnable, SEARCH_DELAY);
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