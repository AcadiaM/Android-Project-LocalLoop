package com.example.local_loop.Event;

import static com.example.local_loop.Event.EventDetailsActivity.EXTRA_SOURCE;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.os.Bundle;
import android.widget.ArrayAdapter;
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
import com.example.local_loop.Welcome.WelcomePage;
import com.example.local_loop.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;


public class UserEventActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private DatabaseHelper dbHelper;
    private String userName;
    private EditText searchBar;
    private Spinner categorySpinner;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final long SEARCH_DELAY = 600; // ms


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
    }

    //Method to load the events for the user from the database
    private void loadEventsForUser() {
        String query = searchBar.getText().toString().trim();
        String selectedCategory = categorySpinner.getSelectedItem().toString();

        if (selectedCategory.equals("All")) {
            selectedCategory = "";
        }

        List<Event> events = dbHelper.searchEvents(query, selectedCategory);
        Toast.makeText(this, "Loading " + events.size() + " events for: " + userName, Toast.LENGTH_SHORT).show();
        eventAdapter.updateEvents(events);
    }

    //Method to handle back button press which redirects to previous page, in this case, the WelcomePage
    public void backButtonPressed(View view) {
        Intent intent = new Intent(UserEventActivity.this, WelcomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
