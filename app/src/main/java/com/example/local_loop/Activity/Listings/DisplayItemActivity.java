package com.example.local_loop.Activity.Listings;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.Activity.Details.CategoryDetailsActivity;
import com.example.local_loop.Activity.Details.EventDetailsActivity;
import com.example.local_loop.Adapters.DisplayItemAdapter;
import com.example.local_loop.Helpers.DatabaseHelper;
import com.example.local_loop.Helpers.DisplayItem;
import com.example.local_loop.Helpers.ViewMode;
import com.example.local_loop.Models.Account;
import com.example.local_loop.R;
import com.example.local_loop.Models.Category;
import com.example.local_loop.Models.Event;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DisplayItemActivity extends AppCompatActivity {
    private Account user;
    private DatabaseHelper dbHelper;
    private DisplayItemAdapter displayItemAdapter;

    private ImageButton addButton, removeButton, editButton;

    private ViewMode mode;

    private final List<DisplayItem> selectedItems = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_item);

        dbHelper = new DatabaseHelper(this);

        user = getIntent().getParcelableExtra("user", Account.class);
        String newMode = getIntent().getStringExtra(ViewMode.VIEW.name());
        mode = ViewMode.valueOf(newMode);

        setupButtons();
        setupRecyclerView();
        loadItems();
    }

    private void setupButtons() {
        addButton = findViewById(R.id.addButton);
        removeButton = findViewById(R.id.removeButton);
        editButton = findViewById(R.id.editButton);

        addButton.setOnClickListener(v -> showItemDialog(null));
        removeButton.setOnClickListener(v -> handleRemoveButton());
        editButton.setOnClickListener(v -> handleEditButton());

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        displayItemAdapter = new DisplayItemAdapter(new ArrayList<>(), new DisplayItemAdapter.OnItemClickListener() {
            @Override
            public void onClick(DisplayItem item) {
                if (item instanceof Category) {
                    openCategoryDetails((Category) item);
                } else if (item instanceof Event) {
                    openEventDetails((Event) item);
                }
            }

            @Override
            public void onLongClick(DisplayItem item) {
                showItemOptions(item);
            }

            @Override
            public void onRenameClick(DisplayItem item) {
                showItemDialog(item);
                exitEditMode();
            }
        });

        recyclerView.setAdapter(displayItemAdapter);
    }

    private void openCategoryDetails(Category category) {
        Intent intent = new Intent(this, CategoryDetailsActivity.class);
        intent.putExtra("user",user);
        intent.putExtra("category", category.toBundle());
        intent.putExtra(ViewMode.VIEW.name(), mode.name());
        startActivity(intent);
    }

    private void openEventDetails(Event event) {
        Intent intent = new Intent(this, EventDetailsActivity.class);
        intent.putExtra("user",user);
        intent.putExtra("event", event.toBundle());
        intent.putExtra(ViewMode.VIEW.name(), mode.name());
        startActivity(intent);
    }


    private void handleRemoveButton() {
        if (displayItemAdapter.getItemCount() == 0) {
            Toast.makeText(this, "No items to delete", Toast.LENGTH_SHORT).show();
            return;
        }
        if(displayItemAdapter.getViewMode() != ViewMode.DELETE ){
            enterDeleteMode();
        }else{
            for (DisplayItem item : selectedItems) {
                if (item instanceof Category) {
                    dbHelper.deleteCategory(item.getID());
                } else if (item instanceof Event) {
                    dbHelper.deleteEvent(item.getID());
                }
            }
            Toast.makeText(this, "Deleted " + selectedItems.size() + " items", Toast.LENGTH_SHORT).show();
            exitDeleteMode();
            loadItems();
        }
    }

    private void enterDeleteMode() {
        selectedItems.clear();
        Toast.makeText(this, "Select items to delete", Toast.LENGTH_SHORT).show();
        addButton.setEnabled(false);
        editButton.setEnabled(false);
        removeButton.setImageResource(R.drawable.outline_check_circle_24);
        displayItemAdapter.setViewMode(ViewMode.DELETE);
        displayItemAdapter.setSelectedItems(selectedItems);
    }


    private void handleEditButton() {
        if (displayItemAdapter.getItemCount() == 0) {
            Toast.makeText(this, "No items to edit", Toast.LENGTH_SHORT).show();
            return;
        }
        enterEditMode();
    }


    private void exitDeleteMode() {
        selectedItems.clear();
        addButton.setEnabled(true);
        editButton.setEnabled(true);
        removeButton.setImageResource(R.drawable.baseline_delete_forever_24);

        displayItemAdapter.setViewMode(ViewMode.DEFAULT);
        displayItemAdapter.setSelectedItems(new ArrayList<>());
        loadItems();
    }

    private void loadItems() {
        List<DisplayItem> items = new ArrayList<>();

        if (mode == ViewMode.ADMIN_CATEGORIES) {
            items.addAll(dbHelper.getAllCategories());
        }
        if(mode == ViewMode.ORG_EVENTS) {
            items.addAll(dbHelper.getEventsByOrganizer(user.getUserID()));
        }
        if(mode == ViewMode.ADMIN_EVENTS) {
            items.addAll(dbHelper.getAllEvents());
        }

        displayItemAdapter.updateItems(items);
        TextView noCategoriesTextView = findViewById(R.id.noTextView);

        noCategoriesTextView.setVisibility(View.VISIBLE);
        Log.d("DIA", "Expirementing");

        if (items.isEmpty()) {
            switch (mode){
                case ADMIN_EVENTS:
                    noCategoriesTextView.setText(R.string.no_events_created);
                    break;
                case ORG_EVENTS:
                    noCategoriesTextView.setText(R.string.no_events_created);
                    break;
                case ADMIN_CATEGORIES:
                    noCategoriesTextView.setText(R.string.no_categories_created);
                    break;
                default:
                    //noCategoriesTextView.setVisibility(View.VISIBLE);
                    break;
            }
        }else{
            noCategoriesTextView.setVisibility(View.GONE);
        }

    }




    private void showItemOptions(DisplayItem item) {
        String itemName = (item instanceof Category) ? item.getTitle() : ((Event) item).getTitle();
        String[] options = {"Rename", "Delete"};

        new AlertDialog.Builder(this)
                .setTitle(itemName)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {  // Rename
                        showItemDialog(item);
                    } else if (which == 1) {  // Delete
                        if (item instanceof Category) {
                            dbHelper.deleteCategory(item.getID());
                        } else {
                            dbHelper.deleteEvent(item.getID());
                        }
                        loadItems();
                    }
                })
                .show();
    }

    private void showItemDialog(DisplayItem itemToEdit) {
        boolean isEvent = mode != ViewMode.ADMIN_CATEGORIES;
        List<Category> categories = dbHelper.getAllCategories();

        if (isEvent && (categories == null || categories.isEmpty()) ) {
            Toast.makeText(this, "No categories exist. Please create a category first.", Toast.LENGTH_LONG).show();
            return;  // Stop here, do not open dialog

        }

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(
                isEvent ? R.layout.dialog_event_item : R.layout.dialog_category_item, null
        );

        TextInputLayout nameLayout = dialogView.findViewById(
                isEvent ? R.id.eventTitleLayout : R.id.categoryNameLayout
        );
        TextInputEditText nameInput = dialogView.findViewById(
                isEvent ? R.id.eventTitleInput : R.id.categoryNameInput
        );

        TextInputLayout descriptionLayout = dialogView.findViewById(
                isEvent ? R.id.eventDescriptionLayout : R.id.categoryDescriptionLayout
        );
        TextInputEditText descriptionInput = dialogView.findViewById(
                isEvent ? R.id.eventDescriptionInput : R.id.categoryDescriptionInput
        );

        Spinner categorySpinner;
        TextInputLayout feeLayout;
        TextInputLayout datetimeLayout;
        TextInputEditText feeInput;
        TextInputEditText datetimeInput;

        if (isEvent) {
            categorySpinner = dialogView.findViewById(R.id.eventCategorySpinner);
            feeLayout = dialogView.findViewById(R.id.eventFeeLayout);
            datetimeLayout = dialogView.findViewById(R.id.eventDateTimeLayout);
            feeInput = dialogView.findViewById(R.id.eventFeeInput);
            datetimeInput = dialogView.findViewById(R.id.eventDateTimeInput);

            datetimeInput.setOnClickListener(v -> showDateTimePicker(datetimeInput));

            List<String> categoryNames = new ArrayList<>();
            for (Category cat : categories) categoryNames.add(cat.getTitle());
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(spinnerAdapter);
        } else {
            categorySpinner = null;
            datetimeLayout = null;
            feeLayout = null;
            datetimeInput = null;
            feeInput = null;
        }

        if (itemToEdit != null) {
            if (isEvent) {
                Event event = (Event) itemToEdit;
                nameInput.setText(event.getTitle());
                descriptionInput.setText(event.getDescription());
                feeInput.setText(String.valueOf(event.getFee()));
                datetimeInput.setText(event.getDateTime());
            } else {
                Category category = (Category) itemToEdit;
                nameInput.setText(category.getTitle());
                descriptionInput.setText(category.getDescription());
            }
        }

        String dialogTitle = (itemToEdit == null)
                ? (isEvent ? "Add Event" : "Add Category")
                : (isEvent ? "Edit Event" : "Edit Category");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setCustomTitle(createDialogTitle(dialogTitle))
                .setView(dialogView)
                .setPositiveButton(itemToEdit == null ? "Add" : "Save", null)
                .setNegativeButton("Cancel", (d, which) -> {
                    if (itemToEdit != null) exitEditMode();
                    d.dismiss();
                })
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button confirmButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            confirmButton.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
            cancelButton.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));

            confirmButton.setOnClickListener(v -> {
                String name = Objects.requireNonNull(nameInput.getText()).toString().trim();
                String description = Objects.requireNonNull(descriptionInput.getText()).toString().trim();
                nameLayout.setError(null);
                descriptionLayout.setError(null);

                boolean valid = true;

                if (name.isEmpty()) {
                    nameLayout.setError("This field cannot be blank.");
                    valid = false;
                }
                if (description.isEmpty()) {
                    descriptionLayout.setError("This field cannot be blank.");
                    valid = false;
                }

                if (isEvent) {
                    boolean nameExists = dbHelper.eventTitleExists(name);
                    if (nameExists && (itemToEdit == null || !name.equals(((Event) itemToEdit).getTitle()))) {
                        nameLayout.setError("Event title already exists.");
                        valid = false;
                    }
                } else {
                    boolean nameExists = dbHelper.checkCategoryName(name);
                    if (nameExists && (itemToEdit == null || !name.equals(itemToEdit.getTitle()))) {
                        nameLayout.setError("Category name already exists.");
                        valid = false;
                    }
                }

                double fee = 0.0;
                String datetime = "";
                int categoryId = 1;

                if (isEvent) {
                    String feeText = Objects.requireNonNull(feeInput.getText()).toString().trim();
                    String datetimeText = Objects.requireNonNull(datetimeInput.getText()).toString().trim();

                    feeLayout.setError(null);
                    datetimeLayout.setError(null);

                    if (feeText.isEmpty()) {
                        feeLayout.setError("Fee is required.");
                        valid = false;
                    } else {
                        try {
                            fee = Double.parseDouble(feeText);
                        } catch (NumberFormatException e) {
                            feeLayout.setError("Invalid fee value.");
                            valid = false;
                        }
                    }

                    if (datetimeText.isEmpty()) {
                        datetimeLayout.setError("Date/Time is required.");
                        valid = false;
                    } else {
                        datetime = datetimeText;
                    }

                    int selectedPosition = categorySpinner.getSelectedItemPosition();
                    if (selectedPosition >= 0) {
                        categoryId = dbHelper.getAllCategories().get(selectedPosition).getID();
                    } else {
                        Toast.makeText(this, "Please select a category.", Toast.LENGTH_SHORT).show();
                        valid = false;
                    }
                }

                if (valid) {
                    if (itemToEdit == null) {
                        if (isEvent) {
                            Event event = new Event(-1,name, description, categoryId, user.getUserID(),fee , datetime);
                            dbHelper.addEvent(event);
                        } else {
                            dbHelper.addCategory(name, description);
                        }
                    } else {
                        if (isEvent) {
                            dbHelper.updateEvent(itemToEdit.getID(), name, description, fee, datetime, categoryId);
                        } else {
                            dbHelper.renameCategory(itemToEdit.getID(), name, description);
                        }
                        exitEditMode();
                    }
                    loadItems();
                    dialog.dismiss();
                }
            });
        });
        dialog.show();
    }


    private void enterEditMode() {
        Toast.makeText(this, "Tap a category to rename", Toast.LENGTH_SHORT).show();
        addButton.setEnabled(false);
        removeButton.setEnabled(false);
        editButton.setEnabled(false);
        displayItemAdapter.setViewMode(ViewMode.EDIT);
    }

    private void exitEditMode() {
        addButton.setEnabled(true);
        removeButton.setEnabled(true);
        editButton.setEnabled(true);
        displayItemAdapter.setViewMode(ViewMode.DEFAULT);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed() {
        switch (displayItemAdapter.getViewMode()){
            case EDIT:
                exitDeleteMode();
                Toast.makeText(this, "Delete mode cancelled", Toast.LENGTH_SHORT).show();
            case DELETE:
                exitEditMode();
                Toast.makeText(this, "Edit mode cancelled", Toast.LENGTH_SHORT).show();
            default:
                super.onBackPressed();
        }
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

    private TextView createDialogTitle(String title) {
        TextView dialogTitle = new TextView(this);
        dialogTitle.setText(title);
        dialogTitle.setTextSize(20);
        dialogTitle.setTypeface(null, Typeface.BOLD);
        dialogTitle.setPadding(40, 30, 40, 10);
        dialogTitle.setTextColor(ContextCompat.getColor(this, R.color.holo_dark_blue));
        dialogTitle.setGravity(Gravity.CENTER);
        return dialogTitle;
    }

}