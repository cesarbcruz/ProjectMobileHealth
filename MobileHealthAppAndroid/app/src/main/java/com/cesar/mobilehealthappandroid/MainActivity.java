package com.cesar.mobilehealthappandroid;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.cesar.mobilehealthappandroid.api.ClientRest;
import com.cesar.mobilehealthappandroid.api.Monitoring;
import com.cesar.mobilehealthappandroid.api.ObtainGPS;
import com.cesar.mobilehealthappandroid.api.Utils;
import com.cesar.mobilehealthappandroid.basicsyncadapter.EntryListActivity;
import com.cesar.mobilehealthappandroid.pref.PrefsActivity;
import com.cesar.mobilehealthappandroid.sdk.ActionCallback;
import com.cesar.mobilehealthappandroid.sdk.listeners.HeartRateNotifyListener;
import com.cesar.mobilehealthappandroid.sdk.listeners.RealtimeListener;
import com.cesar.mobilehealthappandroid.sync.SyncQrcodeActivity;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.DecoDrawEffect;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.cesar.mobilehealthappandroid.DeviceScanActivity.log;

public class MainActivity extends AppCompatActivity {

    private TextView tvHeartRate;
    private BluetoothLeService mBluetoothLeService;
    private static final String TAG = MainActivity.class.getSimpleName();
    private String mDeviceAddress = "E1:EE:C3:07:10:BA";
    private ActionCallback action;
    private FloatingActionButton buttonEmergency;
    private FloatingActionButton messages;
    private DecoView mDecoView;
    private int mSeriesIndexSteps;
    private ScheduledExecutorService scheduler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDecoView = (DecoView) findViewById(R.id.dynamicArcView);
        buttonEmergency = (FloatingActionButton) findViewById(R.id.emergency);
        buttonEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMonitoring(Globals.getInstance().getHeart_rate(), 100);
                if (Globals.getInstance().isEmergency()) {
                    Globals.getInstance().makeToast(getBaseContext(), "Para cancelar a solicitação de emergência, mantenha o botão pressionado até visualizar a mensagem de confirmação!", Toast.LENGTH_LONG);
                } else {
                    Globals.getInstance().makeToast(getBaseContext(), "Em caso de emergência mantenha o botão pressionado até visualizar a mensagem de confirmação!", Toast.LENGTH_LONG);
                }
            }
        });

        buttonEmergency.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                sendMonitoring(Globals.getInstance().getHeart_rate(), 100);
                changeEmegency();
                updateButtonEmergency();
                vibrate();
                playSound();
                if (Globals.getInstance().isEmergency()) {
                    Globals.getInstance().makeToast(getBaseContext(), "Solicitação de emergência confirmada!", Toast.LENGTH_LONG);
                } else {
                    Globals.getInstance().makeToast(getBaseContext(), "Cancelamento da solicitação de emergência confirmado!", Toast.LENGTH_LONG);
                }
                return true;
            }
        });

        buttonEmergency.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
                        buttonEmergency.startAnimation(animation);
                        break;

                    case MotionEvent.ACTION_UP:
                        buttonEmergency.clearAnimation();
                        break;
                }
                return false;
            }
        });

        messages = (FloatingActionButton) findViewById(R.id.messages);
        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), EntryListActivity.class));
            }
        });

        tvHeartRate = (TextView) findViewById(R.id.txtHeartRate);
        createGraph();
        getValuePreferences();
        verifyLocation();
        connectDevice();
        updateUIState(tvHeartRate, String.valueOf(Globals.getInstance().getHeart_rate()));
        updateButtonEmergency();
        confLabel();
    }

    private void confLabel() {
        ImageView imgSteps = (ImageView) findViewById(R.id.imageSteps);
        imgSteps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Globals.getInstance().makeToast(getBaseContext(), "Passos", Toast.LENGTH_SHORT);
            }
        });
        ImageView imgDistance = (ImageView) findViewById(R.id.imageDistance);
        imgDistance.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Globals.getInstance().makeToast(getBaseContext(), "Distância", Toast.LENGTH_SHORT);
            }
        });
        ImageView imgCalories = (ImageView) findViewById(R.id.imageCalories);
        imgCalories.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Globals.getInstance().makeToast(getBaseContext(), "Calorias", Toast.LENGTH_SHORT);
            }
        });
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
        scheduler =
                Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        mBluetoothLeService.startHeartRateScan(actionCallBack());
                    }
                }, 10, Globals.getInstance().getMinuteSync() * 60, TimeUnit.SECONDS);
    }

    private void syncServer(int heartRate) {
        if (!Globals.getInstance().isConfiguredSsyncUser()) {
            return;
        }
        if (heartRate > 0) {
            sendMonitoring(heartRate, getButtonEmergency());
        } else {
            Globals.getInstance().makeToast(getBaseContext(), Globals.getInstance().MessageDoNotWearMiBand, Toast.LENGTH_LONG);
        }
    }

    private void sendMonitoring(int heartRate, int emergency) {
        ObtainGPS gps = new ObtainGPS(getBaseContext());
        new ClientRest().execute(new Monitoring(Globals.getInstance().getIdUser(), heartRate, gps.getLatitude(), gps.getLongitude(), getTotalSteps(), emergency));
    }

    private int getTotalSteps() {
        return Globals.getInstance().getSteps();
    }

    private ActionCallback actionCallBack() {
        if (action == null) {
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
            mBluetoothLeService.connect(mDeviceAddress, MainActivity.this, actionCallbackConnect);
            Globals.getInstance().setBluetoothLeService(mBluetoothLeService);
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
        int id = item.getItemId();

        if (id == R.id.action_restart) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            return true;
        } else if (id == R.id.action_settings) {
            Intent i = new Intent(this, PrefsActivity.class);
            startActivity(i);
        } else if (id == R.id.action_sync) {
            Intent i = new Intent(this, SyncQrcodeActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            new ExitDialogFragment().show(getFragmentManager(), null);
        } else {
            super.onBackPressed();
        }
    }

    private void updateViewData(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.txtSteps)).setText(Globals.getInstance().getSteps()+" P");
                ((TextView) findViewById(R.id.txtDistance)).setText(Globals.getInstance().getDistance()+ " M");
                ((TextView) findViewById(R.id.txtCalories)).setText(Globals.getInstance().getCalories()+ " Kcal");

                mDecoView.addEvent(new DecoEvent.Builder(Globals.getInstance().getSteps())
                        .setIndex(mSeriesIndexSteps)
                        .setDelay(6250)
                        .build());
            }
        });

    }

    private void getValuePreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Globals.getInstance().setMinuteSync(Integer.parseInt(prefs.getString(Globals.getInstance().ParamMinuteSync, "1")));
        Globals.getInstance().setIdUser(prefs.getInt(Globals.getInstance().ParamIdUser, 0));
        Globals.getInstance().setNameUser(prefs.getString(Globals.getInstance().ParamNameUser, ""));
        Globals.getInstance().setEmergency(prefs.getBoolean(Globals.getInstance().ParamEmergency, false));
        Globals.getInstance().setBattery(prefs.getInt(Globals.getInstance().ParamBattery, 0));
        Globals.getInstance().setSteps(prefs.getInt(Globals.getInstance().ParamSteps, 0));
        Globals.getInstance().setDistance(prefs.getInt(Globals.getInstance().ParamDistance, 0));
        Globals.getInstance().setCalories(prefs.getInt(Globals.getInstance().ParamCalories, 0));

        if (Globals.getInstance().isConfiguredSsyncUser()) {
            if (Globals.getInstance().getNameUser() != null && !Globals.getInstance().getNameUser().isEmpty()) {
                Globals.getInstance().makeToast(getBaseContext(), "Monitorando: " + Globals.getInstance().getNameUser(), Toast.LENGTH_LONG);
            }
        } else {
            Globals.getInstance().makeToast(getBaseContext(), Globals.getInstance().MessageUnconfiguredUserSync, Toast.LENGTH_LONG);
        }

        updateViewData();
    }

    private void changeEmegency() {
        boolean emergency = !Globals.getInstance().isEmergency();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Globals.getInstance().ParamEmergency, emergency);
        editor.commit();
        Globals.getInstance().setEmergency(emergency);
    }

    private void updateButtonEmergency() {
        if (Globals.getInstance().isEmergency()) {
            buttonEmergency.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
        } else {
            buttonEmergency.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A4A4A4")));
        }
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long milliseconds = 100;
        vibrator.vibrate(milliseconds);
    }

    private void playSound() {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.beep);
        mediaPlayer.start();
    }

    public int getButtonEmergency() {
        return 0;
    }

    private RealtimeListener realtimeListener = new RealtimeListener() {
        @Override
        public void onNotify(int battery, int steps, int distance, int calories) {
            Globals.getInstance().setBattery(battery);
            Globals.getInstance().setSteps(steps);
            Globals.getInstance().setDistance(distance);
            Globals.getInstance().setCalories(calories);
            updateData();
        }
    };

    private void updateData() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Globals.getInstance().ParamBattery, Globals.getInstance().getBattery());
        editor.putInt(Globals.getInstance().ParamSteps, Globals.getInstance().getSteps());
        editor.putInt(Globals.getInstance().ParamDistance, Globals.getInstance().getDistance());
        editor.putInt(Globals.getInstance().ParamCalories, Globals.getInstance().getCalories());
        editor.commit();
        updateViewData();
    }

    private HeartRateNotifyListener heartRateListener = new HeartRateNotifyListener() {
        @Override
        public void onNotify(int heartRate) {
            Globals.getInstance().setHeart_rate(heartRate);
            updateUIState(tvHeartRate, String.valueOf(heartRate));
            syncServer(heartRate);
        }
    };


    ActionCallback actionCallbackConnect = new ActionCallback() {
        @Override
        public void onSuccess(Object data) {
            mBluetoothLeService.setHeartRateScanListener(heartRateListener);
            mBluetoothLeService.setRealtimeStepListener(realtimeListener);
            scheduleTask();
        }

        @Override
        public void onFail(int errorCode, String msg) {
            log('e', TAG, "errorCode : " + errorCode + ", msg : " + msg);
        }
    };


    private void createGraph() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFE2E2E2"))
                        .setRange(0, 100, 0)
                        .setInitialVisibility(false)
                        .build();

                int mBackIndex = mDecoView.addSeries(seriesItem);

                mDecoView.executeReset();

                mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                        .setIndex(mBackIndex)
                        .setDuration(2000)
                        .setDelay(1250)
                        .build());

                mDecoView.addEvent(new DecoEvent.Builder(100f).setIndex(mBackIndex).setDelay(3300).build());


                final SeriesItem seriesItemSteps = new SeriesItem.Builder(Color.parseColor("#9ACD32"))
                        .setRange(0, 1000, 0)
                        .setInitialVisibility(false)
                        .build();

                mSeriesIndexSteps = mDecoView.addSeries(seriesItemSteps);

            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(scheduler!=null){
            scheduler.shutdown();
        }

        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
        }
    }
}
