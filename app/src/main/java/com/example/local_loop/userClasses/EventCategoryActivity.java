package com.example.local_loop.userClasses;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.local_loop.R;
import com.example.local_loop.database.DBCategoryHelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class EventCategoryActivity extends AppCompatActivity {

    DBCategoryHelper dbHelper;
    LinearLayout eventContainer;
    Button addEventButton;

    int categoryId;
    String categoryName;
    TextView categoryDescriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dbHelper = new DBCategoryHelper(this);
        eventContainer = findViewById(R.id.eventContainer);
        addEventButton = findViewById(R.id.addEventButton);

        // Get category info from Intent
        categoryId = getIntent().getIntExtra("categoryId", -1);
        categoryName = getIntent().getStringExtra("categoryName");

        categoryDescriptionTextView = findViewById(R.id.categoryDescriptionTextView);

        // Get description from Intent
        String categoryDescription = getIntent().getStringExtra("categoryDescription");

        if (categoryDescription != null && !categoryDescription.isEmpty()) {
            String descriptionText = "Description: " + categoryDescription;
            categoryDescriptionTextView.setText(descriptionText);
            categoryDescriptionTextView.setVisibility(View.VISIBLE);
        } else {
            categoryDescriptionTextView.setVisibility(View.GONE);
        }

        setTitle("Events in " + categoryName);

        loadEvents();

        addEventButton.setOnClickListener(v -> showAddEventDialog());
    }

    private void loadEvents() {
        eventContainer.removeAllViews();
        List<Event> events = dbHelper.getEventsByCategory(categoryId);

        for (Event event : events) {
            Button eventButton = new Button(this);
            eventButton.setText(event.getTitle());

            eventButton.setOnLongClickListener(v -> {
                showEventOptions(event);
                return true;
            });

            eventContainer.addView(eventButton);
        }
    }

    private void showAddEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Event");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String title = input.getText().toString().trim();
            if (!title.isEmpty()) {
                dbHelper.addEvent(categoryId, title);
                loadEvents();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showEventOptions(Event event) {
        String[] options = {"Rename", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                showRenameEventDialog(event);
            } else if (which == 1) {
                dbHelper.deleteEvent(event.getId());
                loadEvents();
            }
        });
        builder.show();
    }

    private void showRenameEventDialog(Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rename Event");

        final EditText input = new EditText(this);
        input.setText(event.getTitle());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newTitle = input.getText().toString().trim();
            if (!newTitle.isEmpty()) {
                dbHelper.renameEvent(event.getId(), newTitle);
                loadEvents();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}