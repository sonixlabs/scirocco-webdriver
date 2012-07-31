package com.android.test.scirocco2.preference;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.android.test.scirocco2.Activator;

public class Scirocco2PreferenceInitializer extends AbstractPreferenceInitializer {

	public static String ANDROID_SDK_LOCATION = "com.android.test.scirocco2.sdkpath";

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(ANDROID_SDK_LOCATION, "");
	}
}
