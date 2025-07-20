package com.example.local_loop.TEMP;

//import static com.example.local_loop.Event.EventDetailsActivity.EXTRA_SOURCE;

import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.Account.*;
import com.example.local_loop.Activity.WelcomeUser.*;
import com.example.local_loop.Category.Category;
import com.example.local_loop.Category.CategoryAdapter;
import com.example.local_loop.Helpers.*;

import com.example.local_loop.R;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategoryActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private Account user;
    InputValidation validation;

    RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private ImageButton addCategoryButton, removeCategoryButton, editCategoryButton;

    private final List<Integer> selectedCategoryIds = new ArrayList<>();

    //Dialog Values
    private TextInputLayout nameLayout, descriptionLayout;
    private TextInputEditText nameInput,  descriptionInput;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        dbHelper = new DatabaseHelper(this);
        validation = new InputValidation(this, dbHelper);

        //Initialize values
        initValues();
        setupRecycler();
        loadCategories();
    }

    private void initValues(){
        //views
        addCategoryButton = findViewById(R.id.addCategoryButton);
        categoryRecyclerView = findViewById(R.id.CategoryListing);
        removeCategoryButton = findViewById(R.id.removeCategoryButton);
        editCategoryButton = findViewById(R.id.editCategoryButton);

        //listeners
        addCategoryButton.setOnClickListener(v -> showAddCategoryDialog());
        editCategoryButton.setOnClickListener(v -> editMode());

        removeCategoryButton.setOnClickListener(v -> {
            if (categoryAdapter.getItemCount() == 0) {
                Toast.makeText(this, "No categories to delete", Toast.LENGTH_SHORT).show();
                return;
            }
            deleteMode();
        });
    }

    private void setupRecycler() {
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onClick(Category category) {
                Intent intent = new Intent(CategoryActivity.this, AdminWelcomePage.class); //ToDO fix next page
                intent.putExtra("category", category);
                startActivity(intent);
            }
            @Override
            public void onLongClick(Category category) {
                showCategoryOptions(category);
            }
            @Override
            public void onRenameClick(Category category) {
                showRenameCategoryDialog(category);
                exitEditMode();
            }
        });
        categoryRecyclerView.setAdapter(categoryAdapter);
    }

    private void deleteMode(){

        //Entering Delete Mode
        if(categoryAdapter.getCurrentMode() != AdapterMode.DELETE){
            //Reset vals
            selectedCategoryIds.clear();
            addCategoryButton.setEnabled(false);
            editCategoryButton.setEnabled(false);
            removeCategoryButton.setImageResource(R.drawable.outline_check_circle_24);

            //start delete mode
            Toast.makeText(this, "Select categories to delete", Toast.LENGTH_SHORT).show();
            categoryAdapter.setSelectedCategoryIds(selectedCategoryIds);
            categoryAdapter.setMode( AdapterMode.DELETE);
        }

        else if(selectedCategoryIds.isEmpty()){
            Toast.makeText(this, "No categories selected to delete", Toast.LENGTH_SHORT).show();
            normalMode();
        }

        else{
            for (int categoryId : selectedCategoryIds) {
                dbHelper.deleteCategory(categoryId);
            }
            Toast.makeText(this, "Deleted " + selectedCategoryIds.size() + " categories", Toast.LENGTH_SHORT).show();
            selectedCategoryIds.clear();
            categoryAdapter.setMode( AdapterMode.NORMAL);
            addCategoryButton.setEnabled(true);
            editCategoryButton.setEnabled(true);
            removeCategoryButton.setImageResource(R.drawable.baseline_delete_forever_24);
            categoryAdapter.setSelectedCategoryIds(new ArrayList<>());
            normalMode();
            loadCategories();
        }

    }

    private void loadCategories() {
        List<Category> categories = dbHelper.getAllCategories();
        categoryAdapter.updateCategories(categories);
    }

    private void onEditCategories(View view){
        if (categoryAdapter.getItemCount() == 0){
            Toast.makeText(this,"No categories to edit", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Tap a category to rename", Toast.LENGTH_SHORT).show();
        addCategoryButton.setEnabled(false);
        removeCategoryButton.setEnabled(false);
        editCategoryButton.setEnabled(false);
        categoryAdapter.setMode(AdapterMode.EDIT);
    }


    private void showCategoryOptions(Category category) {
        String[] options = {"Rename", "Delete"};
        new AlertDialog.Builder(this)
                .setTitle(category.getName())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showRenameCategoryDialog(category);
                    } else if (which == 1) {
                        dbHelper.deleteCategory(category.getID());
                        loadCategories();
                    }
                })
                .show();
    }

    private void initCategoryDialog(View dialogView){
        nameLayout = dialogView.findViewById(R.id.categoryNameLayout);
        descriptionLayout = dialogView.findViewById(R.id.categoryDescriptionLayout);
        nameInput = dialogView.findViewById(R.id.categoryNameInput);
        descriptionInput = dialogView.findViewById(R.id.categoryDescriptionInput);

    }

    private void showRenameCategoryDialog(Category category) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_category_item, null);
        initCategoryDialog(dialogView);

        // Set initial values
        nameInput.setText(category.getName());
        descriptionInput.setText(category.getDescription());

        TextView title = new TextView(this);
        title.setText(getString(R.string.edit_category));
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
                    normalMode();
                    d.dismiss();
                })
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            // Buttons
            Button saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

            // Set button colors
            saveButton.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
            cancelButton.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));

            // Override save button click to prevent dismiss if invalid input
            saveButton.setOnClickListener(v -> {


                String newName = Objects.requireNonNull(nameInput.getText()).toString().trim();
                String newDescription = Objects.requireNonNull(descriptionInput.getText()).toString().trim();

                boolean valid = validation.validateLayout(newName,nameLayout) &  validation.validateLayout(newDescription,descriptionLayout);

                if(valid){
                    dbHelper.renameCategory(category.getID(), newName, newDescription);
                    loadCategories();
                    normalMode();
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    private void showAddCategoryDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_category_item, null);
        initCategoryDialog();

        TextView title = new TextView(this);
        title.setText(getString(R.string.add_category));
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
            addButton.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
            cancelButton.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));

            addButton.setOnClickListener(v -> {
                String name = Objects.requireNonNull(nameInput.getText()).toString().trim();
                String description = Objects.requireNonNull(descriptionInput.getText()).toString().trim();

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

    @SuppressWarnings("deprecation")
    //@Override
    public void onBackPressed(View view) {
        switch(categoryAdapter.getCurrentMode()){
            case DELETE:
                selectedCategoryIds.clear();
                categoryAdapter.setMode(AdapterMode.NORMAL);
                removeCategoryButton.setImageResource(R.drawable.baseline_delete_forever_24);
                addCategoryButton.setEnabled(true);
                Toast.makeText(this, "Delete mode cancelled", Toast.LENGTH_SHORT).show();
            default:
                normalMode();

        }
         else{
            Intent intent = new Intent(CategoryActivity.this, AdminWelcomePage.class);
            User userName;
            intent.putExtra("username", getIntent().getStringExtra("username"));
            intent.putExtra("userType", getIntent().getStringExtra("userType"));
            startActivity(intent);
        }
    }

    private void normalMode() {
        addCategoryButton.setEnabled(true);
        removeCategoryButton.setEnabled(true);
        editCategoryButton.setEnabled(true);
        categoryAdapter.setMode(AdapterMode.NORMAL);
    }
}