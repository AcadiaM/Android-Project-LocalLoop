package com.example.local_loop.Activity.Main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.local_loop.Helpers.DatabaseHelper;
import com.example.local_loop.R;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private View decorView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(this);
        db.insertAdmin();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        decorView = getWindow().getDecorView();
    }

    public void OnSignButton(View view) {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }
    public void OnCreateButton(View view) {
        // Application Context and Activity
        Intent intent = new Intent(MainActivity.this, SignUp.class);
        startActivity(intent);

    }

}