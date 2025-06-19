package com.example.local_loop.Category;

import static com.example.local_loop.Event.EventDetailsActivity.EXTRA_SOURCE;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.Event.Event;
import com.example.local_loop.Event.EventAdapter;
import com.example.local_loop.R;
import com.example.local_loop.database.DatabaseHelper;

import java.util.List;

public class CategoryDetailsActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);

        TextView categoryTitleText = findViewById(R.id.categoryTitleText);
        TextView categoryDescriptionText = findViewById(R.id.categoryDescriptionText);
        RecyclerView eventRecyclerView = findViewById(R.id.eventRecyclerView);

        dbHelper = new DatabaseHelper(this);

        // Get category ID passed via intent
        int categoryId = getIntent().getIntExtra("categoryId", -1);
        if (categoryId == -1) {
            Toast.makeText(this, "Invalid category", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load and display category details
        Category category = dbHelper.getCategoryById(categoryId);
        if (category != null) {
            categoryTitleText.setText(category.getName());
            categoryDescriptionText.setText(category.getDescription());
        }

        // Load and display events in that category
        List<Event> events = dbHelper.getEventsByCategoryId(categoryId);
        eventRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        EventAdapter eventAdapter = new EventAdapter(getIntent().getStringExtra(EXTRA_SOURCE), events, this);
        eventRecyclerView.setAdapter(eventAdapter);
    }
}
