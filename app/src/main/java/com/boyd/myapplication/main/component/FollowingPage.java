package com.boyd.myapplication.main.component;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boyd.myapplication.R;
import com.boyd.myapplication.demo.ui.adapters.RelationAdapter;
import com.boyd.myapplication.demo.ui.viewmodels.RelationViewModel;
import com.boyd.myapplication.demo.utils.OnClickUsernameListener;
import com.boyd.myapplication.util.SessionManager;
import com.boyd.myapplication.util.DisplayProfile;
import com.facebook.shimmer.ShimmerFrameLayout;

public class FollowingPage extends AppCompatActivity implements OnClickUsernameListener {
    private RelationAdapter relationAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
        toolbarTitle.setText(R.string.dynamic_text_following);

        String ACTIVE_USER_ID = new SessionManager(this).getSessionId();

        ShimmerFrameLayout shimmerFrameLayout = findViewById(R.id.activity_following_shimmer_layout);
        shimmerFrameLayout.startShimmer();

        RecyclerView recyclerView = findViewById(R.id.activity_following_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        relationAdapter = new RelationAdapter();
        recyclerView.setAdapter(relationAdapter);

        RelationViewModel relationViewModel = new ViewModelProvider(this).get(RelationViewModel.class);
        relationViewModel.fetchRelation(ACTIVE_USER_ID);

        relationViewModel.getRelationMutableLiveData().observe(this, relation -> {
            relationAdapter.setRelations(relation.getFollowing());
            relationAdapter.setOnClickUsernameListener(this);

            //Update the UI.
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        });
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
}
