package com.example.local_loop.Category;

import com.example.local_loop.R;
import com.example.local_loop.Helpers.AdapterMode;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    //Inner Classes
    public interface OnCategoryClickListener {
        void onClick(Category category);

        void onLongClick(Category category);

        void onRenameClick(Category category);

        void onModeChanged(AdapterMode newMode);

        void onSelectionChanged(List<Integer> selectedIds);
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        Button categoryButton;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryButton = itemView.findViewById(R.id.categoryButton);
        }
    }

    //Instance Variables
    private AdapterMode currentMode = AdapterMode.NORMAL;
    private List<Category> categories;
    private List<Integer> selectedCategoryIds = new ArrayList<>();
    private final OnCategoryClickListener listener;


    //Constructor
    public CategoryAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.listener = listener;
        if (categories == null) {
            this.categories = new ArrayList<>();
        } else {
            this.categories = categories;
        }
    }

    //Methods

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.categoryButton.setText(category.getName());

        switch (currentMode) {
            case DELETE:
                onDeleteMode(holder, category);
                break;
            case EDIT:
                onEditMode(holder, category);
                break;
            case NORMAL:
            default:
                onNormalMode(holder, category);
                break;
        }
    }

    private void onDeleteMode(CategoryViewHolder holder, Category category) {
        holder.categoryButton.setOnClickListener(v -> {
            int id = category.getID();
            if (selectedCategoryIds.contains(id)) {
                selectedCategoryIds.remove(Integer.valueOf(id));
                v.setBackgroundColor(Color.LTGRAY);
            } else {
                selectedCategoryIds.add(id);
                v.setBackgroundColor(Color.TRANSPARENT); // Highlight
            }
        });

        // Set visual selection state on bind
        if (selectedCategoryIds.contains(category.getID())) {
            holder.categoryButton.setBackgroundColor(Color.TRANSPARENT);
        } else {
            holder.categoryButton.setBackgroundColor(Color.LTGRAY);
        }
        holder.categoryButton.setOnLongClickListener(null);

    }

    private void onEditMode(CategoryViewHolder holder, Category category){
        holder.categoryButton.setOnClickListener(v -> {
            if (listener != null) listener.onRenameClick(category);
        });
        holder.categoryButton.setBackgroundColor(Color.LTGRAY); // always normal
        holder.categoryButton.setOnLongClickListener(null);
    }

    private void onNormalMode(CategoryViewHolder holder, Category category){
        // Default (view mode)
        holder.categoryButton.setOnClickListener(v -> {
            if (listener != null) listener.onClick(category);
        });
        holder.categoryButton.setOnLongClickListener(v -> {
            if (listener != null) listener.onLongClick(category);
            return true;
        });
        holder.categoryButton.setBackgroundColor(Color.TRANSPARENT);
    }


    @SuppressLint("NotifyDataSetChanged")
    public void updateCategories(List<Category> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSelectedCategoryIds(List<Integer> selectedIds) {
        this.selectedCategoryIds = selectedIds;
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return categories.size();
    }
    public void setMode(AdapterMode mode) {
        if (currentMode == mode) {
            Log.d("CategoryAdapter", "You did not change modes");
            return; //No changes
        }
        currentMode = mode;
        if (listener != null) {
            listener.onModeChanged(mode);
        }
        notifyItemRangeChanged(0, getItemCount());
    }

    public AdapterMode getCurrentMode(){
        return this.currentMode;
    }
    @Override
    public void onSelectionChanged(List<Integer> selectedIds) {
        selectedCategoryIds.clear();
        selectedCategoryIds.addAll(selectedIds);
    }
    public List<Integer> getSelectedCategoryIds() {
        return new ArrayList<>(selectedCategoryIds);
    }

}