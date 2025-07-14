package com.example.local_loop.Event;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.local_loop.Category.Category;
import com.example.local_loop.R;
import com.example.local_loop.database.DatabaseHelper;

public class EventDetailsActivity extends AppCompatActivity {

    DatabaseHelper dBHelper;

    public static final String EXTRA_SOURCE = "source";
    public static final String SOURCE_ADMIN = "admin_page";
    public static final String SOURCE_ORGANIZER = "organizer_page";
    public static final String SOURCE_PARTICIPANT = "participant_page";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        dBHelper = new DatabaseHelper(this);

        TextView titleText = findViewById(R.id.eventDetailTitle);
        TextView descriptionText = findViewById(R.id.eventDetailDescription);
        TextView feeText = findViewById(R.id.eventDetailFee);
        TextView dateTimeText = findViewById(R.id.eventDetailDateTime);
        TextView contextInfoText = findViewById(R.id.eventDetailContextInfo);

        Button joinButton = findViewById(R.id.joinButton);

        // Get data passed from intent
        int eventID = getIntent().getIntExtra("eventId",-1);
        String attendeeID = getIntent().getStringExtra("attendeeId");

        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String fee = getIntent().getStringExtra("fee");
        String datetime = getIntent().getStringExtra("datetime");

        String organizer = getIntent().getStringExtra("organizer");
        int categoryID = getIntent().getIntExtra("categoryId",-1);
        Category category = dBHelper.getCategoryById(categoryID);
        String categoryName = category.getName();

        // Optional: context-dependent values
        String source = getIntent().getStringExtra(EXTRA_SOURCE);

        // Set values
        titleText.setText(title);
        descriptionText.setText(description);
        feeText.setText("Fee: $" + fee);
        dateTimeText.setText("Date/Time: " + datetime);

        // Set context-based info
        if (SOURCE_ADMIN.equals(source)) {
            contextInfoText.setText("Organizer: " + organizer);
            joinButton.setEnabled(false);
        } else if (SOURCE_ORGANIZER.equals(source)) {
            contextInfoText.setText("Category: " + categoryName);
            joinButton.setEnabled(false);
        } else {
            contextInfoText.setText("Category: " + categoryName + "\n" + "\n" + "Organizer: " + organizer);  // Or "Unknown Source"
            joinButton.setVisibility(View.VISIBLE);
            if (dBHelper.hasJoinRequest(eventID, attendeeID)){
                joinButton.setEnabled(false);
                joinButton.setText("Joined");
            }
            else{
                joinButton.setOnClickListener(v -> {
                            joinButton.setEnabled(false);
                            joinButton.setText("Joined");
                            dBHelper.submitJoinRequest(eventID, attendeeID);
                });
            }
        }
    }
    public boolean isJoined(int ID, String participant){
      return true;
    }
}
