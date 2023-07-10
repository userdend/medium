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

import com.boyd.myapplication.demo.ui.viewmodels.LoginViewModel;
import com.boyd.myapplication.main.MainActivity;
import com.boyd.myapplication.util.SessionManager;

public class Login extends AppCompatActivity {
    @Override
    public void onStart() {
        super.onStart();
        checkSession();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.loginButton);
        TextView toRegisterButton = findViewById(R.id.toRegisterButton);

        ProgressDialog progressDialog = new ProgressDialog(this);

        LoginViewModel loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModel.getLoginSuccess().observe(this, loginSuccess -> {
            progressDialog.dismiss();
            if (loginSuccess) {
                moveToMainActivity();
            }
        });

        loginButton.setOnClickListener(
                view -> {
                    progressDialog.setMessage("Signing in.");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    EditText loginName = findViewById(R.id.loginName);
                    EditText loginPassword = findViewById(R.id.loginPassword);

                    String getLoginName = loginName.getText().toString();
                    String getPassword = loginPassword.getText().toString();

                    loginViewModel.login(this, getLoginName, getPassword);

                    /* Hide keyboard. */
                    if (view != null) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
        );

        toRegisterButton.setOnClickListener(
                v -> moveToRegisterActivity()
        );
    }

    public void checkSession() {
        /* 1. Check if user is logged in. */
        SessionManager sessionManager = new SessionManager(Login.this);
        String key = sessionManager.getSession();
        if (key != null) {
            /* 2. If user is logged in, move to main activity. */
            moveToMainActivity();
        }
    }

    public void moveToMainActivity() {
        /* Move to the main activity. */
        Intent intent = new Intent(Login.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void moveToRegisterActivity() {
        /* Move to the main activity. */
        Intent intent = new Intent(Login.this, Register.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
