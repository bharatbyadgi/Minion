package networks.focusmind.com.storage;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.util.Map;
import java.util.Set;

public class SharedPreferenceManager implements SharedPreferences {

    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;

    public SharedPreferenceManager(Context context) {
        mSharedPreferences = context.getSharedPreferences("event_pref", Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public void setIsFirstLaunchPreference(boolean isFirstLaunch) {
        mEditor.putBoolean("isFirstLaunch", isFirstLaunch);
        mEditor.commit();
    }

    public boolean getIsFirstLaunchPreference() {
        return mSharedPreferences.getBoolean("isFirstLaunch", true);
    }

    public void setUserSpecificTokenPreference(String usToken) {
        if (null != usToken && usToken.length() > 0) {
            mEditor.putString("ustoken", usToken);
            mEditor.commit();
        }
    }

    public String getUserSpecificTokenPreference() {
        return mSharedPreferences.getString("ustoken", null);
    }

    public void setUserName(String uname) {
        if (null != uname && uname.length() > 0) {
            mEditor.putString("uname", uname);
            mEditor.commit();
        }
    }

    public String getUserName() {
        return mSharedPreferences.getString("uname", null);
    }

    @Override
    public Map<String, ?> getAll() {
        return null;
    }

    @Nullable
    @Override
    public String getString(String key, String defValue) {
        return null;
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return null;
    }

    @Override
    public int getInt(String key, int defValue) {
        return 0;
    }

    @Override
    public long getLong(String key, long defValue) {
        return 0;
    }

    @Override
    public float getFloat(String key, float defValue) {
        return 0;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return false;
    }

    @Override
    public boolean contains(String key) {
        return false;
    }

    @Override
    public Editor edit() {
        return null;
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }
}
