package com.boyd.myapplication.util;

import android.content.Context;
import android.content.Intent;

import com.boyd.myapplication.main.component.ProfilePage;

public class DisplayProfile {
    Context context;
    String name;
    String userId;

    public DisplayProfile(Context context, String userId, String name) {
        this.context = context;
        this.userId = userId;
        this.name = name;
    }

    public void goToProfilePage() {
        Intent intent = new Intent(context, ProfilePage.class);
        intent.putExtra("name", name);
        intent.putExtra("userId", userId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
