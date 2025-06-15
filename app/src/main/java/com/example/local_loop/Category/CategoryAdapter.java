package com.example.local_loop.Category;

import com.example.local_loop.R;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onClick(Category category);
        void onLongClick(Category category);
        void onRenameClick(Category category);
    }

    private List<Category> categories;
    private OnCategoryClickListener listener;
    public CategoryAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    private boolean deleteMode = false;
    private List<Integer> selectedIds = new ArrayList<>();

    public void setDeleteMode(boolean enabled) {
        this.deleteMode = enabled;
        notifyDataSetChanged();
    }

    private boolean editMode = false;
    private int selectedCategoryIdForRename = -1;

    public void setEditMode(boolean enabled) {
        this.editMode = enabled;
        notifyDataSetChanged();
    }

    private List<Integer> selectedCategoryIds = new ArrayList<>();

    public void setSelectedCategoryIds(List<Integer> selectedIds){
        this.selectedCategoryIds= selectedIds;
        notifyDataSetChanged();
    }

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

        // Set click listeners
        holder.categoryButton.setOnClickListener(v -> {
            if (listener != null) listener.onClick(category);
        });

        holder.categoryButton.setOnLongClickListener(v -> {
            if (listener != null) listener.onLongClick(category);
            return true;
        });

        holder.categoryButton.setText(category.getName());

        if (deleteMode) {
            // Select to delete
            holder.categoryButton.setOnClickListener(v -> {
                int id = category.getId();
                if (selectedCategoryIds.contains(id)) {
                    selectedCategoryIds.remove(Integer.valueOf(id));
                    v.setBackgroundColor(Color.LTGRAY);
                } else {
                    selectedCategoryIds.add(id);
                    v.setBackgroundColor(Color.TRANSPARENT); // Highlight
                }
            });

            // Set visual selection state on bind
            if (selectedCategoryIds.contains(category.getId())) {
                holder.categoryButton.setBackgroundColor(Color.TRANSPARENT);
            } else {
                holder.categoryButton.setBackgroundColor(Color.LTGRAY);
            }

        } else if (editMode) {
            holder.categoryButton.setOnClickListener(v -> {
                if (listener != null) listener.onRenameClick(category);
            });
            holder.categoryButton.setBackgroundColor(Color.LTGRAY); // always normal
            } else {
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
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void updateCategories(List<Category> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged();
    }

    public int getSelectedCategoryIdForRename() {
        return selectedCategoryIdForRename;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        Button categoryButton;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryButton = itemView.findViewById(R.id.categoryButton);
        }
    }
}