package com.example.local_loop.Activity;

import static com.example.local_loop.Details.EventDetailsActivity.EXTRA_SOURCE;
import static com.example.local_loop.Details.EventDetailsActivity.SOURCE_ADMIN;
import static com.example.local_loop.Details.EventDetailsActivity.SOURCE_ORGANIZER;
import static com.example.local_loop.Details.EventDetailsActivity.SOURCE_PARTICIPANT;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.UserContent.User;
import com.example.local_loop.Helpers.DatabaseHelper;
import com.example.local_loop.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Objects;

public class UserDisplayActivity extends AppCompatActivity {

    private View decorView;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_display);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int eventId = getIntent().getIntExtra("eventId", -1);
        String source = getIntent().getStringExtra(EXTRA_SOURCE);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        TextView noUsersTextView = findViewById(R.id.noUsersTextView);
        TextView pageTitleTextView = findViewById(R.id.pageTitleTextView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        try {
            List<User> users;
            if (SOURCE_ORGANIZER.equals(source)) {
                try (DatabaseHelper db = new DatabaseHelper(this)) {
                    users = db.getRequestsByEvent(eventId, "pending");
                }
                setTitle("Request List");
                pageTitleTextView.setText(R.string.requestManagement);
                noUsersTextView.setText(R.string.no_requests);
            } else if (SOURCE_PARTICIPANT.equals(source)) {
                try (DatabaseHelper db = new DatabaseHelper(this)) {
                    users = db.getRequestsByEvent(eventId, "Approved");
                }
                setTitle("Attendee List");
                pageTitleTextView.setText(R.string.attendeeList);
                noUsersTextView.setText(R.string.no_attendees);
            } else {
                try (DatabaseHelper db = new DatabaseHelper(this)) {
                    users = db.getUsers();
                }
                setTitle("User List");
                pageTitleTextView.setText(R.string.userManagement);
                noUsersTextView.setText(R.string.no_users);
            }

            if (users == null || users.isEmpty()) {
                noUsersTextView.setVisibility(View.VISIBLE);
            } else {
                noUsersTextView.setVisibility(View.GONE);
            }

            recyclerView.setAdapter(new UserDisplayAdapter(this, users, source, eventId, noUsersTextView));

        } catch (Exception e) {
            Toast.makeText(this, "Adapter error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if (visibility == 0) {
                decorView.setSystemUiVisibility(hideSystemBars());
            }
        });

        findViewById(R.id.userBackButton).setOnClickListener(v -> onBackPressed());
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

    // -------------------------------
    // INNER CLASS: ADAPTER
    // -------------------------------
    public static class UserDisplayAdapter extends RecyclerView.Adapter<UserDisplayViewHolder> {

        private final Context context;
        private final List<User> users;
        private final DatabaseHelper db;
        private final String source;
        private final int eventId;
        private final TextView noUsersTextView;

        public UserDisplayAdapter(Context context, List<User> users, String source, int eventId, TextView noUsersTextView) {
            this.context = context;
            this.users = users;
            this.source = source;
            this.eventId = eventId;
            this.noUsersTextView = noUsersTextView;
            this.db = new DatabaseHelper(context);
        }

        @NonNull
        @Override
        public UserDisplayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int layoutId = R.layout.user_list_layout;
            return new UserDisplayViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull UserDisplayViewHolder holder, int position) {
            User user = users.get(position);
            holder.firstView.setText(user.getFirstName());
            holder.lastView.setText(user.getLastName());
            holder.userView.setText(user.getUsername());
            holder.emailView.setText(user.getEmail());
            holder.typeView.setText(user.getRole());

            if (SOURCE_ORGANIZER.equals(source)) {
                holder.disable.setOnClickListener(v -> {
                    approve(eventId, user.getUsername());
                    deleteEntry(user.getEmail());
                    notifyItemRemoved(holder.getAdapterPosition());
                });

                holder.delete.setOnClickListener(v -> {
                    refuse(eventId, user.getUsername());
                    deleteEntry(user.getEmail());
                    notifyItemRemoved(holder.getAdapterPosition());
                });

            } else if (SOURCE_ADMIN.equals(source)) {
                holder.disable.setIcon(Objects.requireNonNull(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.disableicon)));
                holder.delete.setIcon(Objects.requireNonNull(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.deleteicon)));
                holder.delete.setIconTint(ColorStateList.valueOf(Color.BLACK));
                holder.delete.setOnClickListener(v -> deleteUser(user.getEmail(), holder.getAdapterPosition()));

                holder.disable.setOnClickListener(v -> toggleUserActiveStatus(holder, user.getUsername(), holder.getAdapterPosition()));

                if (db.isActive(user.getUsername()) == 0) {
                    holder.disable.setIconTint(ColorStateList.valueOf(Color.RED));
                    holder.disable.setHint("User is disabled.");
                } else {
                    holder.disable.setIconTint(ColorStateList.valueOf(Color.BLACK));
                    holder.disable.setHint("User is not disabled.");
                }
            } else {
                holder.delete.setIcon(Objects.requireNonNull(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.outline_cancel_24)));
                holder.delete.setIconTint(ColorStateList.valueOf(Color.RED));
                holder.delete.setOnClickListener(v -> {
                    refuse(eventId, user.getUsername());
                    deleteEntry(user.getEmail());
                    notifyItemRemoved(holder.getAdapterPosition());
                });
                holder.disable.setVisibility(View.GONE);
            }
        }

        private void approve(int eventId, String attendeeId) {
            db.updateRequestStatus(eventId, attendeeId, "Approved");
            Toast.makeText(context, "User " + attendeeId + " approved.", Toast.LENGTH_SHORT).show();
            updateEmptyViewVisibility();
        }

        private void refuse(int eventId, String attendeeId) {
            db.updateRequestStatus(eventId, attendeeId, "Refused");
            Toast.makeText(context, "User " + attendeeId + " refused.", Toast.LENGTH_SHORT).show();
            updateEmptyViewVisibility();
        }

        private void toggleUserActiveStatus(UserDisplayViewHolder holder, String username, int pos) {
            if (db.isActive(username) == 1) {
                db.deactivateUser(username);
                holder.disable.setIconTint(ColorStateList.valueOf(Color.RED));
                Toast.makeText(context, "User " + username + " disabled.", Toast.LENGTH_SHORT).show();
            } else {
                db.reactivateUser(username);
                holder.disable.setIconTint(ColorStateList.valueOf(Color.BLACK));
                Toast.makeText(context, "User " + username + " enabled.", Toast.LENGTH_SHORT).show();
            }
            notifyItemChanged(pos);
        }

        private void updateEmptyViewVisibility() {
            if (users == null || users.isEmpty()) {
                noUsersTextView.setVisibility(View.VISIBLE);
            } else {
                noUsersTextView.setVisibility(View.GONE);
            }
        }

        private void deleteUser(String email, int pos) {
            String username = db.getUsernameByEmail(email);
            db.deleteUser(email);
            deleteEntry(email);
            Toast.makeText(context, "User " + username + " deleted.", Toast.LENGTH_SHORT).show();
            notifyItemRemoved(pos);
            updateEmptyViewVisibility();
        }

        public void deleteEntry(String email) {
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getEmail().equals(email)) {
                    users.remove(i);
                    break;
                }
            }
            updateEmptyViewVisibility();
        }

        @Override
        public int getItemCount() {
            return users.size();
        }
    }

    // -------------------------------
    // INNER CLASS: VIEW HOLDER
    // -------------------------------
    public static class UserDisplayViewHolder extends RecyclerView.ViewHolder {

        TextView firstView, lastView, userView, emailView, typeView;
        MaterialButton disable, delete;

        public UserDisplayViewHolder(@NonNull View itemView) {
            super(itemView);
            firstView = itemView.findViewById(R.id.FirstName);
            lastView = itemView.findViewById(R.id.LastName);
            userView = itemView.findViewById(R.id.Username);
            emailView = itemView.findViewById(R.id.Email);
            typeView = itemView.findViewById(R.id.Type);
            delete = itemView.findViewById(R.id.delete);
            disable = itemView.findViewById(R.id.disable);
        }
    }
}
