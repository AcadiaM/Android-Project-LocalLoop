package com.example.local_loop.Details;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.local_loop.Activity.UserDisplayActivity;
import com.example.local_loop.Helpers.DatabaseHelper;
import com.example.local_loop.R;
import com.example.local_loop.UserContent.Category;

import java.util.Objects;

public class EventDetailsActivity extends AppCompatActivity {

    DatabaseHelper dBHelper;

    public static final String EXTRA_SOURCE = "source";
    public static final String SOURCE_ADMIN = "admin_page";
    public static final String SOURCE_ORGANIZER = "organizer_page";
    public static final String SOURCE_PARTICIPANT = "participant_page";

    @SuppressLint("SetTextI18n")
    @SuppressWarnings("deprecation")
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
        Button functionButton = findViewById(R.id.functionButton);

        // Get data passed from intent
        int eventID = getIntent().getIntExtra("eventId", -1);
        String attendeeID = getIntent().getStringExtra("attendeeId");

        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String fee = getIntent().getStringExtra("fee");
        String datetime = getIntent().getStringExtra("datetime");

        String organizer = getIntent().getStringExtra("organizer");
        int categoryID = getIntent().getIntExtra("categoryId", -1);
        Category category = dBHelper.getCategoryById(categoryID);
        String categoryName = category.getName();

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
            functionButton.setEnabled(false);
            joinButton.setVisibility(View.INVISIBLE);
            functionButton.setVisibility(View.INVISIBLE);
        } else if (SOURCE_ORGANIZER.equals(source)) {
            contextInfoText.setText("Category: " + categoryName);
            joinButton.setText("Manage Requests");
            functionButton.setText("Attendee List");
            joinButton.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), UserDisplayActivity.class);
                intent.putExtra(EXTRA_SOURCE, source);
                intent.putExtra("sourceContext", getIntent().getStringExtra("sourceContext"));
                intent.putExtra("eventId", eventID);
                intent.putExtra("title", title);
                intent.putExtra("description", description);
                intent.putExtra("fee", fee);
                intent.putExtra("datetime", datetime);
                intent.putExtra("categoryId", categoryID);
                intent.putExtra("organizer", organizer);
                startActivity(intent);
            });
            functionButton.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), UserDisplayActivity.class);
                intent.putExtra(EXTRA_SOURCE, SOURCE_PARTICIPANT);
                intent.putExtra("sourceContext", getIntent().getStringExtra("sourceContext"));
                intent.putExtra("eventId", eventID);
                intent.putExtra("title", title);
                intent.putExtra("description", description);
                intent.putExtra("fee", fee);
                intent.putExtra("datetime", datetime);
                intent.putExtra("categoryId", categoryID);
                intent.putExtra("organizer", organizer);
                startActivity(intent);
            });
        } else {
            contextInfoText.setText("Category: " + categoryName + "\n" + "\n" + "Organizer: " + organizer);  // Or "Unknown Source"
            if (dBHelper.hasJoinRequest(eventID, attendeeID)) {
                joinButton.setEnabled(false);
                joinButton.setText(dBHelper.getStatus(eventID, attendeeID));
                if (dBHelper.getStatus(eventID,attendeeID).equals("Refused")){
                    functionButton.setVisibility(View.GONE);
                }
                else {
                    functionButton.setText("Remove Request");
                    functionButton.setBackgroundColor(getColor(R.color.red));
                    functionButton.setOnClickListener(v -> {
                        dBHelper.deleteRequest(eventID, attendeeID);
                        joinButton.setEnabled(true);
                        joinButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
                        joinButton.setText("Join");
                        functionButton.setVisibility(View.GONE);
                        setResult(RESULT_OK);
                        joinButton.setOnClickListener(v1 -> {
                            dBHelper.submitJoinRequest(eventID, attendeeID);
                            joinButton.setEnabled(false);
                            joinButton.setBackgroundColor(Color.LTGRAY);
                            joinButton.setText(dBHelper.getStatus(eventID, attendeeID));
                            functionButton.setVisibility(View.VISIBLE);
                            setResult(RESULT_OK);
                        });
                    });
                }
                if (Objects.equals(dBHelper.getStatus(eventID, attendeeID), "Approved")) {
                    joinButton.setBackgroundColor(getColor(R.color.dark_green));
                    joinButton.setTextColor(Color.WHITE);
                } else if (Objects.equals(dBHelper.getStatus(eventID, attendeeID), "Refused")) {
                    joinButton.setBackgroundColor(getColor(R.color.red));
                    joinButton.setTextColor(Color.WHITE);
                } else {
                    joinButton.setBackgroundColor(Color.LTGRAY);

                }
            } else {
                joinButton.setText("Join");
                functionButton.setVisibility(View.GONE);
                joinButton.setOnClickListener(v -> {
                    dBHelper.submitJoinRequest(eventID, attendeeID);
                    joinButton.setEnabled(false);
                    joinButton.setBackgroundColor(Color.LTGRAY);
                    joinButton.setText(dBHelper.getStatus(eventID, attendeeID));
                    functionButton.setVisibility(View.VISIBLE);
                    functionButton.setBackgroundColor(getColor(R.color.red));
                    functionButton.setText("Remove Request");
                    setResult(RESULT_OK);
                    functionButton.setOnClickListener(v1 -> {
                        dBHelper.deleteRequest(eventID, attendeeID);
                        joinButton.setEnabled(true);
                        joinButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
                        joinButton.setText("Join");
                        functionButton.setVisibility(View.GONE);
                        setResult(RESULT_OK);
                    });
                });
            }
        }

        Button backButton = findViewById(R.id.eventDetailsBackButton);
        backButton.setOnClickListener(v -> onBackPressed());
    }
}