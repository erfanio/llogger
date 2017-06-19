package io.erfan.llogger;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

// PreferenceManager, but that would conflict with android.preference.PreferenceManager
public class PrefMan {
    private final SharedPreferences mPreferences;

    // pref names
    private static final String FIRST_LAUNCH = "firstLaunch";
    private static final String CURR_USER = "currentUser";

    public PrefMan(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    // make Long out of long (store null as -1)
    private void setLong(String key, Long origValue) {
        long value = origValue == null ? -1 : origValue;
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }
    private Long getLong(String key) {
        long value = mPreferences.getLong(key, -1);
        return value == -1 ? null : value;
    }

    public void setFirstLaunch(boolean isFirstLaunch) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(FIRST_LAUNCH, isFirstLaunch);
        editor.apply();
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
