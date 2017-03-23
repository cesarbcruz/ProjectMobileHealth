package com.cesar.mobilehealthappandroid.basicsyncadapter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.cesar.mobilehealthappandroid.R;

/**
 * Activity for holding EntryListFragment.
 */
public class EntryListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_list);
    }
}
