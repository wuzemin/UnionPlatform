package com.min.smalltalk;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Min on 2016/11/24.
 */

public class SharedPreferencesContext {
    private static SharedPreferencesContext mSharedPreferencesContext;
    public Context mContext;
    private SharedPreferences mPreferences;

    public static void init(Context context) {
        mSharedPreferencesContext = new SharedPreferencesContext(context);
    }
    public static SharedPreferencesContext getInstance() {

        if (mSharedPreferencesContext == null) {
            mSharedPreferencesContext = new SharedPreferencesContext();
        }
        return mSharedPreferencesContext;
    }

    private SharedPreferencesContext() {

    }
    private SharedPreferencesContext(Context context) {
        mContext = context;
        mSharedPreferencesContext = this;

        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public SharedPreferences getSharedPreferences() {
        return mPreferences;
    }
}
