package com.boyd.myapplication.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.boyd.myapplication.demo.data.model.User;

public class SessionManager {
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    /* USER PURPOSE. */
    String SHARED_PREFERENCES_NAME = "session";
    String USER_SESSION_KEY = "user_session_key";
    String USER_SESSION_ID = "user_session_id";
    String USER_SESSION_IMAGE = "user_session_image";

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /* USER SESSION. */
    public void saveSession(User user) {
        /* Save session of user whenever user is logged in. */
        String key = user.getName();
        String id = user.getUserId();
        String img = user.getUserImg();
        editor.putString(USER_SESSION_KEY, key).apply();
        editor.putString(USER_SESSION_ID, id).apply();
        editor.putString(USER_SESSION_IMAGE, img).apply();
    }

    public String getSession() {
        /* Return user whose session is saved. */
        return sharedPreferences.getString(USER_SESSION_KEY, null);
    }

    public String getSessionId() {
        /* Return user whose session is saved. */
        return sharedPreferences.getString(USER_SESSION_ID, null);
    }

    public String getSessionImg() {
        /* Return user whose session is saved. */
        return sharedPreferences.getString(USER_SESSION_IMAGE, null);
    }

    public void removeSession() {
        editor.putString(USER_SESSION_KEY, null).commit();
        editor.putString(USER_SESSION_ID, null).commit();
        editor.putString(USER_SESSION_IMAGE, null).commit();
    }
}
