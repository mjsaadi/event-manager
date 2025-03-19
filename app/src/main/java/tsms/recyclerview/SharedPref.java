package tsms.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by shams on 9/16/2017.
 */

public class SharedPref{

    //the constants
    private static final String SHARED_PREF_NAME = "tsmssharedpref";
    private static final String KEY_LOGIN_CODE = "keylogincode";
    private static final String KEY_EVENT_CODE = "keyevent";
    private static final String KEY_ID = "keyid";

    private static SharedPref mInstance;
    private static Context mCtx;

    private SharedPref(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPref getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPref(context);
        }
        return mInstance;
    }

    //method to let the user login
    //this method will store the user data in shared preferences
    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ID, user.getId());
        editor.putString(KEY_LOGIN_CODE, user.getLoginCode());
        editor.putString(KEY_EVENT_CODE, user.getEvent());
        editor.apply();
    }

    //this method will checker whether user is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_LOGIN_CODE, null) != null;
    }

    //this method will give the logged in user
    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getString(KEY_ID, null),
                sharedPreferences.getString(KEY_LOGIN_CODE, null),
                sharedPreferences.getString(KEY_EVENT_CODE, null));
    }

    //this method will logout the user
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        mCtx.startActivity(new Intent(mCtx, LoginActivity.class));
    }
}
