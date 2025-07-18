package com.example.local_loop.Event;

import static com.example.local_loop.Event.EventDetailsActivity.EXTRA_SOURCE;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Spinner;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.local_loop.Category.Category;
import com.example.local_loop.R;
import com.example.local_loop.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;


public class UserEventActivity extends AppCompatActivity {
    private EventAdapter eventAdapter;
    private DatabaseHelper dbHelper;
    private String userName;
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
        setContentView(R.layout.activity_event_user_list);

        dbHelper = new DatabaseHelper(this);

        // Get category info passed from CategoryActivity
        String categoryName = getIntent().getStringExtra("categoryName");
        userName = getIntent().getStringExtra("username");

        setTitle("Events - " + (categoryName != null ? categoryName : ""));


        RecyclerView eventRecyclerView = findViewById(R.id.recyclerViewEvents);
        searchBar = findViewById(R.id.searchBar);
        categorySpinner = findViewById(R.id.categorySpinner);
        eventRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        eventAdapter = new EventAdapter(getIntent().getStringExtra(EXTRA_SOURCE), userName, new ArrayList<>(), this);
        eventRecyclerView.setAdapter(eventAdapter);
        setupCategorySpinner();
        setupSearchBar();

        loadEventsForUser();

        //this is to hide the system bars and make the app immersive
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if (visibility == 0) {
                decorView.setSystemUiVisibility(hideSystemBars());
            }
        });

        Button backButton = findViewById(R.id.browseEventBackButton);
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
        Toast.makeText(this, "Loading " + events.size() + " events for: " + userName, Toast.LENGTH_SHORT).show();
        eventAdapter.updateEvents(events);
    }

    private void setupCategorySpinner() {
        List<String> categoryNames = new ArrayList<>();
        categoryNames.add("All");

        for (Category c : dbHelper.getAllCategories()) {
            categoryNames.add(c.getName());
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
}
