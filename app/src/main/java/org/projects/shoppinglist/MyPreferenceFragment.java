package org.projects.shoppinglist;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

//extending the standard preference fragment
public class MyPreferenceFragment extends PreferenceFragment {

    //These values are specifed in the prefs.xml file
    //and needs to correspond exactly to those in the prefs.xml file
    //You can check the key values in that file and check that it
    //corresponds to the keys defined here.
    private static String SETTINGS_UNIT = "unit";

    //note these are static methods - meaning they always exists
    //so we do not have to create an instance of this class to
    //get the values.

    public static boolean chooseUnit(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SETTINGS_UNIT, true);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //adding the preferences from the xml
        //so this will in fact be the whole view.
        addPreferencesFromResource(R.xml.prefs);
    }
}
