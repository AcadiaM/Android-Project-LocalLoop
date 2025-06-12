package com.example.local_loop.userClasses;

import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Space;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.R;
import com.example.local_loop.database.DBCategoryHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private DBCategoryHelper dbHelper;
    private CategoryAdapter categoryAdapter;

    private ImageButton addCategoryButton, removeCategoryButton;

    private boolean isDeleteMode = false;
    private final List<Integer> selectedCategoryIds = new ArrayList<>();

    private ImageButton editCategoryButton;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        dbHelper = new DBCategoryHelper(this);

        addCategoryButton = findViewById(R.id.addCategoryButton);
        addCategoryButton.setOnClickListener(v -> showAddCategoryDialog());

        RecyclerView categoryRecyclerView = findViewById(R.id.categoryRecyclerView);

        // Set layout manager with 3 columns for grid
        categoryRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Initialize adapter with empty list and listener
        categoryAdapter = new CategoryAdapter(new java.util.ArrayList<>(), new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onClick(Category category) {
                Intent intent = new Intent(CategoryActivity.this, EventCategoryActivity.class);
                intent.putExtra("categoryId", category.getId());
                intent.putExtra("categoryName", category.getName());
                intent.putExtra("categoryDescription", category.getDescription());
                startActivity(intent);
            }

            @Override
            public void onLongClick(Category category) {
                showCategoryOptions(category);
            }
            @Override
            public void onRenameClick(Category category){
                showRenameCategoryDialog(category);
                exitEditMode();
            }
        });

        categoryRecyclerView.setAdapter(categoryAdapter);
        loadCategories();

        removeCategoryButton = findViewById(R.id.removeCategoryButton);
        removeCategoryButton.setOnClickListener(v -> {
            if (!isDeleteMode) {
                isDeleteMode = true;
                selectedCategoryIds.clear();

                Toast.makeText(this, "Select categories to delete", Toast.LENGTH_SHORT).show();

                addCategoryButton.setEnabled(false);
                editCategoryButton.setEnabled(false);

                removeCategoryButton.setImageResource(R.drawable.outline_check_circle_24);

                categoryAdapter.setDeleteMode(true);
                categoryAdapter.setSelectedCategoryIds(selectedCategoryIds);

                } else {
                for (int categoryId : selectedCategoryIds) {
                    dbHelper.deleteCategory(categoryId);
                }

                Toast.makeText(this, "Deleted " + selectedCategoryIds.size() + " categories", Toast.LENGTH_SHORT).show();

                isDeleteMode = false;
                selectedCategoryIds.clear();

                addCategoryButton.setEnabled(true);
                editCategoryButton.setEnabled(true);

                removeCategoryButton.setImageResource(R.drawable.baseline_delete_forever_24);

                categoryAdapter.setDeleteMode(false);
                categoryAdapter.setSelectedCategoryIds(new ArrayList<>());

                loadCategories();
            }
        });

        editCategoryButton = findViewById(R.id.editCategoryButton);

        editCategoryButton.setOnClickListener(v -> {
            if (!isEditMode) {
                isEditMode = true;
                Toast.makeText(this, "Tap a category to rename", Toast.LENGTH_SHORT).show();
                addCategoryButton.setEnabled(false);
                removeCategoryButton.setEnabled(false);
                editCategoryButton.setEnabled(false);
                categoryAdapter.setEditMode(true);
            }
        });
    }

    private void loadCategories() {
        List<Category> categories = dbHelper.getAllCategories();
        categoryAdapter.updateCategories(categories);
    }

    private void showCategoryOptions(Category category) {
        String[] options = {"Rename", "Delete"};
        new AlertDialog.Builder(this)
                .setTitle(category.getName())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showRenameCategoryDialog(category);
                    } else if (which == 1) {
                        dbHelper.deleteCategory(category.getId());
                        loadCategories();
                    }
                })
                .show();
    }

    private void showRenameCategoryDialog(Category category) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_category_item, null);

        TextInputLayout nameLayout = dialogView.findViewById(R.id.categoryNameLayout);
        TextInputLayout descriptionLayout = dialogView.findViewById(R.id.categoryDescriptionLayout);
        TextInputEditText nameInput = dialogView.findViewById(R.id.categoryNameInput);
        TextInputEditText descriptionInput = dialogView.findViewById(R.id.categoryDescriptionInput);

        // Set initial values
        nameInput.setText(category.getName());
        descriptionInput.setText(category.getDescription());

        TextView title = new TextView(this);
        title.setText("Edit Category");
        title.setPadding(40, 40, 40, 40);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(ContextCompat.getColor(this, R.color.holo_dark_blue));
        title.setTextSize(20f);
        title.setTypeface(null, Typeface.BOLD);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setCustomTitle(title)
                .setView(dialogView)
                .setPositiveButton("Save", null)   // We'll override later
                .setNegativeButton("Cancel", (d, which) -> {
                    exitEditMode();
                    d.dismiss();
                })
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            // Buttons
            Button saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

            // Set button colors
            saveButton.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            cancelButton.setTextColor(getResources().getColor(android.R.color.darker_gray));

            // Override save button click to prevent dismiss if invalid input
            saveButton.setOnClickListener(v -> {
                String newName = nameInput.getText().toString().trim();
                String newDescription = descriptionInput.getText().toString().trim();

                boolean valid = true;

                if (newName.isEmpty()) {
                    nameLayout.setError("Required");
                    valid = false;
                } else {
                    nameLayout.setError(null);
                }

                if (newDescription.isEmpty()) {
                    descriptionLayout.setError("Required");
                    valid = false;
                } else {
                    descriptionLayout.setError(null);
                }

                if (valid) {
                    dbHelper.renameCategory(category.getId(), newName, newDescription);
                    loadCategories();
                    exitEditMode();
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    private void showAddCategoryDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_category_item, null);

        TextInputLayout nameLayout = dialogView.findViewById(R.id.categoryNameLayout);
        TextInputLayout descriptionLayout = dialogView.findViewById(R.id.categoryDescriptionLayout);
        TextInputEditText nameInput = dialogView.findViewById(R.id.categoryNameInput);
        TextInputEditText descriptionInput = dialogView.findViewById(R.id.categoryDescriptionInput);

        TextView title = new TextView(this);
        title.setText("Edit Category");
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

            // Button colors
            addButton.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            cancelButton.setTextColor(getResources().getColor(android.R.color.darker_gray));

            addButton.setOnClickListener(v -> {
                String name = nameInput.getText().toString().trim();
                String description = descriptionInput.getText().toString().trim();

                boolean valid = true;

                // Reset errors
                nameLayout.setError(null);
                descriptionLayout.setError(null);

                if (name.isEmpty()) {
                    nameLayout.setError("Required");
                    valid = false;
                } else if (dbHelper.categoryNameExists(name)) {
                    nameLayout.setError("Category name already exists");
                    valid = false;
                }

                if (description.isEmpty()) {
                    descriptionLayout.setError("Required");
                    valid = false;
                }

                if (valid) {
                    dbHelper.addCategory(name, description);
                    loadCategories();
                    Toast.makeText(this, "Category added", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    @Deprecated
    public void onBackPressed() {
        if (isDeleteMode) {
            isDeleteMode = false;
            selectedCategoryIds.clear();
            categoryAdapter.setDeleteMode(false);
            removeCategoryButton.setImageResource(R.drawable.baseline_delete_forever_24);
            addCategoryButton.setEnabled(true);
            Toast.makeText(this, "Delete mode cancelled", Toast.LENGTH_SHORT).show();
        } else if (isEditMode) {
            exitEditMode();
        } else{
            super.onBackPressed();
        }
    }

    private void exitEditMode() {
        isEditMode = false;
        addCategoryButton.setEnabled(true);
        removeCategoryButton.setEnabled(true);
        editCategoryButton.setEnabled(true);
        categoryAdapter.setEditMode(false);
    }
}