package com.example.local_loop.AttendeeList;

import static com.example.local_loop.Event.EventDetailsActivity.EXTRA_SOURCE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.CreateAccount.User;
import com.example.local_loop.Event.EventDetailsActivity;
import com.example.local_loop.R;
import com.example.local_loop.database.DatabaseHelper;

import java.util.List;

public class AttendeeList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendee_list);

        int eventID = getIntent().getIntExtra("eventId", -1);
        RecyclerView attendeeRecycler = findViewById(R.id.attendee_recycler);
        attendeeRecycler.setLayoutManager(new LinearLayoutManager(this));
        try {
            attendeeRecycler.setAdapter(new RecycleAdapterByEvent(getApplicationContext(), getData(eventID), eventID));
        }catch (Exception e){
            Toast.makeText(this, "adapter crash:" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    private List<User> getData(int eventID) {
        DatabaseHelper db = new DatabaseHelper(this);
        return db.getPendingRequestsByEvent(eventID);
    }

    public void OnAttendeeBackButtonPressed(View view) {
        Intent intent = new Intent(com.example.local_loop.AttendeeList.AttendeeList.this, EventDetailsActivity.class);
        intent.putExtra("username", getIntent().getStringExtra("organizer"));
        intent.putExtra("userType", "organizer");
        intent.putExtra(EXTRA_SOURCE, getIntent().getStringExtra(EXTRA_SOURCE));
        intent.putExtra("sourceContext", getIntent().getStringExtra("sourceContext"));
        intent.putExtra("eventId", getIntent().getIntExtra("eventId",-1));
        intent.putExtra("title", getIntent().getStringExtra("title"));
        intent.putExtra("description", getIntent().getStringExtra("description"));
        intent.putExtra("fee", getIntent().getStringExtra("fee"));
        intent.putExtra("datetime", getIntent().getStringExtra("datetime"));
        intent.putExtra("categoryId", getIntent().getIntExtra("categoryId",-1));
        intent.putExtra("organizer", getIntent().getStringExtra("organizer"));
        startActivity(intent);
    }

}