package com.example.local_loop.UserDisplay;

import static com.example.local_loop.Event.EventDetailsActivity.EXTRA_SOURCE;
import static com.example.local_loop.Event.EventDetailsActivity.SOURCE_ORGANIZER;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.CreateAccount.User;
import com.example.local_loop.R;
import com.example.local_loop.database.DatabaseHelper;

import java.util.List;

public class UserDisplayActivity extends AppCompatActivity {

    DatabaseHelper db;
    private View decorView;

    @SuppressWarnings({"CallToPrintStackTrace", "deprecation"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_user_display);  // Shared layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new DatabaseHelper(this);

        boolean isAttendeeMode = SOURCE_ORGANIZER.equals(getIntent().getStringExtra(EXTRA_SOURCE));
        int eventId = getIntent().getIntExtra("eventId", -1);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        TextView noUsersTextView = findViewById(R.id.noUsersTextView);
        TextView pageTitleTextView = findViewById(R.id.pageTitleTextView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        try {
            List<User> users;
            if (isAttendeeMode) {
                users = db.getPendingRequestsByEvent(eventId);
                setTitle("Attendees");
                pageTitleTextView.setText(R.string.attendeeList);
                noUsersTextView.setText(R.string.no_attendees);
            } else {
                users = db.getUsers();
                setTitle("User List");
                pageTitleTextView.setText(R.string.userManagement);
                noUsersTextView.setText(R.string.no_users);
            }

            if (users == null || users.isEmpty()) {
                noUsersTextView.setVisibility(View.VISIBLE);
            } else {
                noUsersTextView.setVisibility(View.GONE);
            }

            recyclerView.setAdapter(new UserDisplayAdapter(this, users, isAttendeeMode, eventId));


        } catch (Exception e) {
            Toast.makeText(this, "Adapter error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if (visibility == 0) {
                decorView.setSystemUiVisibility(hideSystemBars());
            }
        });

        Button backButton = findViewById(R.id.userBackButton);
        backButton.setOnClickListener(v -> onBackPressed());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }

    @SuppressWarnings("deprecation")
    private int hideSystemBars() {
        return (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}
