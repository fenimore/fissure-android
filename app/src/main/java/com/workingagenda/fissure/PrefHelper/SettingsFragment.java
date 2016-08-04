package com.workingagenda.fissure.PrefHelper;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.workingagenda.fissure.PrefHelper.NumberPickerPreference;
import com.workingagenda.fissure.R;

/**
 * Created by fen on 8/3/16.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private CheckBoxPreference mCheckBox;
    private EditTextPreference mEditText;
    private NumberPickerPreference mNumberPicker;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        //getDelegate().installViewFactory();
        //getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
        mCheckBox = (CheckBoxPreference) findPreference("pref_repeat");
        mEditText = (EditTextPreference) findPreference("pre_default_title");
        mNumberPicker = (NumberPickerPreference) findPreference("pref_compression");
        //mNumberPicker.setSummary(mNumberPicker.get);
        



    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }


}