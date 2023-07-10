package com.boyd.myapplication.demo.utils;

import android.widget.ImageView;

public interface ProfilePictureListener {
    void onDisplayProfilePicture(String imageUri, String username, ImageView userImage);
}
