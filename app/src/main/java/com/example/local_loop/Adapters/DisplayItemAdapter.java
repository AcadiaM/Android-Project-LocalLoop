package com.example.local_loop.Adapters;

import com.example.local_loop.Helpers.DisplayItem;
import com.example.local_loop.Helpers.MODE;
import com.example.local_loop.R;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DisplayItemAdapter extends RecyclerView.Adapter<DisplayItemAdapter.ItemViewHolder> {

    public interface OnItemClickListener {
        void onClick(DisplayItem item);
        void onLongClick(DisplayItem item);
        void onRenameClick(DisplayItem item);
        void onModeChanged(MODE mode);
    }


    private List<DisplayItem> items;
    private final OnItemClickListener listener;
    private MODE mode = MODE.DEFAULT;

    public DisplayItemAdapter(List<DisplayItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }
    private List<DisplayItem> selectedItems = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void setSelectedItems(List<DisplayItem> selectedItems) {
        this.selectedItems = selectedItems;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setMode(MODE mode) {
        this.mode = mode;
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

        switch (mode) {
            case DELETE:
                onDeleteMode(holder,item);
                break;
            case EDIT:
                onEditMode(holder,item);
                break;
            default:
                onNormalMode(holder,item);
                break;
        }
    }

    private void onDeleteMode(@NonNull ItemViewHolder holder,DisplayItem item) {
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
    }

    private void onEditMode(@NonNull ItemViewHolder holder,DisplayItem item){
        holder.button.setOnClickListener(v -> {
            if (listener != null) listener.onRenameClick(item);
        });

        holder.button.setBackgroundColor(Color.LTGRAY);

    }

    private void onNormalMode(@NonNull ItemViewHolder holder,DisplayItem item){
        // Normal Mode: open details on click
        holder.button.setOnClickListener(v -> {
            if (listener != null) listener.onClick(item);
        });

        holder.button.setBackgroundColor(Color.TRANSPARENT);
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
