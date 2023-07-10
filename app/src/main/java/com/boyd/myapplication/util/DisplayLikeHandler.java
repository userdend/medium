package com.boyd.myapplication.util;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boyd.myapplication.demo.ui.viewmodels.PostsViewModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DisplayLikeHandler {
    //Obtained from the adapters.
    private final String documentId;
    private final Long comments;

    //UI related.
    private final TextView buttonLike;
    private final TextView likeCountTextView;
    private final LinearLayout likesCommentsLayout;

    //Additional variables;
    private final Set<String> likedUsers;
    private final String currentUsername;
    private final PostsViewModel postsViewModel;

    public DisplayLikeHandler(String documentId, Context context, List<String> listUsers, Long comments, TextView buttonLike, TextView likeCountTextView, LinearLayout likesCommentsLayout, PostsViewModel postsViewModel) {
        this.documentId = documentId;
        this.comments = comments;
        this.buttonLike = buttonLike;
        this.likeCountTextView = likeCountTextView;
        this.likesCommentsLayout = likesCommentsLayout;
        this.likedUsers = new HashSet<>(listUsers);
        this.currentUsername = new SessionManager(context).getSession();
        this.postsViewModel = postsViewModel;
    }

    public void setLikeCount() {
        int likeCount = likedUsers.size();
        boolean hasLiked = likedUsers.contains(currentUsername);

        if (hasLiked) {
            buttonLike.setTextColor(Color.parseColor("#3498DB"));
            likedUsers.remove(currentUsername);
        } else {
            buttonLike.setTextColor(Color.parseColor("#7F8C8D"));
            likedUsers.add(currentUsername);
        }

        updateLikeCountUI(likeCount);
        updateLikesCommentsVisibility(likeCount);
        updateLikeButtonClickListener();
    }

    private void updateLikeCountUI(int likeCount) {
        String displayText = "";
        if (likeCount == 0) {
            likeCountTextView.setText("");
        } else if (likeCount == 1) {
            displayText = likeCount + " like";
        } else {
            displayText = likeCount + " likes";
        }
        setTextView(displayText);
    }

    private void updateLikesCommentsVisibility(int likeCount) {
        if (likeCount == 0 && comments == 0) {
            likesCommentsLayout.setVisibility(View.GONE);
        } else {
            likesCommentsLayout.setVisibility(View.VISIBLE);
        }
    }

    private void updateLikeButtonClickListener() {
        buttonLike.setOnClickListener(
                v -> {
                    String action = likedUsers.contains(currentUsername) ? "LIKE" : "UNLIKE";
                    postsViewModel.updatePostsLike(action, documentId, currentUsername);
                    setLikeCount();
                }
        );
    }

    private void setTextView(String displayText) {
        likeCountTextView.setText(displayText);
    }
}
