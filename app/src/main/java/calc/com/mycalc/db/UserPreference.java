package calc.com.mycalc.db;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreference {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;


    private final static String PREF_NAME = "MyCalc";
    public final static String TOKEN = "TOKEN";
    public final static String IS_LOGGED_IN = "IS_LOGGED_IN";
    public final static String USER_ID = "USER_ID";

    public UserPreference(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void storeValue(String key, String val) {
        preferences.edit().putString(key, val).apply();
    }

    public void storeValue(String key, boolean val) {
        preferences.edit().putBoolean(key, val).apply();
    }

    public void clear() {
        preferences.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(IS_LOGGED_IN, false);
    }
}
