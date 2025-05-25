package com.example.local_loop.userClasses;

import com.example.local_loop.R;
import com.example.local_loop.database.DatabaseHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.person_position, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
    public void onSubmitButton(View view) {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        EditText first, user, last, email, password;
        String rInput, fInput, lInput, uInput, eInput, pInput;
        rInput = spinner.getSelectedItem().toString();
        first = (EditText) findViewById(R.id.firstNameInputEditText);
        fInput = first.getText().toString();
        last = (EditText) findViewById(R.id.lastNameInputEditText);
        lInput = last.getText().toString();
        user = (EditText) findViewById(R.id.userNameInputEditText);
        uInput = user.getText().toString();
        email = (EditText) findViewById(R.id.emailInputEditText);
        eInput = email.getText().toString();
        password = (EditText) findViewById(R.id.passwordInputEditText);
        pInput = password.getText().toString();
        if (!rInput.isEmpty() && !uInput.isEmpty() && !fInput.isEmpty() && !lInput.isEmpty() && !eInput.isEmpty() && !pInput.isEmpty()) {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            boolean inserted = dbHelper.insertUser(uInput, fInput, lInput, eInput, pInput, rInput);
            if (inserted) {
                Intent intent = new Intent(getApplicationContext(), WelcomePage.class);
                startActivityForResult(intent, 0);
                Toast.makeText(ProfileActivity.this, "Welcome " + fInput + "! You are logged in as " + rInput + ".", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Username or email already exists.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}