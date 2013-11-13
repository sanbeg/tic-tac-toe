package io.github.sanbeg.ttt;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Prefs extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	};
	
	/**
	 * Get the value of the single player preference
	 * @param context the base context
	 * @return true if we should auto place moves
	 */
	public static boolean getSP(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context).
				getBoolean("sp", true);
	}
	public static boolean isEasy(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).
				getBoolean("easy", false);
		
	}
}
