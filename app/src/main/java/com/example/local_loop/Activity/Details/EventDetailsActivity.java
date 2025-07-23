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
import com.example.local_loop.Helper.RequestStatus;
import com.example.local_loop.Helper.ViewMode;
import com.example.local_loop.Models.Account;
import com.example.local_loop.Models.Event;
import com.example.local_loop.R;

public class EventDetailsActivity extends AppCompatActivity {
    DatabaseHelper db;
    private ViewMode mode;
    private Account session;
    Event event;
    private TextView titleText, descriptionText, feeText, dateTimeText, contextInfoText;
    private Button joinButton, backButton;
    private RequestStatus status;

    @SuppressLint("SetTextI18n")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        db = new DatabaseHelper(this);

        session = getIntent().getParcelableExtra("user", Account.class);
        mode = ViewMode.valueOf(getIntent().getStringExtra(ViewMode.VIEW.name()));
        Bundle itemBundle = getIntent().getBundleExtra("event");

        if(session == null || mode == null || itemBundle == null){
            Log.d("User/Mode/Bundle Null", this.getLocalClassName());
        }
        event = Event.fromBundle(itemBundle);
        status = db.getRequestStatus(event.getID(), session.getUserID());

        //Initialize
        titleText = findViewById(R.id.eventDetailTitle);
        descriptionText = findViewById(R.id.eventDetailDescription);
        feeText = findViewById(R.id.eventDetailFee);
        dateTimeText = findViewById(R.id.eventDetailDateTime);
        contextInfoText = findViewById(R.id.eventDetailContextInfo);
        joinButton = findViewById(R.id.joinButton);


        // Set values
        titleText.setText(event.getTitle());
        descriptionText.setText(event.getDescription());
        feeText.setText("Fee: $" + event.getFee());
        dateTimeText.setText("Date/Time: " + event.getDateTime());

        setContextText();

        backButton = findViewById(R.id.eventDetailsBackButton);
        backButton.setOnClickListener(v -> onBackPressed());

    }

    @SuppressLint("SetTextI18n")
    private void setContextText(){
        String category = db.getDisplay(event.getCategoryId(), "categories");
        String organizer = db.getDisplay(event.getOrganizer(), "users");

        if(session.getRole().equals("participant")){
            contextInfoText.setText("Category: " + category + "\n" + "\n" + "Organizer: " + organizer);  // Or "Unknown Source"
            joinButton.setEnabled(false);
            joinButton.setText(status.name());

            switch (status){
                case PENDING:
                    joinButton.setBackgroundColor(Color.LTGRAY);
                    break;
                case REFUSED:
                    joinButton.setBackgroundColor(getColor(R.color.red));
                    joinButton.setTextColor(Color.WHITE);
                    break;
                case APPROVED:
                    joinButton.setBackgroundColor(getColor(R.color.dark_green));
                    joinButton.setTextColor(Color.WHITE);
                    break;

                default:
                    //Inactive
                    joinButton.setEnabled(true);
                    joinButton.setText("Join");
                    joinButton.setOnClickListener(v -> {
                        Log.d("EventDetails", "Check submission request vals. eventID: "+event.getID() +" userID" + session.getUserID() );
                        db.submitRequest(event.getID(), session.getUserID());
                        Log.d("EventDetails", "Check if Join button Status is PENDING --> "+ db.getRequestStatus(event.getID(), session.getUserID()));
                        joinButton.setEnabled(false);
                        joinButton.setBackgroundColor(Color.LTGRAY);
                        String msg = db.getRequestStatus(event.getID(), session.getUserID()).toString();
                        joinButton.setText(msg);
                    });
                    break;
            }
        }else{
            switch (mode){
                case ADMIN_EVENTS:
                    contextInfoText.setText("Organizer: " + organizer);
                    joinButton.setText("Attendee List");
                    joinButton.setOnClickListener(v -> {
                        Intent intent = new Intent(getApplicationContext(), UserDisplayActivity.class);
                        intent.putExtra(ViewMode.VIEW.name(), mode.name());
                        intent.putExtra("user", session);
                        intent.putExtra("eventID", event.getID());
                        startActivity(intent);
                    });
                    break;

                case ORG_EVENTS:
                    contextInfoText.setText("Category: " + category);
                    joinButton.setText("Attendee List");
                    joinButton.setOnClickListener(v -> {
                        Intent intent = new Intent(getApplicationContext(), UserDisplayActivity.class);
                        intent.putExtra(ViewMode.VIEW.name(), mode.name());
                        intent.putExtra("user", session);
                        intent.putExtra("eventID", event.getID());
                        startActivity(intent);
                    });
                    break;

                default:
            }
        }

    }
@Override
    protected void onDestroy(){
        super.onDestroy();
        db.close();
    }
}
