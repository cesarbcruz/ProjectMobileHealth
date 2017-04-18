package com.cesar.mobilehealthappandroid.sync;

/**
 * Created by cesar on 28/03/17.
 */

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cesar.mobilehealthappandroid.Globals;
import com.cesar.mobilehealthappandroid.MainActivity;
import com.cesar.mobilehealthappandroid.R;

import java.util.regex.Pattern;

import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;

public class SyncQrcodeActivity  extends AppCompatActivity {
        // UI
        private TextView text;

        // QREader
        private SurfaceView mySurfaceView;
        private QREader qrEader;

        @Override
        protected void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.sync_qrcode);

            text = (TextView) findViewById(R.id.code_info);

            final Button stateBtn = (Button) findViewById(R.id.btn_start_stop);
            // change of reader state in dynamic
            stateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    qrEader.stop();
                    onBackPressed();
                }
            });

            stateBtn.setVisibility(View.VISIBLE);

            Button restartbtn = (Button) findViewById(R.id.btn_restart_activity);
            restartbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(SyncQrcodeActivity.this, SyncQrcodeActivity.class));
                    finish();
                }
            });


            // Setup SurfaceView
            // -----------------
            mySurfaceView = (SurfaceView) findViewById(R.id.camera_view);

            // Init QREader
            // ------------
            qrEader = new QREader.Builder(this, mySurfaceView, new QRDataListener() {
                @Override
                public void onDetected(final String data) {
                    Log.d("QREader", "Value : " + data);
                    text.post(new Runnable() {
                        @Override
                        public void run() {
                            String [] dados = data.split(Pattern.quote("|"));
                            if(dados.length==2 && isNumeric(dados[0]) && !isNumeric(dados[1])){
                                qrEader.stop();
                                Globals.getInstance().setIdUser(Integer.parseInt(dados[0]));
                                Globals.getInstance().setNameUser(dados[1]);
                                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SyncQrcodeActivity.this);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putInt(Globals.getInstance().ParamIdUser, Globals.getInstance().getIdUser());
                                editor.putString(Globals.getInstance().ParamNameUser, Globals.getInstance().getNameUser());
                                editor.commit();

                                AlertDialog.Builder builder = new AlertDialog.Builder(SyncQrcodeActivity.this);
                                builder.setMessage("Conta web vinculada: "+Globals.getInstance().getNameUser())
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent mainIntent = new Intent(SyncQrcodeActivity.this, MainActivity.class);
                                                startActivity(mainIntent);
                                                finish();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        }
                    });
                }
            }).facing(QREader.BACK_CAM)
                    .enableAutofocus(true)
                    .height(mySurfaceView.getHeight())
                    .width(mySurfaceView.getWidth())
                    .build();

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);

            }
        }

        public boolean isNumeric(String s){
            String pattern= "^[0-9]*$";
            return s.matches(pattern);
        }

        @Override
        protected void onResume() {
            super.onResume();

            // Init and Start with SurfaceView
            // -------------------------------
            qrEader.initAndStart(mySurfaceView);
        }

        @Override
        protected void onPause() {
            super.onPause();

            // Cleanup in onPause()
            // --------------------
            qrEader.releaseAndCleanup();
        }
    }

