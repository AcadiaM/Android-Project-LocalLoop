package com.example.local_loop.Displays;

import static com.example.local_loop.Details.EventDetailsActivity.EXTRA_SOURCE;
import static com.example.local_loop.Details.EventDetailsActivity.SOURCE_ADMIN;
import static com.example.local_loop.Details.EventDetailsActivity.SOURCE_ORGANIZER;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.CreateAccount.User;
import com.example.local_loop.Details.CategoryDetailsActivity;
import com.example.local_loop.Details.EventDetailsActivity;
import com.example.local_loop.Helpers.DatabaseHelper;
import com.example.local_loop.Helpers.DisplayItem;
import com.example.local_loop.R;
import com.example.local_loop.UserContent.Category;
import com.example.local_loop.UserContent.Event;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DisplayItemActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private DisplayItemAdapter displayItemAdapter;

    private ImageButton addButton, removeButton, editButton;

    private boolean isDeleteMode = false;

    private final List<DisplayItem> selectedItems = new ArrayList<>();

    private boolean isEditMode = false;

    private String source;    // "SOURCE_ADMIN" or "SOURCE_ORGANIZER"
    private String organizerUsername;  // For filtering events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_item);

        dbHelper = new DatabaseHelper(this);

        source = getIntent().getStringExtra(EXTRA_SOURCE);
        User user = getIntent().getParcelableExtra("user", User.class);
        assert user != null;
        organizerUsername = user.getUsername();

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

    // --- INNER CLASS FOR ADAPTER ---
    public static class DisplayItemAdapter extends RecyclerView.Adapter<DisplayItemAdapter.ItemViewHolder> {
        public interface OnItemClickListener {
            void onClick(DisplayItem item);
            void onLongClick(DisplayItem item);
            void onRenameClick(DisplayItem item);
        }

        private List<DisplayItem> items;
        private final OnItemClickListener listener;

        private boolean deleteMode = false;
        private boolean editMode = false;

        private List<DisplayItem> selectedItems = new ArrayList<>();

        public DisplayItemAdapter(List<DisplayItem> items, OnItemClickListener listener) {
            this.items = items;
            this.listener = listener;
        }

        @SuppressLint("NotifyDataSetChanged")
        public void setSelectedItems(List<DisplayItem> selectedItems) {
            this.selectedItems = selectedItems;
            notifyDataSetChanged();
        }

        @SuppressLint("NotifyDataSetChanged")
        public void setDeleteMode(boolean enabled) {
            this.deleteMode = enabled;
            notifyDataSetChanged();
        }

        @SuppressLint("NotifyDataSetChanged")
        public void setEditMode(boolean enabled) {
            this.editMode = enabled;
            notifyDataSetChanged();
        }

        @SuppressLint("NotifyDataSetChanged")
        public void updateItems(List<DisplayItem> newItems) {
            this.items = newItems;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_item, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            DisplayItem item = items.get(position);
            holder.button.setText(item.getName());

            if (deleteMode) {
                holder.button.setOnClickListener(v -> {
                    if (selectedItems.contains(item)) {
                        selectedItems.remove(item);
                        holder.button.setBackgroundColor(Color.LTGRAY);
                    } else {
                        selectedItems.add(item);
                        holder.button.setBackgroundColor(Color.TRANSPARENT);
                    }
                });
                holder.button.setBackgroundColor(selectedItems.contains(item) ? Color.TRANSPARENT : Color.LTGRAY);
            } else if (editMode) {
                holder.button.setOnClickListener(v -> {
                    if (listener != null) listener.onRenameClick(item);
                });
                holder.button.setBackgroundColor(Color.LTGRAY);
            } else {
                holder.button.setOnClickListener(v -> {
                    if (listener != null) listener.onClick(item);
                });
                holder.button.setBackgroundColor(Color.TRANSPARENT);
            }

            holder.button.setOnLongClickListener(v -> {
                if (listener != null) listener.onLongClick(item);
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public static class ItemViewHolder extends RecyclerView.ViewHolder {
            Button button;

            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                button = itemView.findViewById(R.id.categoryButton);
            }
        }

    }
    private void openCategoryDetails(Category category) {
        Intent intent = new Intent(this, CategoryDetailsActivity.class);
        intent.putExtra(EXTRA_SOURCE, getIntent().getStringExtra(EXTRA_SOURCE));
        intent.putExtra("sourceContext", DisplayItemActivity.class.getSimpleName());
        intent.putExtra("categoryId", category.getID());
        intent.putExtra("categoryName", category.getName());
        intent.putExtra("categoryDescription", category.getDescription());
        startActivity(intent);
    }

    private void openEventDetails(Event event) {
        Intent intent = new Intent(this, EventDetailsActivity.class);
        intent.putExtra(EXTRA_SOURCE, getIntent().getStringExtra(EXTRA_SOURCE));
        intent.putExtra("sourceContext", DisplayItemActivity.class.getSimpleName());
        intent.putExtra("eventId", event.getID());
        intent.putExtra("title", event.getName());
        intent.putExtra("description", event.getDescription());
        intent.putExtra("fee", String.valueOf(event.getFee()));
        intent.putExtra("datetime", event.getDateTime());
        intent.putExtra("categoryId", event.getCategoryId());
        intent.putExtra("organizer", event.getOrganizer());
        startActivity(intent);
    }


    private void handleRemoveButton() {
        if (displayItemAdapter.getItemCount() == 0) {
            Toast.makeText(this, "No items to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isDeleteMode) {
            enterDeleteMode();
        } else {
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


    private void handleEditButton() {
        if (displayItemAdapter.getItemCount() == 0) {
            Toast.makeText(this, "No items to edit", Toast.LENGTH_SHORT).show();
            return;
        }
        enterEditMode();
    }

    private void enterDeleteMode() {
        isDeleteMode = true;
        selectedItems.clear();
        Toast.makeText(this, "Select items to delete", Toast.LENGTH_SHORT).show();
        addButton.setEnabled(false);
        editButton.setEnabled(false);
        removeButton.setImageResource(R.drawable.outline_check_circle_24);

        displayItemAdapter.setDeleteMode(true);
        displayItemAdapter.setSelectedItems(selectedItems);
    }

    private void exitDeleteMode() {
        isDeleteMode = false;
        selectedItems.clear();
        addButton.setEnabled(true);
        editButton.setEnabled(true);
        removeButton.setImageResource(R.drawable.baseline_delete_forever_24);

        displayItemAdapter.setDeleteMode(false);
        displayItemAdapter.setSelectedItems(new ArrayList<>());
    }

    private void loadItems() {
        List<DisplayItem> items = new ArrayList<>();

        if (SOURCE_ADMIN.equals(source)) {
            items.addAll(dbHelper.getAllCategories());
        } else if (SOURCE_ORGANIZER.equals(source)) {
            items.addAll(dbHelper.getEventsByOrganizer(organizerUsername));
        }

        displayItemAdapter.updateItems(items);

        TextView noCategoriesTextView = findViewById(R.id.noTextView);

        if (items.isEmpty()) {
            if (SOURCE_ADMIN.equals(source)) {
                noCategoriesTextView.setText(R.string.no_categories_created);
            } else if (SOURCE_ORGANIZER.equals(source)) {
                noCategoriesTextView.setText(R.string.no_events_created);
            }
            noCategoriesTextView.setVisibility(View.VISIBLE);
        } else {
            noCategoriesTextView.setVisibility(View.GONE);
        }
    }




    private void showItemOptions(DisplayItem item) {
        String itemName = item.getName();
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
        boolean isEvent = SOURCE_ORGANIZER.equals(source);

        if (isEvent) {
            List<Category> categories = dbHelper.getAllCategories();
            if (categories == null || categories.isEmpty()) {
                Toast.makeText(this, "No categories exist. Please create a category first.", Toast.LENGTH_LONG).show();
                return;  // Stop here, do not open dialog
            }
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

            List<Category> categories = dbHelper.getAllCategories();
            List<String> categoryNames = new ArrayList<>();
            for (Category cat : categories) categoryNames.add(cat.getName());
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
                nameInput.setText(event.getName());
                descriptionInput.setText(event.getDescription());
                feeInput.setText(String.valueOf(event.getFee()));
                datetimeInput.setText(event.getDateTime());
            } else {
                Category category = (Category) itemToEdit;
                nameInput.setText(category.getName());
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
                    if (nameExists && (itemToEdit == null || !name.equals(itemToEdit.getName()))) {
                        nameLayout.setError("Event title already exists.");
                        valid = false;
                    }
                } else {
                    boolean nameExists = dbHelper.categoryNameExists(name);
                    if (nameExists && (itemToEdit == null || !name.equals(itemToEdit.getName()))) {
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
                            dbHelper.addEvent(name, description, categoryId, datetime, fee, organizerUsername);
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
        isEditMode = true;
        Toast.makeText(this, "Tap a category to rename", Toast.LENGTH_SHORT).show();
        addButton.setEnabled(false);
        removeButton.setEnabled(false);
        editButton.setEnabled(false);
        displayItemAdapter.setEditMode(isEditMode);
    }

    private void exitEditMode() {
        isEditMode = false;
        addButton.setEnabled(true);
        removeButton.setEnabled(true);
        editButton.setEnabled(true);
        displayItemAdapter.setEditMode(isEditMode);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed() {
        if (isDeleteMode) {
            exitDeleteMode();
            Toast.makeText(this, "Delete mode cancelled", Toast.LENGTH_SHORT).show();
        } else if (isEditMode) {
            exitEditMode();
            Toast.makeText(this, "Edit mode cancelled", Toast.LENGTH_SHORT).show();
        } else {
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
    // --- REST OF DisplayItemActivity (same as what you posted) ---
    // ... You can continue to paste or integrate the rest of DisplayItemActivity here ...
}
