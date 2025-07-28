package com.example.local_loop.Activity;

import static com.example.local_loop.Details.EventDetailsActivity.EXTRA_SOURCE;
import static com.example.local_loop.Details.EventDetailsActivity.SOURCE_ADMIN;
import static com.example.local_loop.Details.EventDetailsActivity.SOURCE_ORGANIZER;
import static com.example.local_loop.Details.EventDetailsActivity.SOURCE_PARTICIPANT;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.UserContent.User;
import com.example.local_loop.Details.CategoryDetailsActivity;
import com.example.local_loop.Details.EventDetailsActivity;
import com.example.local_loop.Helpers.DatabaseHelper;
import com.example.local_loop.Helpers.DisplayItem;
import com.example.local_loop.R;
import com.example.local_loop.UserContent.Category;
import com.example.local_loop.UserContent.Event;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.*;

public class DisplayItemActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private DisplayItemAdapter adapter;

    private boolean isMyEventsMode;
    private String source;
    private String username;

    private EditText searchBar;
    private Spinner categorySpinner;
    private TextView emptyTextView;
    private ImageButton addButton, editButton, removeButton;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isEditMode = false, isDeleteMode = false;
    private final List<DisplayItem> selectedItems = new ArrayList<>();
    private Runnable searchRunnable;
    private static final long SEARCH_DELAY = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_item);

        dbHelper = new DatabaseHelper(this);
        User user = getIntent().getParcelableExtra("user", User.class);
        if (user == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        username = user.getUsername();
        source = getIntent().getStringExtra(EXTRA_SOURCE);
        isMyEventsMode = getIntent().getBooleanExtra("isMyEventsMode", false);

        setupViews();
        setupRecyclerView();
        setupListeners();
        loadItems();
    }

    @SuppressLint("SetTextI18n")
    private void setupViews() {
        searchBar = findViewById(R.id.searchBar);
        categorySpinner = findViewById(R.id.categorySpinner);
        emptyTextView = findViewById(R.id.noEventsUserTextView);
        TextView titleTextView = findViewById(R.id.events);

        addButton = findViewById(R.id.addDisplayButton);
        editButton = findViewById(R.id.editDisplayButton);
        removeButton = findViewById(R.id.removeDisplayButton);

        // Title & empty text per mode
        if (SOURCE_ADMIN.equals(source)) {
            titleTextView.setText("Manage Categories");
            emptyTextView.setText(R.string.no_categories_created);
        } else if (SOURCE_ORGANIZER.equals(source)) {
            titleTextView.setText("Manage Events");
            emptyTextView.setText(R.string.no_events_created);
        } else if (isMyEventsMode) {
            titleTextView.setText(R.string.my_events);
            emptyTextView.setText(R.string.no_events_joined);
        } else {
            titleTextView.setText(R.string.browse_events);
            emptyTextView.setText(R.string.no_events_found);
        }

        // Spinner + SearchBar visibility
        boolean shouldShowSearchAndFilter = !SOURCE_ADMIN.equals(source) && !SOURCE_ORGANIZER.equals(source) && !isMyEventsMode;
        searchBar.setVisibility(shouldShowSearchAndFilter ? View.VISIBLE : View.GONE);
        categorySpinner.setVisibility(shouldShowSearchAndFilter ? View.VISIBLE : View.GONE);

        if (shouldShowSearchAndFilter) {
            setupCategorySpinner();
            setupSearchBar();
        }

        // Show buttons for Admin and Organizer only
        int buttonVisibility = (SOURCE_ADMIN.equals(source) || SOURCE_ORGANIZER.equals(source)) ? View.VISIBLE : View.GONE;
        addButton.setVisibility(buttonVisibility);
        editButton.setVisibility(buttonVisibility);
        removeButton.setVisibility(buttonVisibility);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewEvents);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) recyclerView.getLayoutParams();

        if (SOURCE_ADMIN.equals(source) || SOURCE_ORGANIZER.equals(source)) {
            layoutParams.topMargin = (int) (1 * getResources().getDisplayMetrics().density); // convert 1dp to px
        } else if (isMyEventsMode){
            layoutParams.topMargin = (int) (30 * getResources().getDisplayMetrics().density);
            layoutParams.bottomMargin = (int) (57 * getResources().getDisplayMetrics().density);
        } else {
            layoutParams.topMargin = (int) (55 * getResources().getDisplayMetrics().density); // larger for regular users
        }

        recyclerView.setLayoutParams(layoutParams);

    }



    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new DisplayItemAdapter(new ArrayList<>(),new DisplayItemAdapter.OnItemClickListener() {

            @Override
            public void onClick(DisplayItem item) {
                if (item instanceof Category) {
                    Intent intent = new Intent(DisplayItemActivity.this, CategoryDetailsActivity.class);
                    intent.putExtra(EXTRA_SOURCE, source);
                    intent.putExtra("sourceContext", DisplayItemActivity.class.getSimpleName());
                    intent.putExtra("categoryId", item.getID());
                    intent.putExtra("categoryName", item.getName());
                    intent.putExtra("categoryDescription", item.getDescription());
                    startActivity(intent);
                } else if (item instanceof Event) {
                    Intent intent = new Intent(DisplayItemActivity.this, EventDetailsActivity.class);
                    intent.putExtra(EXTRA_SOURCE, source);
                    intent.putExtra("sourceContext", DisplayItemActivity.class.getSimpleName());
                    intent.putExtra("attendeeId", username);
                    intent.putExtra("eventId", item.getID());
                    intent.putExtra("title", item.getName());
                    intent.putExtra("description", item.getDescription());
                    intent.putExtra("fee", String.valueOf(((Event) item).getFee()));
                    intent.putExtra("datetime", ((Event) item).getDateTime());
                    intent.putExtra("categoryId", ((Event) item).getCategoryId());
                    intent.putExtra("organizer", ((Event) item).getOrganizer());
                    startActivity(intent);
                }
            }

            @Override
            public void onLongClick(DisplayItem item) {
                if (!SOURCE_PARTICIPANT.equals(source)) {
                    AlertDialog dialog = new AlertDialog.Builder(DisplayItemActivity.this)
                            .setTitle("Delete " + item.getName() + "?")
                            .setMessage("Are you sure you want to delete this item?")
                            .setPositiveButton("Delete", (dialogInterface, which) -> {
                                if (item instanceof Category) dbHelper.deleteCategory(item.getID());
                                else if (item instanceof Event) dbHelper.deleteEvent(item.getID());
                                loadItems();
                            })
                            .setNegativeButton("Cancel", null)
                            .create();

                    dialog.setOnShowListener(dialogInterface -> setDialogButtonColors(dialog));
                    dialog.show();
                }
            }

            @Override
            public void onRenameClick(DisplayItem item) {
                if (item instanceof Category) {
                    showItemDialog("Edit Category", item);

                } else if (item instanceof Event) {
                    showItemDialog("Edit Event", item);
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    public static class DisplayItemAdapter extends RecyclerView.Adapter<DisplayItemAdapter.ItemViewHolder> {
        public interface OnItemClickListener {
            void onClick(DisplayItem item);
            void onLongClick(DisplayItem item);
            void onRenameClick(DisplayItem item);
        }

        private List<DisplayItem> items;
        private final DisplayItemAdapter.OnItemClickListener listener;

        private boolean deleteMode = false;
        private boolean editMode = false;

        private List<DisplayItem> selectedItems = new ArrayList<>();

        public DisplayItemAdapter(List<DisplayItem> items, DisplayItemAdapter.OnItemClickListener listener) {
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
        public DisplayItemAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_item, parent, false);
            return new DisplayItemAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DisplayItemAdapter.ItemViewHolder holder, int position) {
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
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) { loadItems(); }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {} });
    }

    private void setupSearchBar() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) handler.removeCallbacks(searchRunnable);
                searchRunnable = DisplayItemActivity.this::loadItems;
                handler.postDelayed(searchRunnable, SEARCH_DELAY);
            }
        });
    }

    private void setupListeners() {
        findViewById(R.id.browseEventBackButton).setOnClickListener(v -> onBackPressed());

        if (SOURCE_ADMIN.equals(source)) {
            addButton.setOnClickListener(v -> showItemDialog("Add Category", null));
            removeButton.setOnClickListener(v -> handleRemoveButton());
            editButton.setOnClickListener(v -> handleEditButton());
        } else if (SOURCE_ORGANIZER.equals(source)) {
            addButton.setOnClickListener(v -> showItemDialog("Add Event", null));
            removeButton.setOnClickListener(v -> handleRemoveButton());
            editButton.setOnClickListener(v -> handleEditButton());
        }
    }

    private void loadItems() {
        List<DisplayItem> items = new ArrayList<>();

        if (SOURCE_ADMIN.equals(source)) {
            items.addAll(dbHelper.getAllCategories());
        } else if (SOURCE_ORGANIZER.equals(source)) {
            items.addAll(dbHelper.getEventsByOrganizer(username));
        } else if (isMyEventsMode) {
            items.addAll(dbHelper.getEventsUserRequested(username));
        } else {
            String query = searchBar.getText().toString().trim();
            String category = categorySpinner.getSelectedItem().toString();
            if ("All".equals(category)) category = "";

            List<Event> events = dbHelper.searchEvents(query, category);
            List<Event> joined = dbHelper.getEventsUserRequested(username);

            Set<Integer> joinedIds = new HashSet<>();
            for (Event e : joined) joinedIds.add(e.getID());
            for (Event e : events) if (!joinedIds.contains(e.getID())) items.add(e);
        }

        System.out.println("Loaded " + items.size() + " items for source: " + source);

        emptyTextView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.updateItems(items);
    }

    private void handleEditButton() {
        if (adapter.getItemCount() == 0) {
            Toast.makeText(this, "No items to edit", Toast.LENGTH_SHORT).show();
            return;
        }
        toggleEditMode(true);
    }

    private void handleRemoveButton() {
        if (adapter.getItemCount() == 0) {
            Toast.makeText(this, "No items to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isDeleteMode) {
            toggleDeleteMode(true);
        } else {
            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "No items selected", Toast.LENGTH_SHORT).show();
                return;
            }

            StringBuilder names = new StringBuilder();
            for (DisplayItem item : selectedItems) {
                names.append("- ").append(item.getName()).append("\n");
            }

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete the following items?\n\n" + names)
                    .setPositiveButton("Delete", (d, which) -> {
                        for (DisplayItem item : selectedItems) {
                            if (item instanceof Category) dbHelper.deleteCategory(item.getID());
                            else if (item instanceof Event) dbHelper.deleteEvent(item.getID());
                        }
                        toggleDeleteMode(false);
                        loadItems();
                    })
                    .setNegativeButton("Cancel", (d, which) -> toggleDeleteMode(false))
                    .create();

            dialog.setOnShowListener(dialogInterface -> setDialogButtonColors(dialog));

            dialog.show();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void toggleDeleteMode(boolean enable) {
        isDeleteMode = enable;
        selectedItems.clear();

        addButton.setEnabled(!enable);
        editButton.setEnabled(!enable);
        removeButton.setImageResource(
                enable ? R.drawable.outline_check_circle_24 : R.drawable.baseline_delete_forever_24
        );

        adapter.setDeleteMode(enable);
        adapter.setSelectedItems(enable ? selectedItems : new ArrayList<>());
        adapter.notifyDataSetChanged();

        if (enable) {
            Toast.makeText(this, "Select items to delete", Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private void toggleEditMode(boolean enable) {
        isEditMode = enable;
        addButton.setEnabled(!enable);
        removeButton.setEnabled(!enable);
        editButton.setEnabled(!enable);
        adapter.setEditMode(enable);
        adapter.setDeleteMode(false);
        selectedItems.clear();
        adapter.setSelectedItems(new ArrayList<>());
        adapter.notifyDataSetChanged();
    }






    @Override
    protected void onResume() {
        super.onResume();
        loadItems();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) loadItems();
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
        dialogTitle.setPadding(40, 80, 40, 10);
        dialogTitle.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
        dialogTitle.setGravity(Gravity.CENTER);
        return dialogTitle;
    }

    private void setDialogButtonColors(AlertDialog dialog) {
        Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        if (positive != null)
            positive.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
        if (negative != null)
            negative.setTextColor(Color.BLACK);
    }

    private void showItemDialog(@NonNull String dialogTitle, @Nullable DisplayItem existingItem) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_display_item, null);

        LinearLayout categorySection = dialogView.findViewById(R.id.categorySection);
        LinearLayout eventSection = dialogView.findViewById(R.id.eventSection);

        if (source.equals(SOURCE_ADMIN)) {
            categorySection.setVisibility(View.VISIBLE);
            eventSection.setVisibility(View.GONE);
            EditText nameInput = dialogView.findViewById(R.id.categoryNameInput);
            EditText descInput = dialogView.findViewById(R.id.categoryDescriptionInput);

            if (existingItem != null) {
                nameInput.setText(existingItem.getName());
                descInput.setText(existingItem.getDescription());
            }

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setCustomTitle(createDialogTitle(dialogTitle))
                    .setView(dialogView)
                    .setPositiveButton(existingItem == null ? "Create" : "Update", null)
                    .setNegativeButton("Cancel", null)
                    .create();

            dialog.setOnShowListener(dialogInterface -> {
                setDialogButtonColors(dialog);
                Button confirmButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                confirmButton.setOnClickListener(v -> {
                    String name = nameInput.getText().toString().trim();
                    String desc = descInput.getText().toString().trim();

                    if (name.isEmpty() || desc.isEmpty()) {
                        Toast.makeText(this, "All Fields Required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (existingItem == null && dbHelper.categoryNameExists(name)){
                        Toast.makeText(this, "This Category Field Already Exists", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (existingItem == null) {
                        dbHelper.addCategory(name, desc);
                    } else {
                        dbHelper.renameCategory(existingItem.getID(), name, desc);
                    }

                    loadItems();
                    dialog.dismiss();
                    toggleEditMode(false);
                });

                cancelButton.setOnClickListener(v -> {
                    dialog.dismiss();
                    toggleEditMode(false);
                });
            });

            dialog.show();
        }
        else {
            categorySection.setVisibility(View.GONE);
            eventSection.setVisibility(View.VISIBLE);
            EditText titleInput = dialogView.findViewById(R.id.eventTitleInput);
            EditText descInput = dialogView.findViewById(R.id.eventDescriptionInput);
            EditText feeInput = dialogView.findViewById(R.id.eventFeeInput);
            TextView dateTimeInput = dialogView.findViewById(R.id.eventDateTimeInput);
            Spinner categorySpinner = dialogView.findViewById(R.id.eventCategorySpinner);

            List<Category> categories = dbHelper.getAllCategories();
            List<String> categoryNames = new ArrayList<>();
            for (Category c : categories) categoryNames.add(c.getName());

            if (categories.isEmpty()) {
                Toast.makeText(this, "No categories available. Please add one first.", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(spinnerAdapter);

            if (existingItem != null) {
                titleInput.setText(existingItem.getName());
                descInput.setText(existingItem.getDescription());
                feeInput.setText(String.valueOf(((Event) existingItem).getFee()));
                dateTimeInput.setText(((Event) existingItem).getDateTime());

                for (int i = 0; i < categories.size(); i++) {
                    if (categories.get(i).getID() == ((Event) existingItem).getCategoryId()) {
                        categorySpinner.setSelection(i);
                        break;
                    }
                }
            }

            dateTimeInput.setOnClickListener(v -> showDateTimePicker((TextInputEditText) dateTimeInput));

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setCustomTitle(createDialogTitle(dialogTitle))
                    .setView(dialogView)
                    .setPositiveButton(existingItem == null ? "Create" : "Update", null)
                    .setNegativeButton("Cancel", null)
                    .create();

            dialog.setOnShowListener(dialogInterface -> {
                setDialogButtonColors(dialog);
                Button confirmButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                confirmButton.setOnClickListener(v -> {
                    String title = titleInput.getText().toString().trim();
                    String desc = descInput.getText().toString().trim();
                    String feeStr = feeInput.getText().toString().trim();
                    String datetime = dateTimeInput.getText().toString().trim();

                    if (title.isEmpty() || desc.isEmpty() || feeStr.isEmpty() || datetime.isEmpty()) {
                        Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (existingItem == null && dbHelper.eventTitleExists(title)){
                        Toast.makeText(this, "Event Title Already Exists", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int categoryId = categories.get(categorySpinner.getSelectedItemPosition()).getID();
                    double fee = Double.parseDouble(feeStr);

                    if (existingItem == null) {
                        dbHelper.addEvent(title, desc, categoryId, datetime, fee, username);
                    } else {
                        dbHelper.updateEvent(existingItem.getID(), title, desc, fee, datetime, categoryId);
                    }

                    loadItems();
                    dialog.dismiss();
                    toggleEditMode(false);
                });

                cancelButton.setOnClickListener(v -> {
                    dialog.dismiss();
                    toggleEditMode(false);
                });
            });

            dialog.show();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed() {
        if (isDeleteMode) {
            toggleDeleteMode(false);
            Toast.makeText(this, "Delete mode cancelled", Toast.LENGTH_SHORT).show();
        } else if (isEditMode) {
            toggleEditMode(false);
            Toast.makeText(this, "Edit mode cancelled", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }
}
