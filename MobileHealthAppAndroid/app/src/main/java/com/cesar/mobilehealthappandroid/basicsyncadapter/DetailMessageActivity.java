package com.cesar.mobilehealthappandroid.basicsyncadapter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.cesar.mobilehealthappandroid.Globals;
import com.cesar.mobilehealthappandroid.Message;
import com.cesar.mobilehealthappandroid.R;

import java.text.SimpleDateFormat;

/**
 * Activity for holding DetailMessageFragment.
 */
public class DetailMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_message);
        ImageView img = (ImageView) findViewById(R.id.icon);
        TextView issuer_name = (TextView) findViewById(R.id.issuer);
        TextView subtitle = (TextView) findViewById(R.id.subtitle);
        TextView msg = (TextView) findViewById(R.id.detail_msg);
        TextView date_time = (TextView) findViewById(R.id.date_time);

        Message message = Globals.getInstance().getMessageSelected();
        if(message!=null){
            img.setImageBitmap(message.getImg());
            issuer_name.setText(message.getIssuer_name());
            subtitle.setText(message.getSubject());
            msg.setText(message.getMsg());
            date_time.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(message.getDate_time()));
        }
    }
}
