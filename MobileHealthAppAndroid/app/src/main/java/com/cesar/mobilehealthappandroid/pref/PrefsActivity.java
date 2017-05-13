package com.cesar.mobilehealthappandroid.pref;

import android.app.AlertDialog;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.cesar.mobilehealthappandroid.R;

/**
 * Created by cesar on 28/03/17.
 */

public class PrefsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs);
            EditTextPreference textPreference = (EditTextPreference) this.findPreference("minuteSync");
            textPreference.setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            try {
                                int num = Integer.parseInt((String) newValue);
                                if (num >= 1 && num <= 60) {
                                    return true;
                                } else {
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle("Valor inválido");
                                    builder.setMessage("Permitido somente valor numérico de 1 até 60");
                                    builder.setPositiveButton(android.R.string.ok, null);
                                    builder.show();
                                    return false;
                                }
                            } catch (Exception ex) {
                                return false;
                            }
                        }

                    });
            setHasOptionsMenu(true);
        }


        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.menu_detail_message, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_detail_message_previous:
                    getActivity().onBackPressed();
                    return true;
            }
            return super.onOptionsItemSelected(item);
        }

    }
}


