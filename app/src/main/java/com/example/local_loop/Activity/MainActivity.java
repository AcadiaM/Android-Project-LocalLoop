package com.example.local_loop.Activity;

import com.example.local_loop.R;
import com.example.local_loop.Helpers.DatabaseHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DatabaseHelper dbHelper;
    private View decorView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(this);
        dbHelper.insertAdmin();

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