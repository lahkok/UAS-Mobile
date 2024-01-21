package com.example.uas_mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String SHARED_PREF_NAME = "token_pref";
    private static final String KEY_NAME = "token";

    public TokenManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveToken(String token) {
        editor.putString(KEY_NAME, token);
        editor.apply();
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_NAME, null);
    }

    public void saveUserId(int userId) {
        editor.putInt("userId", userId);
        editor.apply();
    }

    public int getUserId() {
        return sharedPreferences.getInt("userId", -1);
    }
}
