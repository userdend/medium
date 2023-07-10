package com.boyd.myapplication.main.component;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.boyd.myapplication.R;
import com.boyd.myapplication.demo.data.model.User;
import com.boyd.myapplication.demo.ui.viewmodels.EditProfilePageViewModel;
import com.boyd.myapplication.util.SessionManager;
import com.boyd.myapplication.util.SelectImage;
import com.squareup.picasso.Picasso;

public class EditProfilePage extends AppCompatActivity {
    private Uri imageUri;
    private Boolean theUserHasPickAnImage = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Gallery.
        if (requestCode == 100 && data != null && data.getData() != null) {
            ImageView edit_profile_img = findViewById(R.id.edit_profile_img);
            imageUri = data.getData();
            edit_profile_img.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
            edit_profile_img.requestLayout();
            edit_profile_img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            edit_profile_img.setImageURI(imageUri);
            theUserHasPickAnImage = true;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        ImageView backButton = findViewById(R.id.backButton);
        ImageView createNewPostButton = findViewById(R.id.createNewPostButton);
        ImageView edit_profile_img = findViewById(R.id.edit_profile_img);

        backButton.setOnClickListener(v -> finish());
        toolbarTitle.setText(R.string.dynamic_text_edit_profile);

        EditText edit_profile_name = findViewById(R.id.edit_profile_name);
        TextView edit_profile_img_change = findViewById(R.id.edit_profile_img_change);

        edit_profile_name.setEnabled(false);

        //Get session.
        String ACTIVE_USER = new SessionManager(this).getSession();
        String ACTIVE_USER_ID = new SessionManager(this).getSessionId();
        edit_profile_name.setText(ACTIVE_USER);

        ProgressDialog progressDialog = new ProgressDialog(this);

        EditProfilePageViewModel editProfilePageViewModel = new ViewModelProvider(this).get(EditProfilePageViewModel.class);
        editProfilePageViewModel.setImageUri(ACTIVE_USER_ID);
        editProfilePageViewModel.getImageUri().observe(this, uri -> {
            User user = new User(0, null, ACTIVE_USER, ACTIVE_USER_ID, uri.toString());
            new SessionManager(this).saveSession(user);
            edit_profile_img.setBackgroundResource(0);
            Picasso.get().load(uri).into(edit_profile_img);
            progressDialog.dismiss();
        });

        edit_profile_img_change.setOnClickListener(
                v -> (new SelectImage(EditProfilePage.this)).openGallery()
        );

        createNewPostButton.setOnClickListener(
                v -> {
                    if (theUserHasPickAnImage) {
                        progressDialog.setMessage("Updating.");
                        progressDialog.show();

                        editProfilePageViewModel.addImageUri(imageUri, ACTIVE_USER_ID);
                    }
                }
        );
    }
}
