package com.example.local_loop.userClasses;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.R;
import com.example.local_loop.database.DBCategoryHelper;

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
        android.widget.EditText input = new android.widget.EditText(this);
        input.setText(category.getName());
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT);

        new AlertDialog.Builder(this)
                .setTitle("Rename Category")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        dbHelper.renameCategory(category.getId(), newName);
                        loadCategories();
                        exitEditMode();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Cancel renaming, still exit edit mode
                    exitEditMode();
                })
                .show();
    }
    private void showAddCategoryDialog() {
        android.widget.EditText input = new android.widget.EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT);

        new AlertDialog.Builder(this)
                .setTitle("Add New Category")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        dbHelper.addCategory(name);
                        loadCategories();
                        Toast.makeText(this, "Category added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Category name cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
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