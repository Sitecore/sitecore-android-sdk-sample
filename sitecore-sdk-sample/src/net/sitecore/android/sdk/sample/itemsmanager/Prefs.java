package net.sitecore.android.sdk.sample.itemsmanager;

import android.content.Context;
import android.content.SharedPreferences;

import net.sitecore.android.sdk.api.ScPublicKey;
import net.sitecore.android.sdk.sample.R;

public class Prefs {

    private static final String PREFS_NAME = "net.sitecore.android.itemsmanager_preferences";

    private SharedPreferences mPreferences;
    private Context mContext;

    private Prefs(Context context) {
        mPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mContext = context;
    }

    public static Prefs from(Context context) {
        return new Prefs(context);
    }

    public String getString(int keyResourceId) {
        return getString(keyResourceId, "");
    }

    public String getString(int keyResourceId, String defaultText) {
        final String key = mContext.getString(keyResourceId);
        return mPreferences.getString(key, defaultText);
    }

    public boolean getBool(int keyResourceId, boolean defaultValue) {
        final String key = mContext.getString(keyResourceId);
        return mPreferences.getBoolean(key, defaultValue);
    }

    public void put(int stringResourceId, String value) {
        String key = mContext.getString(stringResourceId);
        mPreferences.edit().putString(key, value).commit();
    }

    public void put(int stringResourceId, boolean value) {
        String key = mContext.getString(stringResourceId);
        mPreferences.edit().putBoolean(key, value).commit();
    }

    public boolean isFirstLaunch() {
        return getBool(R.string.key_first_launch, true);
    }

    public String getLogin() {
        return getString(R.string.key_login);
    }

    public String getPassword() {
        return getString(R.string.key_password);
    }

    public String getUrl() {
        return getString(R.string.key_url, mContext.getString(R.string.default_server));
    }

    public boolean isAuth() {
        return getBool(R.string.key_is_auth, false);
    }

    public String getPublicKeyValue() {
        return getString(R.string.key_public_key, null);
    }

    public void savePublicKey(ScPublicKey key) {
        put(R.string.key_public_key, key.getRawValue());
    }
}
