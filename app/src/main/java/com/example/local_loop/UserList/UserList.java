package com.example.local_loop.UserList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.local_loop.CreateAccount.Organizer;
import com.example.local_loop.CreateAccount.User;
import com.example.local_loop.R;
import com.example.local_loop.Welcome.AdminWelcomePage;
import com.example.local_loop.Welcome.OrganizerWelcomePage;
import com.example.local_loop.database.DatabaseHelper;

import java.util.List;

public class UserList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        try {
            recyclerView.setAdapter(new RecycleAdapter(getApplicationContext(), getData()));
        }catch (Exception e){
            Toast.makeText(this, "adapter crash:" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    private List<User> getData() {
        DatabaseHelper db = new DatabaseHelper(this);
        return db.getUsers();
    }

    public void OnBackButtonPressed(View view) {
        Intent intent = new Intent(UserList.this, AdminWelcomePage.class);
        intent.putExtra("username", getIntent().getStringExtra("username"));
        intent.putExtra("userType", getIntent().getStringExtra("userType"));
        startActivity(intent);
    }

}