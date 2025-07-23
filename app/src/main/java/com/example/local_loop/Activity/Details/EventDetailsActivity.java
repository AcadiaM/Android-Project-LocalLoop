package com.example.local_loop.Activity.Details;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.example.local_loop.Activity.Listings.UserDisplayActivity;
import com.example.local_loop.Helper.DatabaseHelper;
import com.example.local_loop.Helper.ViewMode;
import com.example.local_loop.Models.Account;
import com.example.local_loop.Models.Event;
import com.example.local_loop.R;
import com.example.local_loop.Models.Category;

import java.util.Objects;

public class EventDetailsActivity extends AppCompatActivity {
    DatabaseHelper dBHelper;
    private ViewMode mode;
    private Account session;
    Event event;

    @SuppressLint("SetTextI18n")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        dBHelper = new DatabaseHelper(this);
        session = getIntent().getParcelableExtra("user", Account.class);
        mode = ViewMode.valueOf(getIntent().getStringExtra(ViewMode.VIEW.name()));
        Bundle itemBundle = getIntent().getBundleExtra("event");

        if (session == null || itemBundle==null) {
            Log.d("USER_EVENT_A","Session or bundle is null");
            finish();
            return;
        }

        event = Event.fromBundle(itemBundle);

        TextView titleText = findViewById(R.id.eventDetailTitle);
        TextView descriptionText = findViewById(R.id.eventDetailDescription);
        TextView feeText = findViewById(R.id.eventDetailFee);
        TextView dateTimeText = findViewById(R.id.eventDetailDateTime);
        TextView contextInfoText = findViewById(R.id.eventDetailContextInfo);

        Button joinButton = findViewById(R.id.joinButton);

        // Get data passed from intent

        Category category = dBHelper.getCategory(event.getCategoryId());
        String categoryName = category.getTitle();
        String organizer = dBHelper.getUsername(event.getOrganizer());

        // Set values
        titleText.setText(event.getTitle());
        descriptionText.setText(event.getDescription());
        feeText.setText("Fee: $" + event.getFee());
        dateTimeText.setText("Date/Time: " + event.getDateTime());

        // Set context-based info
        switch (mode){
            case ADMIN_EVENTS:
                contextInfoText.setText("Organizer: " + organizer);
                joinButton.setEnabled(false);
                joinButton.setVisibility(View.INVISIBLE);
                break;
            case ORG_EVENTS:
                contextInfoText.setText("Category: " + categoryName);
                joinButton.setText("Attendee List");
                joinButton.setOnClickListener(v -> {
                    Intent intent = new Intent(getApplicationContext(), UserDisplayActivity.class);
                    intent.putExtra(ViewMode.VIEW.name(), mode.name());
                    intent.putExtra("user", session);
                    startActivity(intent);
                });
                break;
            default:
                contextInfoText.setText("Category: " + categoryName + "\n" + "\n" + "Organizer: " + organizer);
                if (dBHelper.hasRequest(event.getID(), session.getUserID())) {
                    joinButton.setEnabled(false);
                    joinButton.setText(dBHelper.getStatus(event.getID(), session.getUserID()));
                    if (Objects.equals(dBHelper.getStatus(event.getID(), session.getUserID()), "Approved")) {
                        joinButton.setBackgroundColor(getColor(R.color.dark_green));
                        joinButton.setTextColor(Color.WHITE);
                    } else if (Objects.equals(dBHelper.getStatus(event.getID(), session.getUserID()), "Refused")) {
                        joinButton.setBackgroundColor(getColor(R.color.red));
                        joinButton.setTextColor(Color.WHITE);
                    } else {
                        joinButton.setBackgroundColor(Color.LTGRAY);
                    }
                }else {
                    joinButton.setText("Join");
                    joinButton.setOnClickListener(v -> {
                        dBHelper.submitRequest(event.getID(), session.getUserID());
                        joinButton.setEnabled(false);
                        joinButton.setBackgroundColor(Color.LTGRAY);
                        joinButton.setText(dBHelper.getStatus(event.getID(), session.getUserID()));
                    });
                }
                break;
        }

        Button backButton = findViewById(R.id.eventDetailsBackButton);
        backButton.setOnClickListener(v -> onBackPressed());
    }
}