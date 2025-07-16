package com.example.local_loop.Event;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.Category.Category;
import com.example.local_loop.CreateAccount.User;
import com.example.local_loop.R;
import com.example.local_loop.UserList.RecycleAdapter;
import com.example.local_loop.database.DatabaseHelper;

import java.util.List;
import java.util.Objects;

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
        RecyclerView attendeeRecycler = findViewById(R.id.attendee_recycler);

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
            attendeeRecycler.setVisibility(View.VISIBLE);
            joinButton.setEnabled(false);
            attendeeRecycler.setLayoutManager(new LinearLayoutManager(this));
            try {
                attendeeRecycler.setAdapter(new RecycleAdapterByEvent(getApplicationContext(), dBHelper.getPendingRequestsByEvent(eventID), eventID));
            }catch (Exception e){
                Toast.makeText(this, "adapter crash:" + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else {
            contextInfoText.setText("Category: " + categoryName + "\n" + "\n" + "Organizer: " + organizer);  // Or "Unknown Source"
            joinButton.setVisibility(View.VISIBLE);
            if (dBHelper.hasJoinRequest(eventID, attendeeID)){
                joinButton.setEnabled(false);
                joinButton.setText(dBHelper.getStatus(eventID,attendeeID));
                if (Objects.equals(dBHelper.getStatus(eventID, attendeeID), "Approved")){
                    joinButton.setBackgroundColor(Color.GREEN);
                    joinButton.setTextColor(Color.WHITE);
                } else if (Objects.equals(dBHelper.getStatus(eventID, attendeeID), "Refused")){
                    joinButton.setBackgroundColor(Color.RED);
                    joinButton.setTextColor(Color.WHITE);
                } else {
                    joinButton.setBackgroundColor(Color.LTGRAY);
                }
            }
            else{
                joinButton.setOnClickListener(v -> {
                            dBHelper.submitJoinRequest(eventID, attendeeID);
                            joinButton.setEnabled(false);
                            joinButton.setBackgroundColor(Color.LTGRAY);
                            joinButton.setText(dBHelper.getStatus(eventID,attendeeID));
                });
            }
        }
    }
}
