package com.cesar.mobilehealthappandroid;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.cesar.mobilehealthappandroid.api.ClientRest;
import com.cesar.mobilehealthappandroid.api.Monitoring;
import com.cesar.mobilehealthappandroid.api.ObtainGPS;
import com.cesar.mobilehealthappandroid.api.Utils;
import com.cesar.mobilehealthappandroid.basicsyncadapter.EntryListActivity;
import com.cesar.mobilehealthappandroid.sdk.ActionCallback;
import com.cesar.mobilehealthappandroid.sdk.listeners.HeartRateNotifyListener;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.cesar.mobilehealthappandroid.DeviceScanActivity.log;

public class MainActivity extends AppCompatActivity {

    public static final String PRFFS_ADDRESS = "prefs_address";
    public static final String PRFFS_ADDRESS_KEY = "prefs_address_key";
    private TextView tvHeartRate;
    private BluetoothLeService mBluetoothLeService;
    private static final String TAG = MainActivity.class.getSimpleName();
    private String mDeviceAddress = "E1:EE:C3:07:10:BA";
    private HeartRateNotifyListener listener;
    private ActionCallback action;
    private FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), EntryListActivity.class));
            }
        });

        tvHeartRate = (TextView) findViewById(R.id.heart_rate);
        verifyLocation();
        connectDevice();

    }

    private void verifyLocation() {
        ObtainGPS gps = new ObtainGPS(this);

        if (Utils.getLocalization(this)) {
            // check if GPS enabled
            if (gps.canGetLocation()) {
                Log.d("LOG", "Lat:" + gps.getLatitude() + " Lng:" + gps.getLongitude());
            }
        }
    }

    private void scheduleTask() {

        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        scanHeartRate();
                    }
                }, 30, 60, TimeUnit.SECONDS);
    }

    private void scanHeartRate(){
        if(listener==null){
            listener = new HeartRateNotifyListener() {
                @Override
                public void onNotify(int heartRate) {
                    updateUIState(tvHeartRate, String.valueOf(heartRate));
                    syncServer(heartRate);
                }
            };
            mBluetoothLeService.setHeartRateScanListener(listener);
        }
        mBluetoothLeService.startHeartRateScan(actionCallBack());
    }

    private void syncServer(int heartRate) {
        if(heartRate > 0){
            ObtainGPS gps = new ObtainGPS(getBaseContext());
            new ClientRest().execute(new Monitoring(heartRate, gps.getLatitude(), gps.getLongitude(), getTotalSteps()));
        }else{
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getBaseContext(), "Aparentemente você não está utilizando a pulseira corretamente!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private int getTotalSteps() {
        return 6000;
    }

    private ActionCallback actionCallBack() {
        if(action == null) {
            action = new ActionCallback() {
                @Override
                public void onSuccess(Object data) {
                    BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) data;
                    log('d', TAG, Arrays.toString(characteristic.getValue()));
                }

                @Override
                public void onFail(int errorCode, String msg) {
                    log('e', TAG, "errorCode : " + errorCode + ", msg : " + msg);
                    updateUIState(tvHeartRate, "...");
                }
            };
        }
        return action;
    }

    private void updateUIState(final TextView tv, final String addStr) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(addStr);
            }
        });
    }


    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                log('e', TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress, MainActivity.this);
            scheduleTask();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private void connectDevice() {
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        //mBluetoothLeService.connect(mDeviceAddress, MainActivity.this); // caso queira conectar novamente ao device
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(isTaskRoot()) {
            new ExitDialogFragment().show(getFragmentManager(), null);
        } else {
            super.onBackPressed();
        }
    }
}
