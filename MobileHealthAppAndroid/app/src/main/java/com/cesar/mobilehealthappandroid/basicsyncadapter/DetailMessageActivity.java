package com.cesar.mobilehealthappandroid.basicsyncadapter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.cesar.mobilehealthappandroid.Globals;
import com.cesar.mobilehealthappandroid.R;

/**
 * Activity for holding DetailMessageFragment.
 */
public class DetailMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_message);
        TextView msg = (TextView) findViewById(R.id.detail_msg);
        msg.setText(Globals.getInstance().getMessageSelected().getMsg());
    }
}
