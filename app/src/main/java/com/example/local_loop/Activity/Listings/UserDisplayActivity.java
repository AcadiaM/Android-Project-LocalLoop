package com.example.local_loop.Activity.Listings;

import android.os.Bundle;
import android.util.Log;
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

import com.example.local_loop.Helper.ViewMode;
import com.example.local_loop.Models.Account;
import com.example.local_loop.Adapters.UserDisplayAdapter;
import com.example.local_loop.Helper.DatabaseHelper;
import com.example.local_loop.R;

import java.util.List;

public class UserDisplayActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private View decorView;
    private ViewMode mode;
    private Account session;
    private int eventID;

    @SuppressWarnings({"CallToPrintStackTrace", "deprecation"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_display);  // Shared layout
        EdgeToEdge.enable(this);
        db = new DatabaseHelper(this);

        session = getIntent().getParcelableExtra("user", Account.class);
        mode = ViewMode.valueOf(getIntent().getStringExtra(ViewMode.VIEW.name()));
        if (session == null || mode == null) {
            Log.d("UDA","Session or view mode is null ");
            finish();
            return;
        }

        eventID = getIntent().getIntExtra("eventID", -1);
        Log.d("UserDisplayA", "Event ID is --> " + eventID);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        TextView noUsersTextView = findViewById(R.id.noUsersTextView);
        TextView pageTitleTextView = findViewById(R.id.pageTitleTextView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        try {
            List<Account> users;
            if(mode == ViewMode.ADMIN_USERS){
                users = db.getUsers();
                setTitle("User List");
                pageTitleTextView.setText(R.string.userManagement);
                noUsersTextView.setText(R.string.no_users);
            }else{
                users = db.getPendingRequestByEvents(eventID);
                setTitle("Attendees");
                pageTitleTextView.setText(R.string.attendeeList);
                noUsersTextView.setText(R.string.no_attendees);
            }

            if (users == null || users.isEmpty()) {
                noUsersTextView.setVisibility(View.VISIBLE);
            } else {
                noUsersTextView.setVisibility(View.GONE);
            }
            recyclerView.setAdapter(new UserDisplayAdapter(this, users, mode, eventID, noUsersTextView));

        } catch (Exception e) {
            Toast.makeText(this, "Adapter error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
