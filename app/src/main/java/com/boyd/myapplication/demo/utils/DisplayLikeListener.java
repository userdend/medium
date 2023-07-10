package com.boyd.myapplication.demo.utils;

import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public interface DisplayLikeListener {
    void onDisplayLike(String documentId, List<String> listUser, Long comments, TextView buttonLike, TextView likes, LinearLayout likesComments);
}
