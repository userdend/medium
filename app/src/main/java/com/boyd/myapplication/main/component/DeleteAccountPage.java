package com.boyd.myapplication.main.component;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.boyd.myapplication.Login;
import com.boyd.myapplication.R;
import com.boyd.myapplication.demo.ui.viewmodels.DeleteAccountPageViewModel;
import com.boyd.myapplication.util.SessionManager;

import java.util.HashMap;
import java.util.Map;

public class DeleteAccountPage extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        //TOOLBAR.
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);

        //Elements.
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        //TOOLBAR PROPERTIES.
        toolbarTitle.setText(R.string.dynamic_text_delete_account);


        ProgressDialog progressDialog = new ProgressDialog(this);

        DeleteAccountPageViewModel deleteAccountPageViewModel = new ViewModelProvider(this).get(DeleteAccountPageViewModel.class);
        deleteAccountPageViewModel.getIsDeleted().observe(this, result -> {
            if (result) {
                progressDialog.dismiss();
                new SessionManager(this).removeSession();
                startActivity(new Intent(this, Login.class));
            }
        });

        Button deleteButton = findViewById(R.id.delete_account_page_button);
        deleteButton.setOnClickListener(
                v -> {
                    progressDialog.setMessage("Deleting account.");
                    progressDialog.show();

                    Map<String, String> userDetails = new HashMap<>();
                    userDetails.put("name", new SessionManager(this).getSession());
                    userDetails.put("userId", new SessionManager(this).getSessionId());

                    deleteAccountPageViewModel.onDelete(userDetails);
                }
        );
    }
}
