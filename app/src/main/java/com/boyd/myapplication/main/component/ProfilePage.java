package com.boyd.myapplication.main.component;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.boyd.myapplication.R;
import com.boyd.myapplication.demo.ui.viewmodels.ProfilePageViewModel;
import com.boyd.myapplication.util.SessionManager;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfilePage extends AppCompatActivity {
    private Boolean follow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        //TOOLBAR.
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        ImageView backButton = findViewById(R.id.backButton);

        //TOOLBAR PROPERTIES.
        Bundle extras = getIntent().getExtras();
        String user = extras.getString("name");
        String userId = extras.getString("userId");

        toolbarTitle.setText(user);
        backButton.setOnClickListener(
                v -> finish()
        );

        //SET IMAGE BACKGROUND.
        setProfilePagePicture(userId);

        //FOLLOW-FOLLOWING FUNCTIONALITY.
        Button profilePageFollowButton = findViewById(R.id.profilePage_followButton);

        String activeUser = new SessionManager(getApplicationContext()).getSession();
        String activeUserId = new SessionManager(getApplicationContext()).getSessionId();

        if (user.equals(activeUser)) {
            profilePageFollowButton.setVisibility(View.INVISIBLE);
        }

        TextView profilePage_follower = findViewById(R.id.profilePage_follower);
        TextView profilePage_following = findViewById(R.id.profilePage_following);

        ProfilePageViewModel profilePageViewModel = new ViewModelProvider(this).get(ProfilePageViewModel.class);
        profilePageViewModel.fetchRelations(userId);
        profilePageViewModel.getRelations().observe(this, relation -> {

            List<HashMap<String, String>> follower = relation.getFollower();
            List<HashMap<String, String>> following = relation.getFollowing();

            int followerSize = follower != null ? follower.size() : 0;
            int followingSize = following != null ? following.size() : 0;

            String displayFollower = followerSize + " Follower";
            String displayFollowing = followingSize + " Following";

            profilePage_follower.setText(displayFollower);
            profilePage_following.setText(displayFollowing);

            if (followerSize == 0) {
                follow = false;
                profilePageFollowButton.setText(R.string.profile_page_follow_button);
            } else {
                for (int i = 0; i < followerSize; i++) {
                    if (activeUser.equals(follower.get(i).get("name"))) {
                        follow = true;
                        profilePageFollowButton.setText(R.string.profile_page_unfollow_button);
                        break;
                    }
                }
            }
        });

        profilePageFollowButton.setOnClickListener(
                view -> {
                    Map<String, String> intoOrFromActiveUser = new HashMap<>();
                    intoOrFromActiveUser.put("name", user);
                    intoOrFromActiveUser.put("userId", userId);

                    Map<String, String> intoOrFromUser = new HashMap<>();
                    intoOrFromUser.put("name", activeUser);
                    intoOrFromUser.put("userId", activeUserId);

                    if (follow) {
                        //The user wants to unfollow.
                        follow = false;
                        profilePageViewModel.unFollowUser(activeUserId, userId, intoOrFromActiveUser, intoOrFromUser);
                        profilePageFollowButton.setText(R.string.profile_page_follow_button);
                    } else {
                        //The user wants to follow.
                        follow = true;
                        profilePageViewModel.followUser(activeUserId, userId, intoOrFromActiveUser, intoOrFromUser);
                        profilePageFollowButton.setText(R.string.profile_page_unfollow_button);
                    }
                }
        );
    }

    private void setProfilePagePicture(String userId) {
        ImageView profilePage_picture = findViewById(R.id.profilePage_picture);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/user/" + userId);
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            if (uri != null) {
                profilePage_picture.setBackgroundResource(0);
            }
            Picasso.get().load(uri).into(profilePage_picture);
        });
    }
}