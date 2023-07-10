package com.boyd.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.boyd.myapplication.demo.ui.viewmodels.RegisterViewModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText username = findViewById(R.id.registerName);
        EditText password = findViewById(R.id.registerPassword);
        Button registerButton = findViewById(R.id.registerButton);
        TextView toLoginButton = findViewById(R.id.toLoginButton);

        ProgressDialog progressDialog = new ProgressDialog(this);

        RegisterViewModel registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        registerViewModel.getRegisterSuccess().observe(this, result -> {
            if (result) {
                Toast.makeText(this, "Successfully registered.", Toast.LENGTH_SHORT).show();
                username.setText("");
                password.setText("");
            }
            if (!result) {
                Toast.makeText(this, "Details already exist.", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        });

        registerButton.setOnClickListener(
                v -> {
                    progressDialog.setMessage("Signing up.");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    String pattern = ".*\\\\s.*";
                    Pattern whitespacePattern = Pattern.compile(pattern);
                    Matcher matcherUsername = whitespacePattern.matcher(username.getText().toString());
                    Matcher matcherPassword = whitespacePattern.matcher(password.getText().toString());

                    if (matcherUsername.matches() || matcherPassword.matches()) {
                        Toast.makeText(this, "Invalid input.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        return;
                    }

                    if (!matcherUsername.matches() && !matcherPassword.matches()) {
                        if (username.length() >= 4 && password.length() >= 4) {
                            registerViewModel.registerUser(username.getText().toString(), password.getText().toString());
                        } else {
                            Toast.makeText(this, "Username/ Password should be more than 4 characters.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }

                    //Hide keyboard.
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
        );

        toLoginButton.setOnClickListener(
                v -> moveToLoginActivity()
        );
    }

    public void moveToLoginActivity() {
        /* Move to the main activity. */
        Intent intent = new Intent(Register.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}