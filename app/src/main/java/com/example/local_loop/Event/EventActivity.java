package com.example.local_loop.Event;

import static com.example.local_loop.Event.EventDetailsActivity.EXTRA_SOURCE;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.Category.Category;
import com.example.local_loop.R;
import com.example.local_loop.Welcome.OrganizerWelcomePage;
import com.example.local_loop.Welcome.WelcomePage;
import com.example.local_loop.database.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EventActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EventAdapter eventAdapter;

    private ImageButton addEventButton, removeEventButton, editEventButton;

    private boolean isDeleteMode = false;
    private boolean isEditMode = false;
    private final List<Integer> selectedEventIds = new ArrayList<>();

    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        dbHelper = new DatabaseHelper(this);

        // Get category info passed from CategoryActivity
        String categoryName = getIntent().getStringExtra("categoryName");
        userName = getIntent().getStringExtra("username");

        setTitle("Events - " + (categoryName != null ? categoryName : ""));

        addEventButton = findViewById(R.id.addEventButton);
        removeEventButton = findViewById(R.id.removeEventButton);
        editEventButton = findViewById(R.id.editEventButton);

        addEventButton.setOnClickListener(v -> {
            List<Category> categories = dbHelper.getAllCategories();
            if (categories == null || categories.isEmpty()) {
                Toast.makeText(this, "Please add a category first.", Toast.LENGTH_LONG).show();
            } else {
                showAddEventDialog();
            }
        });

        RecyclerView eventRecyclerView = findViewById(R.id.recyclerViewEvents);
        eventRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        eventAdapter = new EventAdapter(getIntent().getStringExtra(EXTRA_SOURCE), new ArrayList<>(), this, new EventAdapter.OnEventClickListener() {
            @Override
            public void onClick(Event event) {
                if (isEditMode) {
                    showRenameEventDialog(event);
                    exitEditMode();
                } else if (isDeleteMode) {
                    toggleSelection(event.getID());
                } else {
                    // Normal click, maybe open event details or edit screen
                    Toast.makeText(EventActivity.this, "Clicked event: " + event.getTitle(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLongClick(Event event) {
                showEventOptions(event);
            }

            @Override
            public void onRenameClick(Event event) {
                showRenameEventDialog(event);

            }
        });

        eventRecyclerView.setAdapter(eventAdapter);

        loadEvents();

        removeEventButton.setOnClickListener(v -> {
            if (eventAdapter.getItemCount() == 0){
                Toast.makeText(this,"No events to delete", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isDeleteMode) {
                enterDeleteMode(); // no preselection
            } else {
                // Confirm and delete selected
                for (int eventId : selectedEventIds) {
                    dbHelper.deleteEvent(eventId);
                }
                Toast.makeText(this, "Deleted " + selectedEventIds.size() + " events", Toast.LENGTH_SHORT).show();
                exitDeleteMode();
                loadEvents();
            }
        });

        editEventButton.setOnClickListener(v -> {
            if (eventAdapter.getItemCount() == 0){
                Toast.makeText(this,"No events to edit", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isEditMode) {
                isEditMode = true;
                Toast.makeText(this, "Tap an event to rename", Toast.LENGTH_SHORT).show();
                addEventButton.setEnabled(false);
                removeEventButton.setEnabled(false);
                editEventButton.setEnabled(false);
                eventAdapter.setEditMode(true);
            }
        });
    }

    private void loadEvents() {
        List<Event> events = dbHelper.getEventsByOrganizer(userName);
        eventAdapter.updateEvents(events);
    }

    private void showEventOptions(Event event) {
        String[] options = {"Rename", "Delete"};
        new AlertDialog.Builder(this)
                .setTitle(event.getTitle())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showRenameEventDialog(event);
                    } else if (which == 1) {
                        dbHelper.deleteEvent(event.getID());
                        loadEvents();
                    }
                })
                .show();
    }



    private void showAddEventDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_event_item, null);

        Spinner categorySpinner = dialogView.findViewById(R.id.eventCategorySpinner);
        dbHelper = new DatabaseHelper(this);

        // Step 1: Load categories from database
        List<Category> categories = dbHelper.getAllCategories();  // Make sure this method exists in DatabaseHelper

        // Step 2: Create a list of category names
        List<String> categoryNames = new ArrayList<>();
        for (Category cat : categories) {
            categoryNames.add(cat.getName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categoryNames
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);


        TextInputLayout titleLayout = dialogView.findViewById(R.id.eventTitleLayout);
        TextInputLayout descriptionLayout = dialogView.findViewById(R.id.eventDescriptionLayout);
        TextInputLayout feeLayout = dialogView.findViewById(R.id.eventFeeLayout);
        TextInputLayout datetimeLayout = dialogView.findViewById(R.id.eventDateTimeLayout);

        TextInputEditText titleInput = dialogView.findViewById(R.id.eventTitleInput);
        TextInputEditText descriptionInput = dialogView.findViewById(R.id.eventDescriptionInput);
        TextInputEditText feeInput = dialogView.findViewById(R.id.eventFeeInput);
        TextInputEditText datetimeInput = dialogView.findViewById(R.id.eventDateTimeInput);

        datetimeInput.setOnClickListener(v -> showDateTimePicker(datetimeInput));

        TextView title = new TextView(this);
        title.setText(getString(R.string.add_event));
        title.setPadding(40, 40, 40, 40);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(ContextCompat.getColor(this, R.color.holo_dark_blue));
        title.setTextSize(20f);
        title.setTypeface(null, Typeface.BOLD);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setCustomTitle(title)
                .setView(dialogView)
                .setPositiveButton("Add", null) // override later
                .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

            addButton.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
            cancelButton.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));

            addButton.setOnClickListener(v -> {
                String titleText = Objects.requireNonNull(titleInput.getText()).toString().trim();
                String descriptionText = Objects.requireNonNull(descriptionInput.getText()).toString().trim();
                String feeText = Objects.requireNonNull(feeInput.getText()).toString().trim();
                String datetimeText = Objects.requireNonNull(datetimeInput.getText()).toString().trim();

                boolean valid = true;

                // Reset errors
                titleLayout.setError(null);
                descriptionLayout.setError(null);
                feeLayout.setError(null);
                datetimeLayout.setError(null);

                if (titleText.isEmpty()) {
                    titleLayout.setError("Required");
                    valid = false;
                } else if (dbHelper.eventTitleExists(titleText)) {
                titleLayout.setError("Event title already exists");
                valid = false;
                }

                if (descriptionText.isEmpty()) {
                    descriptionLayout.setError("Required");
                    valid = false;
                }

                if (feeText.isEmpty()) {
                    feeLayout.setError("Required");
                    valid = false;
                } else {
                    try {
                        double feeVal = Double.parseDouble(feeText);
                        if (feeVal < 0) {
                            feeLayout.setError("Must be positive");
                            valid = false;
                        }
                    } catch (NumberFormatException e) {
                        feeLayout.setError("Invalid number");
                        valid = false;
                    }
                }

                if (datetimeText.isEmpty()) {
                    datetimeLayout.setError("Required");
                    valid = false;
                }
                // Optional: Add datetime format validation here

                if (valid) {
                    // 1. Get selected category from Spinner
                    int selectedPosition = categorySpinner.getSelectedItemPosition();
                    if (selectedPosition >= 0) {
                        Category selectedCategory = categories.get(selectedPosition);  // 'categories' was loaded earlier
                        int categoryId = selectedCategory.getID();  // Extract ID for the DB call

                        // 2. Save event with all fields
                        dbHelper.addEvent(
                                titleText,
                                descriptionText,
                                categoryId,
                                datetimeText,
                                Double.parseDouble(feeText),
                                userName
                        );

                        // 3. Refresh list and close dialog
                        loadEvents();
                        Toast.makeText(this, "Event added", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        dialog.show();
    }

    private void showRenameEventDialog(Event event) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_event_item, null);

        Spinner categorySpinner = dialogView.findViewById(R.id.eventCategorySpinner);
        dbHelper = new DatabaseHelper(this);

        // Step 1: Load categories from database
        List<Category> categories = dbHelper.getAllCategories();  // Make sure this method exists in DatabaseHelper

        // Step 2: Create a list of category names
        List<String> categoryNames = new ArrayList<>();
        for (Category cat : categories) {
            categoryNames.add(cat.getName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categoryNames
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);

        TextInputLayout titleLayout = dialogView.findViewById(R.id.eventTitleLayout);
        TextInputLayout descriptionLayout = dialogView.findViewById(R.id.eventDescriptionLayout);
        TextInputLayout feeLayout = dialogView.findViewById(R.id.eventFeeLayout);
        TextInputLayout datetimeLayout = dialogView.findViewById(R.id.eventDateTimeLayout);

        TextInputEditText titleInput = dialogView.findViewById(R.id.eventTitleInput);
        TextInputEditText descriptionInput = dialogView.findViewById(R.id.eventDescriptionInput);
        TextInputEditText feeInput = dialogView.findViewById(R.id.eventFeeInput);
        TextInputEditText datetimeInput = dialogView.findViewById(R.id.eventDateTimeInput);

        datetimeInput.setOnClickListener(v -> showDateTimePicker(datetimeInput));


        // Set initial values
        titleInput.setText(event.getTitle());
        descriptionInput.setText(event.getDescription());
        feeInput.setText(String.valueOf(event.getFee()));
        datetimeInput.setText(event.getDateTime());

        TextView title = new TextView(this);
        title.setText(getString(R.string.edit_event));
        title.setPadding(40, 40, 40, 40);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(ContextCompat.getColor(this, R.color.holo_dark_blue));
        title.setTextSize(20f);
        title.setTypeface(null, Typeface.BOLD);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setCustomTitle(title)
                .setView(dialogView)
                .setPositiveButton("Save", null)   // override later
                .setNegativeButton("Cancel", (d, which) -> {
                    exitEditMode();
                    d.dismiss();
                })
                .create();

        dialog.setOnShowListener(dialogInterface -> {
                    Button saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    Button cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                    saveButton.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
                    cancelButton.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));

                    saveButton.setOnClickListener(v -> {
                        String newTitle = Objects.requireNonNull(titleInput.getText()).toString().trim();
                        String newDescription = Objects.requireNonNull(descriptionInput.getText()).toString().trim();
                        String newFeeText = Objects.requireNonNull(feeInput.getText()).toString().trim();
                        String newDatetime = Objects.requireNonNull(datetimeInput.getText()).toString().trim();

                        boolean valid = true;

                        // Reset errors
                        titleLayout.setError(null);
                        descriptionLayout.setError(null);
                        feeLayout.setError(null);
                        datetimeLayout.setError(null);

                        // Get selected category first
                        int selectedPosition = categorySpinner.getSelectedItemPosition();
                        if (selectedPosition < 0) {
                            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Category selectedCategory = categories.get(selectedPosition);
                        int categoryId = selectedCategory.getID();

                        // Validation
                        if (newTitle.isEmpty()) {
                            titleLayout.setError("Required");
                            valid = false;
                        } else if (!newTitle.equals(event.getTitle()) && dbHelper.eventTitleExistsInCategory(newTitle, categoryId)) {
                            titleLayout.setError("Event title already exists");
                            valid = false;
                        }

                        if (newDescription.isEmpty()) {
                            descriptionLayout.setError("Required");
                            valid = false;
                        }

                        if (newFeeText.isEmpty()) {
                            feeLayout.setError("Required");
                            valid = false;
                        } else {
                            try {
                                double feeVal = Double.parseDouble(newFeeText);
                                if (feeVal < 0) {
                                    feeLayout.setError("Must be positive");
                                    valid = false;
                                }
                            } catch (NumberFormatException e) {
                                feeLayout.setError("Invalid number");
                                valid = false;
                            }
                        }

                        if (newDatetime.isEmpty()) {
                            datetimeLayout.setError("Required");
                            valid = false;
                        }

                        if (valid) {
                            boolean updated = dbHelper.updateEvent(
                                    event.getID(),
                                    newTitle,
                                    newDescription,
                                    Double.parseDouble(newFeeText),
                                    newDatetime,
                                    categoryId // <-- Update category
                            );

                            if (updated) {
                                loadEvents();
                                Toast.makeText(this, "Event updated", Toast.LENGTH_SHORT).show();
                                exitEditMode();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(this, "Failed to update event", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                });
            dialog.show();
    }

    private void enterDeleteMode(){
        isDeleteMode = true;
        selectedEventIds.clear();
        Toast.makeText(this, "Select events to delete", Toast.LENGTH_SHORT).show();

        addEventButton.setEnabled(false);
        editEventButton.setEnabled(false);

        removeEventButton.setImageResource(R.drawable.outline_check_circle_24);

        eventAdapter.setDeleteMode(true);
        eventAdapter.setSelectedEventIds(selectedEventIds);
    }

    private void exitDeleteMode() {
        isDeleteMode = false;
        selectedEventIds.clear();

        addEventButton.setEnabled(true);
        editEventButton.setEnabled(true);

        removeEventButton.setImageResource(R.drawable.baseline_delete_forever_24);

        eventAdapter.setDeleteMode(false);
        eventAdapter.setSelectedEventIds(new ArrayList<>());
    }

    private void toggleSelection(int eventId) {
        if (selectedEventIds.contains(eventId)) {
            selectedEventIds.remove(Integer.valueOf(eventId));
        } else {
            selectedEventIds.add(eventId);
        }
        eventAdapter.setSelectedEventIds(selectedEventIds);
    }

    @Override
    public void onBackPressed() {
        if (isDeleteMode) {
            exitDeleteMode();
            Toast.makeText(this, "Delete mode cancelled", Toast.LENGTH_SHORT).show();
        } else if (isEditMode) {
            exitEditMode();
        } else {
            super.onBackPressed();
        }
    }

    private void exitEditMode() {
        isEditMode = false;
        addEventButton.setEnabled(true);
        removeEventButton.setEnabled(true);
        editEventButton.setEnabled(true);
        eventAdapter.setEditMode(false);
    }

    private void showDateTimePicker(TextInputEditText datetimeInput) {
        final Calendar calendar = Calendar.getInstance();

        // Date Picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                R.style.MyDatePickerDialogTheme,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Time Picker
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            this,
                            R.style.MyDatePickerDialogTheme,
                            (timeView, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());
                                datetimeInput.setText(format.format(calendar.getTime()));
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            false
                    );

                    // Change button colors AFTER show
                    timePickerDialog.setOnShowListener(dialog -> {
                        Button ok = timePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        Button cancel = timePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                        int blue = Color.parseColor("#0099CC"); // holo_blue_dark
                        if (ok != null) ok.setTextColor(blue);
                        if (cancel != null) cancel.setTextColor(blue);
                    });

                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Block past dates (only allow tomorrow and later)
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        datePickerDialog.getDatePicker().setMinDate(tomorrow.getTimeInMillis());

        // Change button colors AFTER show
        datePickerDialog.setOnShowListener(dialog -> {
            Button ok = datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button cancel = datePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            int blue = Color.parseColor("#0099CC"); // holo_blue_dark
            if (ok != null) ok.setTextColor(blue);
            if (cancel != null) cancel.setTextColor(blue);
        });

        datePickerDialog.show();
    }

    public void backButtonPressed(View view) {
        // Handle back button press
        if (isDeleteMode) {
            exitDeleteMode();
            Toast.makeText(this, "Delete mode cancelled", Toast.LENGTH_SHORT).show();
        } else if (isEditMode) {
            exitEditMode();
        } else {
            Intent intent = new Intent(EventActivity.this, OrganizerWelcomePage.class);
            intent.putExtra("username", userName);
            intent.putExtra("userType", getIntent().getStringExtra("userType"));
            startActivity(intent);
        }
    }
}

