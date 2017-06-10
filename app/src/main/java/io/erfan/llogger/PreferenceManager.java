package io.erfan.llogger;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    // shared pref mode
    private static final String PREF_NAME = "llogger.erfan.io";

    // pref names
    private static final String FIRST_LAUNCH = "firstLaunch";
    private static final String CURR_USER = "currentUser";

    public PreferenceManager(Context context) {
        mPreferences = context.getSharedPreferences(PREF_NAME, 0);
        mEditor = mPreferences.edit();
    }

    // make Long out of long (store null as -1)
    private void setLong(String key, Long origValue) {
        long value = origValue == null ? -1 : origValue;
        mEditor.putLong(CURR_USER, value);
        mEditor.commit();
    }
    private Long getLong(String key) {
        long value = mPreferences.getLong(CURR_USER, -1);
        return value == -1 ? null : value;
    }

    public void setFirstLaunch(boolean isFirstLaunch) {
        mEditor.putBoolean(FIRST_LAUNCH, isFirstLaunch);
        mEditor.commit();
    }
    public boolean isFirstLaunch() {
        return mPreferences.getBoolean(FIRST_LAUNCH, true);
    }

    public void setUser(Long user) {
        setLong(CURR_USER, user);
    }
    public Long getUser() {
        return getLong(CURR_USER);
    }

}
