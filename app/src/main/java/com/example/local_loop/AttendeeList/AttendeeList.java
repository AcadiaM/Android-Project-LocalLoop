package com.example.local_loop.AttendeeList;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.CreateAccount.User;
import com.example.local_loop.R;
import com.example.local_loop.database.DatabaseHelper;

import java.util.List;

public class AttendeeList extends AppCompatActivity {

    DatabaseHelper db;
    private View decorView;

    @SuppressWarnings({"CallToPrintStackTrace", "deprecation"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendee_list);

        db = new DatabaseHelper(this);

        int eventID = getIntent().getIntExtra("eventId", -1);
        RecyclerView attendeeRecycler = findViewById(R.id.attendee_recycler);
        attendeeRecycler.setLayoutManager(new LinearLayoutManager(this));
        try {
            attendeeRecycler.setAdapter(new RecycleAdapterByEvent(getApplicationContext(), getData(eventID), eventID));
        }catch (Exception e){
            Toast.makeText(this, "adapter crash:" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        //this is to hide the system bars and make the app immersive
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if (visibility == 0) {
                decorView.setSystemUiVisibility(hideSystemBars());
            }
        });

        Button backButton = findViewById(R.id.attendeeBackButton);
        backButton.setOnClickListener(v -> onBackPressed());

    }

    private List<User> getData(int eventID) {
        return db.getPendingRequestsByEvent(eventID);
    }

    //this method is called when the activity gains or loses focus
    @SuppressWarnings("deprecation")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }

    @SuppressWarnings("deprecation")
    private int hideSystemBars(){
        return (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}