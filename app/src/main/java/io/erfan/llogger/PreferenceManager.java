package io.erfan.llogger;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private Context mContext;

    // shared pref mode
    private static final String PREF_NAME = "llogger.erfan.io";
    private static final String FIRST_LAUNCH = "firstLaunch";

    public PreferenceManager(Context context) {
        mContext = context;
        mPreferences = context.getSharedPreferences(PREF_NAME, 0);
        mEditor = mPreferences.edit();
    }

    public void setFirstLaunch(boolean isFirstLaunch) {
        mEditor.putBoolean(FIRST_LAUNCH, isFirstLaunch);
        mEditor.commit();
    }

    public boolean isFirstLaunch() {
        return mPreferences.getBoolean(FIRST_LAUNCH, true);
    }
}
