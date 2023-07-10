package com.boyd.myapplication.util;

import android.app.Activity;
import android.content.Intent;

public class SelectImage {
    Activity activity;

    public SelectImage(Activity activity) {
        this.activity = activity;
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent, 100);
    }
}
