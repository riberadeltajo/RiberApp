package com.chiri.riberappprototype.preferences;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.chiri.riberappprototype.R;

/**
 * Created by Chiri on 06/02/2016.
 */
public class PreferencesLocationFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_location);
    }

}
