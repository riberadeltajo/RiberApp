package com.chiri.riberappprototype.preferences;

import android.preference.PreferenceActivity;

import com.chiri.riberappprototype.R;

import java.util.List;

/**
 * Created by Chiri on 06/02/2016.
 */
public class Preferences extends PreferenceActivity {

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers,target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {

        return fragmentName.equals("com.chiri.riberappprototype.preferences.PreferencesLocationFragment");
    }

}
