package com.example.local_loop.UserList;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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
        recyclerView.setAdapter(new RecycleAdapter(getApplicationContext(), getData()));

    }

    private List<User> getData() {
        DatabaseHelper db = new DatabaseHelper(this);
        return db.getUsers();
    }

    public void OnDisable(View view) {
        TextView emailText = findViewById(R.id.Email);
        DatabaseHelper db = new DatabaseHelper(this);
        db.deactivateUser(emailText.getText().toString());
    }

    //java.lang.IllegalStateException: Could not find method OnDelete(View) in a parent or ancestor
    // Context for android:onClick attribute defined on view class android.widget.ImageButton with id 'delete'
    public void OnDelete(View view) {
        TextView emailText = findViewById(R.id.Email);
        DatabaseHelper db = new DatabaseHelper(this);
        db.deleteUser(emailText.getText().toString());
        RecyclerView recyclerView = findViewById(R.id.recycler);
        RecycleAdapter adapter = (RecycleAdapter) recyclerView.getAdapter();
        adapter.deleteEntry(emailText.getText().toString());
        //getData();
    }

}