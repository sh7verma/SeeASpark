package utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Utils {
	Context mActivity;
	SharedPreferences preferences;

	public Utils(Context activity) {
		mActivity = activity;
		preferences = activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE);
	}

	public void setString(String key, String value) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public String getString(String key, String defaultValue) {
		return preferences.getString(key, defaultValue);
	}

	public void setBoolean(String key, Boolean value) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public boolean getBoolean(String key, Boolean defaultValue) {
		return preferences.getBoolean(key, defaultValue);
	}

	public void setInt(String key, int value) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public int getInt(String key, int defaultValue) {
		return preferences.getInt(key, defaultValue);
	}

	public void clear_shf() {
		preferences.edit().clear().commit();
	}

    @NotNull
    public Object Connection_Detector(@Nullable Context mContext) {
        return null;
    }
}
