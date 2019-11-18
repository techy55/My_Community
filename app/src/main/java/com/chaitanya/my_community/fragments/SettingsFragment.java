package com.chaitanya.my_community.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.chaitanya.my_community.R;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

    public class SettingsFragment extends PreferenceFragmentCompat implements androidx.preference.Preference.OnPreferenceChangeListener
    {
        private SwitchPreference annoc, dues, events, security;
        private SharedPreferences prefs;
        private Toolbar toolbar;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);
            this.prefs = Objects.requireNonNull(getActivity()).getSharedPreferences("settings", Context.MODE_PRIVATE);

            annoc = (SwitchPreference) findPreference(getString(R.string.announcements));
            security = (SwitchPreference) findPreference(getString(R.string.security));
            events = (SwitchPreference) findPreference(getString(R.string.events));
            dues = (SwitchPreference) findPreference(getString(R.string.dues));

            annoc.setOnPreferenceChangeListener(this);
            security.setOnPreferenceChangeListener(this);
            dues.setOnPreferenceChangeListener(this);
            events.setOnPreferenceChangeListener(this);
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        }

        @Override
        public boolean onPreferenceChange(androidx.preference.Preference preference, Object newValue) {
            boolean key_switch;
            FirebaseMessaging fcm= FirebaseMessaging.getInstance();
            SharedPreferences.Editor editor;
            if (preference == annoc) {
                key_switch = (boolean) newValue;
                editor = prefs.edit();
                editor.putBoolean("assoc_noti", key_switch);
                editor.apply();
                if(key_switch)
                    fcm.subscribeToTopic("Announcements");
                else
                    fcm.unsubscribeFromTopic("Announcements");
                return true;
            } else if (preference == events) {
                key_switch = (boolean) newValue;
                editor = prefs.edit();
                editor.putBoolean("events_noti", key_switch);
                editor.apply();
                if(key_switch)
                    fcm.subscribeToTopic("Events");
                else
                    fcm.unsubscribeFromTopic("Events");
                return true;
            } else if (preference == security) {
                key_switch = (boolean) newValue;
                editor = prefs.edit();
                editor.putBoolean("secuirty_noti", key_switch);
                editor.apply();
                if(key_switch)
                    fcm.subscribeToTopic("Secuirty");
                else
                    fcm.unsubscribeFromTopic("Secuirty");
                return true;
            } else if (preference == dues) {
                key_switch = (boolean) newValue;
                editor = prefs.edit();
                editor.putBoolean("dues_noti", key_switch);
                editor.apply();
                if(key_switch)
                    fcm.subscribeToTopic("Dues");
                else
                    fcm.unsubscribeFromTopic("Dues");
                return true;
            }
            return false;
        }
    }
//}
