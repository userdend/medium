package com.boyd.myapplication.main.component;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.boyd.myapplication.R;
import com.boyd.myapplication.demo.ui.viewmodels.PostsViewModel;
import com.boyd.myapplication.demo.utils.OnTaskCompletedListener;
import com.boyd.myapplication.main.MainActivity;
import com.boyd.myapplication.util.SessionManager;
import com.boyd.myapplication.util.DateConfiguration;
import com.boyd.myapplication.util.SelectImage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewPostPage extends AppCompatActivity {
    private Uri imageUri;
    private Boolean imageContent = false;
    private PostsViewModel postsViewModel;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ImageView imageSelected = findViewById(R.id.imageSelected);
        if (requestCode == 100 && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageSelected.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
            imageSelected.requestLayout();
            imageSelected.setImageURI(imageUri);
            imageContent = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        //INITIALIZE TOOLBAR PROPERTIES.
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);

        //GET UI ID.
        ImageView backButton = findViewById(R.id.backButton);
        ImageView createNewPostButton = findViewById(R.id.createNewPostButton);
        EditText newPostInput = findViewById(R.id.newPostInput);
        TextView addPhoto = findViewById(R.id.addPhoto);

        //SET LAYOUT TITLE.
        toolbarTitle.setText(R.string.dynamic_text_create_post);

        //SET BACK BUTTON FUNCTIONALITY.
        backButton.setOnClickListener(
                v -> finish()
        );

        //ON SELECT IMAGES.
        addPhoto.setOnClickListener(
                v -> (new SelectImage(NewPostPage.this)).openGallery()
        );

        postsViewModel = new ViewModelProvider(this).get(PostsViewModel.class);

        //ON POST CLICK.
        createNewPostButton.setOnClickListener(
                view -> {
                    ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("Creating a new post.");
                    progressDialog.show();

                    String pattern = "^\\s*$";
                    Pattern whitespacePattern = Pattern.compile(pattern);
                    Matcher matcher = whitespacePattern.matcher(newPostInput.getText().toString());

                    if (matcher.matches()) {
                        Toast.makeText(this, "Input must not empty.", Toast.LENGTH_SHORT).show();
                    }

                    if (!matcher.matches()) {
                        //GET ALL DATA.
                        UUID uuid = UUID.randomUUID();
                        String documentId = uuid.toString();
                        String userImageUrl = new SessionManager(getApplicationContext()).getSessionImg();
                        String userId = new SessionManager(getApplicationContext()).getSessionId();
                        String name = new SessionManager(getApplicationContext()).getSession();
                        String content = newPostInput.getText().toString();
                        String creationDate = new DateConfiguration(null).timestampInMilliseconds();

                        //STORE THE DATA LOCALLY.
                        Map<String, Object> newPostsDetails = new HashMap<>();
                        newPostsDetails.put("documentId", documentId);
                        newPostsDetails.put("userId", userId);
                        newPostsDetails.put("userImageUrl", userImageUrl);
                        newPostsDetails.put("name", name);
                        newPostsDetails.put("content", content);
                        newPostsDetails.put("creationDate", creationDate);

                        //CHECK IF THE CONTENT CONTAIN IMAGES.
                        if (imageContent) {
                            postsViewModel.addNewPostsWithImage(documentId, imageUri, newPostsDetails, new OnTaskCompletedListener<Boolean>() {
                                @Override
                                public void onSuccess(Boolean result) {
                                    if(result) {
                                        progressDialog.dismiss();
                                        startActivity(new Intent(NewPostPage.this, MainActivity.class));
                                    }
                                }

                                @Override
                                public void onError(String errorMessage) {
                                    //Later.
                                }
                            });
                        }

                        if (!imageContent) {
                            postsViewModel.addNewPostsWithNoImage(newPostsDetails);
                            progressDialog.dismiss();
                            startActivity(new Intent(NewPostPage.this, MainActivity.class));
                        }

                        //Hide keyboard.
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
        );
    }
}