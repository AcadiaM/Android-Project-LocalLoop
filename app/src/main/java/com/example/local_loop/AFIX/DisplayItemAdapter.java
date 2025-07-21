package com.example.local_loop.AFIX;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.Helpers.DisplayItem;
import com.example.local_loop.R;

import java.util.ArrayList;
import java.util.List;

public class DisplayItemAdapter extends RecyclerView.Adapter<DisplayItemAdapter.ItemViewHolder> {

    public interface OnItemClickListener {
        void onClick(DisplayItem item);
        void onLongClick(DisplayItem item);
        void onRenameClick(DisplayItem item);
    }

    private List<DisplayItem> items;
    private final OnItemClickListener listener;

    public DisplayItemAdapter(List<DisplayItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    private boolean deleteMode = false;
    private boolean editMode = false;

    private List<DisplayItem> selectedItems = new ArrayList<>();

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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item, parent, false);  // same layout for simplicity
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

            holder.button.setBackgroundColor(selectedItems.contains(item)
                    ? Color.TRANSPARENT : Color.LTGRAY);

        } else if (editMode) {
            holder.button.setOnClickListener(v -> {
                if (listener != null) listener.onRenameClick(item);
            });

            holder.button.setBackgroundColor(Color.LTGRAY);

        } else {  // Normal Mode: open details on click
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
            button = itemView.findViewById(R.id.categoryButton);  // reuse category_button.xml
        }
    }
}