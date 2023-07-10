package com.boyd.myapplication.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boyd.myapplication.Login;
import com.boyd.myapplication.R;
import com.boyd.myapplication.demo.ui.adapters.PostsAdapter;
import com.boyd.myapplication.demo.ui.viewmodels.PostsViewModel;
import com.boyd.myapplication.demo.utils.DisplayLikeListener;
import com.boyd.myapplication.demo.utils.DisplayMenuListener;
import com.boyd.myapplication.demo.utils.DisplayOfficialAccount;
import com.boyd.myapplication.demo.utils.DocumentListener;
import com.boyd.myapplication.demo.utils.OnClickUsernameListener;
import com.boyd.myapplication.demo.utils.ProfilePictureListener;
import com.boyd.myapplication.main.component.DeleteAccountPage;
import com.boyd.myapplication.main.component.EditProfilePage;
import com.boyd.myapplication.main.component.FollowersPage;
import com.boyd.myapplication.main.component.FollowingPage;
import com.boyd.myapplication.main.component.NewPostPage;
import com.boyd.myapplication.util.SessionManager;
import com.boyd.myapplication.util.DisplayLikeHandler;
import com.boyd.myapplication.util.DisplayProfile;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MainActivity extends AppCompatActivity implements DocumentListener,
        DisplayLikeListener, DisplayMenuListener, ProfilePictureListener,
        OnClickUsernameListener, DisplayOfficialAccount {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView recyclerView;
    private ShimmerFrameLayout shimmerFrameLayout;
    private PostsViewModel postsViewModel;
    private PostsAdapter postsAdapter;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            ImageView side_menu_image = findViewById(R.id.side_menu_image);
            TextView side_menu_name = findViewById(R.id.side_menu_name);

            String name = new SessionManager(this).getSession();
            String userId = new SessionManager(this).getSessionId();
            String userImg = new SessionManager(this).getSessionImg();

            if (userImg != null) {
                side_menu_image.setBackgroundResource(0);
            }

            Picasso.get().load(userImg).into(side_menu_image);

            side_menu_name.setText(name);
            side_menu_name.setOnClickListener(
                    v -> {
                        DisplayProfile displayProfile = new DisplayProfile(v.getContext(), userId, name);
                        displayProfile.goToProfilePage();
                    }
            );
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        NavigationView navigationView = findViewById(R.id.side_menu);
        drawerLayout = findViewById(R.id.drawer_layout);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.side_menu_open, R.string.side_menu_close);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.actionbar_toggle);
        }

        recyclerView = findViewById(R.id.recyclerView);
        shimmerFrameLayout = findViewById(R.id.shimmer_layout);
        shimmerFrameLayout.startShimmer();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postsAdapter = new PostsAdapter();
        recyclerView.setAdapter(postsAdapter);

        postsViewModel = new ViewModelProvider(this).get(PostsViewModel.class);
        postsViewModel.fetchPosts(getString(R.string.app_name), new SessionManager(this).getSessionId(), new SessionManager(this).getSession());
        postsViewModel.getAllPosts().observe(this, posts -> {
            postsAdapter.setPosts(posts);
            postsAdapter.setDocumentDeleteListener(this);
            postsAdapter.setDisplayLikeListener(this);
            postsAdapter.setDisplayMenuListener(this);
            postsAdapter.setProfilePictureListener(this);
            postsAdapter.setOnClickUsernameListener(this);
            postsAdapter.setDisplayOfficialAccount(this);

            //Update the UI.
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        });

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(
                v -> {
                    Intent intent = new Intent(this, NewPostPage.class);
                    startActivity(intent);
                }
        );

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(
                () -> {
                    postsViewModel.fetchPosts(getString(R.string.app_name), new SessionManager(this).getSessionId(), new SessionManager(this).getSession());
                    swipeRefreshLayout.setRefreshing(false);
                }
        );

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(
                item -> {
                    if (item.getItemId() == R.id.follower) {
                        Intent intent = new Intent(this, FollowersPage.class);
                        startActivity(intent);
                        return true;
                    }
                    if (item.getItemId() == R.id.following) {
                        Intent intent = new Intent(this, FollowingPage.class);
                        startActivity(intent);
                        return true;
                    }
                    if (item.getItemId() == R.id.edit_profile) {
                        Intent intent = new Intent(this, EditProfilePage.class);
                        startActivity(intent);
                        return true;
                    }
                    if (item.getItemId() == R.id.delete_account) {
                        Intent intent = new Intent(this, DeleteAccountPage.class);
                        startActivity(intent);
                        return true;
                    }
                    if (item.getItemId() == R.id.logout) {
                        SessionManager sessionManager = new SessionManager(this);
                        sessionManager.removeSession();

                        Intent intent = new Intent(this, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        return true;
                    }
                    return false;
                }
        );
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDocumentById(String postId, String commentId) {
        postsViewModel.deletePostsById(postId);
    }

    @Override
    public void onDisplayLike(String documentId, List<String> listUser, Long comments, TextView buttonLike, TextView likeCountTextView, LinearLayout likesCommentsLayout) {
        new DisplayLikeHandler(documentId, this, listUser, comments, buttonLike, likeCountTextView, likesCommentsLayout, postsViewModel).setLikeCount();
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
        if (imageUri != null) {
            userImage.setBackgroundResource(0);
        }
        String currentActiveUser = new SessionManager(this).getSession();
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
}