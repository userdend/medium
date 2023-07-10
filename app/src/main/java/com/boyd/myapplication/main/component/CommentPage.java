package com.boyd.myapplication.main.component;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.boyd.myapplication.R;
import com.boyd.myapplication.demo.data.model.Comment;
import com.boyd.myapplication.demo.ui.adapters.CommentAdapter;
import com.boyd.myapplication.demo.ui.viewmodels.CommentViewModel;
import com.boyd.myapplication.demo.utils.DisplayMenuListener;
import com.boyd.myapplication.demo.utils.DisplayOfficialAccount;
import com.boyd.myapplication.demo.utils.DisplayPostsOwner;
import com.boyd.myapplication.demo.utils.DocumentListener;
import com.boyd.myapplication.demo.utils.OnClickUsernameListener;
import com.boyd.myapplication.demo.utils.ProfilePictureListener;
import com.boyd.myapplication.util.SessionManager;
import com.boyd.myapplication.util.DateConfiguration;
import com.boyd.myapplication.util.DisplayProfile;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentPage extends AppCompatActivity implements DocumentListener, DisplayMenuListener,
        ProfilePictureListener, OnClickUsernameListener, DisplayOfficialAccount, DisplayPostsOwner {
    private CommentAdapter commentAdapter;
    private CommentViewModel commentViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_section);

        //TOOLBAR.
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);

        //Elements.
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        //TOOLBAR PROPERTIES.
        toolbarTitle.setText(R.string.dynamic_text_comment);

        //HIGHLIGHTS.
        Bundle extras = getIntent().getExtras();
        String postId = extras != null ? extras.getString("postId") : "";

        ShimmerFrameLayout shimmerFrameLayout = findViewById(R.id.shimmer_layout_comment);
        shimmerFrameLayout.startShimmer();

        RecyclerView recyclerView = findViewById(R.id.commentsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter();
        recyclerView.setAdapter(commentAdapter);

        commentViewModel = new ViewModelProvider(this).get(CommentViewModel.class);
        commentViewModel.fetchComments(postId);
        commentViewModel.getAllComments().observe(this, comments -> {
            commentAdapter.setComments(comments);
            commentAdapter.setCommentDeleteListener(this);
            commentAdapter.setDisplayMenuListener(this);
            commentAdapter.setProfilePictureListener(this);
            commentAdapter.setOnClickUsernameListener(this);
            commentAdapter.setDisplayOfficialAccount(this);
            commentAdapter.setDisplayPostsOwner(this);

            //Update the UI.
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        });

        //SEND A COMMENT.
        EditText inputComments = findViewById(R.id.inputComments);
        Button sendCommentButton = findViewById(R.id.sendCommentButton);
        sendCommentButton.setOnClickListener(
                view -> {
                    String pattern = "^\\s*$";
                    Pattern whitespacePattern = Pattern.compile(pattern);
                    Matcher matcher = whitespacePattern.matcher(inputComments.getText().toString());

                    if (!matcher.matches()) {
                        //Set field.
                        UUID uuid = UUID.randomUUID();
                        String request_documentId = uuid.toString();
                        String request_userId = new SessionManager(getApplicationContext()).getSessionId();
                        String request_userImg = new SessionManager(getApplicationContext()).getSessionImg();
                        String request_name = new SessionManager(getApplicationContext()).getSession();
                        String request_content = inputComments.getText().toString();
                        String request_creationDate = new DateConfiguration(null).timestampInMilliseconds();

                        //Put into comment.
                        Map<String, String> comment = new HashMap<>();
                        comment.put("documentId", request_documentId);
                        comment.put("userId", request_userId);
                        comment.put("userImageUrl", request_userImg);
                        comment.put("name", request_name);
                        comment.put("content", request_content);
                        comment.put("creationDate", request_creationDate);
                        comment.put("postId", postId);

                        //Add into database.
                        commentViewModel.addComment(comment).addOnSuccessListener(
                                command -> Toast.makeText(this, "You add a new comment.", Toast.LENGTH_SHORT).show()
                        ).addOnFailureListener(
                                e -> Toast.makeText(this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show()
                        );

                        //Clear input.
                        inputComments.setText("");

                        //Create a new item.
                        Comment newComment = new Comment();
                        newComment.setDocumentId(comment.get("documentId"));
                        newComment.setUserImageUrl(comment.get("userImageUrl"));
                        newComment.setName(comment.get("name"));
                        newComment.setContent(comment.get("content"));
                        newComment.setCreationDate(comment.get("creationDate"));
                        newComment.setPostId(comment.get("postId"));

                        //items.add(newItem);
                        commentViewModel.updateCommentLiveData(newComment);

                        //Hide keyboard.
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    } else {
                        Toast.makeText(this, "Input must not empty!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public void onDocumentById(String postId, String commentId) {
        commentViewModel.deleteCommentById(postId, commentId);
    }

    @Override
    public void onDisplayMenu(String username, TextView menuIcon) {
        String currentActiveUser = new SessionManager(this).getSession();
        if (currentActiveUser != null && (currentActiveUser.equals(username) || currentActiveUser.equals(getString(R.string.app_name)))) {
            menuIcon.setVisibility(View.VISIBLE);
        } else {
            menuIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDisplayProfilePicture(String imageUri, String username, ImageView userImage) {
        String currentActiveUser = new SessionManager(this).getSession();
        if (imageUri != null) {
            userImage.setBackgroundResource(0);
        }
        if (currentActiveUser != null && currentActiveUser.equals(username)) {
            Picasso.get().load(new SessionManager(this).getSessionImg()).into(userImage);
        } else {
            Picasso.get().load(imageUri).into(userImage);
        }
    }

    @Override
    public void onClickUsername(String userId, String username, TextView usernameLayout) {
        usernameLayout.setOnClickListener(
                v -> {
                    DisplayProfile displayProfile = new DisplayProfile(this, userId, username);
                    displayProfile.goToProfilePage();
                }
        );
    }

    @Override
    public void onDisplayOfficialAccount(String username, TextView usernameLayout) {
        usernameLayout.setText(username);
        if (username != null && username.equals(getString(R.string.app_name))) {
            usernameLayout.setTextColor(Color.parseColor(DisplayOfficialAccount.OFFICIAL_COLOUR));
        }
    }

    @Override
    public void onDisplayPostsOwner(String commenterName, TextView commentNameLayout) {
        Bundle extras = getIntent().getExtras();
        String posterName = extras != null ? extras.getString("posterName") : "";
        if (commenterName != null && (commenterName.equals(posterName) && !commenterName.equals(getString(R.string.app_name)))) {
            commentNameLayout.setTextColor(Color.parseColor(DisplayPostsOwner.OFFICIAL_POSTER_TEXT_COLOR));
        }
    }
}