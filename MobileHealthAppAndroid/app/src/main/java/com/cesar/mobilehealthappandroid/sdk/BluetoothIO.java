package com.cesar.mobilehealthappandroid.sdk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.cesar.mobilehealthappandroid.sdk.listeners.NotifyListener;
import com.cesar.mobilehealthappandroid.sdk.listeners.model.Profile;
import com.cesar.mobilehealthappandroid.sdk.listeners.model.Protocol;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.cesar.mobilehealthappandroid.DeviceScanActivity.log;

public class BluetoothIO extends BluetoothGattCallback {
    private static final String TAG = "BluetoothIO";
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    BluetoothGatt mBluetoothGatt;

    private final String message_error_default="Verifique se o bluetooth está ativo e sua pulseira está conectada";

    ActionCallback mCurrentCallback;
    Context mContext;

    HashMap<UUID, NotifyListener> notifyListeners = new HashMap<UUID, NotifyListener>();
    NotifyListener disconnectedListener = null;

    private boolean isConnected;
    private BluetoothGattCharacteristic mCharBatteryInfo;

    public BluetoothIO(Context c) {
        mContext = c;
        initialize();
    }
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                log('e', TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            log('e', TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(String address, final ActionCallback callback) {
        isConnected = true;
        BluetoothIO.this.mCurrentCallback = callback;

        if (mBluetoothAdapter == null || address == null) {
            log('w', TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            log('d', TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            log('w', TAG, "Device not found.  Unable to connect.");
            return false;
        }

        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(mContext, false, BluetoothIO.this);
        log('d', TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        isConnected = false;
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            log('w', TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        isConnected = false;
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public void setDisconnectedListener(NotifyListener disconnectedListener) {
        this.disconnectedListener = disconnectedListener;
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }

    public BluetoothDevice getDevice() {
        if (null == mBluetoothGatt) {
            log('e', TAG, message_error_default);
            return null;
        }
        return mBluetoothGatt.getDevice();
    }

    public void writeAndRead(final UUID uuid, byte[] valueToWrite, final ActionCallback callback) {
        ActionCallback readCallback = new ActionCallback() {

            @Override
            public void onSuccess(Object characteristic) {
                BluetoothIO.this.readCharacteristic(uuid, callback);
            }

            @Override
            public void onFail(int errorCode, String msg) {
                callback.onFail(errorCode, msg);
            }
        };
        this.writeCharacteristic(uuid, valueToWrite, readCallback);
    }

    public void writeCharacteristic(UUID characteristicUUID, byte[] value, ActionCallback callback) {
        writeCharacteristic(Profile.UUID_SERVICE_MIBAND, characteristicUUID, value, callback);
    }

    public void writeCharacteristic(UUID serviceUUID, UUID characteristicUUID, byte[] value, ActionCallback callback) {
//        log('d', TAG, "writeCharacteristic() - serviceUUID : "+serviceUUID+", characteristicUUID="+characteristicUUID+" value : "+ Arrays.toString(value));
        try {
            if (null == mBluetoothGatt) {
                log('e', TAG, message_error_default);
                throw new Exception(message_error_default);
            }
            this.mCurrentCallback = callback;

            BluetoothGattCharacteristic chara = mBluetoothGatt.getService(serviceUUID).getCharacteristic(characteristicUUID);
            if (null == chara) {
                this.onFail(-1, "BluetoothGattCharacteristic " + characteristicUUID + " is not exsit");
                return;
            }
            chara.setValue(value);
            if (false == this.mBluetoothGatt.writeCharacteristic(chara)) {
                this.onFail(-1, "mBluetoothGatt.writeCharacteristic() return false");
            }
        } catch (Throwable tr) {
            log('e', TAG, "writeCharacteristic", tr);
            this.onFail(-1, tr.getMessage());
        }
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            log('w', TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    public void readCharacteristic(UUID serviceUUID, UUID characteristicUUID, ActionCallback callback) {
//        log('d', TAG, "readCharacteristic() - serviceUUID : "+serviceUUID+", characteristicUUID="+characteristicUUID);
        try {
            if (null == mBluetoothGatt) {
                log('e', TAG, message_error_default);
                throw new Exception(message_error_default);
            }
            this.mCurrentCallback = callback;
            BluetoothGattCharacteristic chara = mBluetoothGatt.getService(serviceUUID).getCharacteristic(characteristicUUID);
            if (null == chara) {
                this.onFail(-1, "BluetoothGattCharacteristic " + characteristicUUID + " is not exist");
                return;
            }
            if (false == this.mBluetoothGatt.readCharacteristic(chara)) {
                this.onFail(-1, "mBluetoothGatt.readCharacteristic() return false");
            }

        } catch (Throwable tr) {
            log('e', TAG, "readCharacteristic", tr);
            this.onFail(-1, tr.getMessage());
        }
    }

    private void listaUUID(){
        List<BluetoothGattService> services = getSupportedGattServices();
        for (BluetoothGattService service : services) {
            System.out.println(" * "+service.getUuid());
            for (BluetoothGattCharacteristic caracteristic :service.getCharacteristics()) {
                System.out.println("   --- "+caracteristic.getUuid()+ " === "+Arrays.toString(caracteristic.getValue()));
            }
        }
    }

    public void readCharacteristic(UUID characteristicUUID, ActionCallback callback) {
        this.readCharacteristic(Profile.UUID_SERVICE_MIBAND, characteristicUUID, callback);
    }

    public void readRssi(ActionCallback callback) {
        try {
            if (null == mBluetoothGatt) {
                log('e', TAG, message_error_default);
                throw new Exception(message_error_default);
            }
            this.mCurrentCallback = callback;
            this.mBluetoothGatt.readRemoteRssi();
        } catch (Throwable tr) {
            log('e', TAG, "readRssi", tr);
            this.onFail(-1, tr.getMessage());
        }

    }

    public void setCharacteristicNotification(UUID serviceUUID, UUID characteristicId,
                                              boolean enabled) {
        if (null == mBluetoothGatt) {
            log('e', TAG, message_error_default);
            return;
        }

        BluetoothGattCharacteristic chara = mBluetoothGatt.getService(serviceUUID).getCharacteristic(characteristicId);
        if (chara == null) {
            log('e', TAG, "characteristicId " + characteristicId.toString() + " not found in service " + serviceUUID.toString());
            return;
        }
        setCharacteristicNotification(chara, enabled);
    }
    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        log('d', TAG, "setCharacteristicNotification() - characteristicUUID : "+characteristic.getUuid().toString()+" enable : "+enabled);
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            log('w', TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        // This is specific to Heart Rate Measurement.
        if (Profile.UUID_CHAR_HEART_RATE_NOTIFICATION.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(Profile.UUID_DESCRIPTOR_UPDATE_NOTIFICATION);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    public void setNotifyListener(UUID serviceUUID, UUID characteristicId, NotifyListener listener) {
        log('d', TAG, "setNotifyListener() - serviceUUID : "+serviceUUID+" characteristicUUID : "+characteristicId.toString()+" enable : true");
        if (null == mBluetoothGatt) {
            log('e', TAG, message_error_default);
            return;
        }

        BluetoothGattCharacteristic chara = mBluetoothGatt.getService(serviceUUID).getCharacteristic(characteristicId);
        if (chara == null) {
            log('e', TAG, "characteristicId " + characteristicId.toString() + " not found in service " + serviceUUID.toString());
            return;
        }


        this.mBluetoothGatt.setCharacteristicNotification(chara, true);
        BluetoothGattDescriptor descriptor = chara.getDescriptor(Profile.UUID_DESCRIPTOR_UPDATE_NOTIFICATION);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        this.mBluetoothGatt.writeDescriptor(descriptor);
        this.notifyListeners.put(characteristicId, listener);
    }


    /*
        BluetoothGattCallback
        // Implements callback methods for GATT events that the app cares about.  For example,
        // connection change and services discovered.
     */
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);

        String intentAction;
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            intentAction = ACTION_GATT_CONNECTED;
            mConnectionState = STATE_CONNECTED;
            broadcastUpdate(intentAction);
            log('i', TAG, "Connected to GATT server.");

            // Attempts to discover services after successful connection.
            log('i', TAG, "Attempting to start service discovery:" +
                    gatt.discoverServices());

        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            intentAction = ACTION_GATT_DISCONNECTED;
            mConnectionState = STATE_DISCONNECTED;
            log('i', TAG, "Disconnected from GATT server.");
            broadcastUpdate(intentAction);
//            이녀석 때문에 disconnect 하고 나서 다시 connect 가 안됨 gatt server 를 close 시켜놔서
//            gatt.close();
            if (this.disconnectedListener != null)
                this.disconnectedListener.onNotify(null);
        }

    }

    private void setConnection() {
        writeCharacteristic(Profile.UUID_SERVICE_MIBAND, Profile.UUID_CHAR_BATTERY_INFO, Protocol.UNKNOWN_1, null);
        writeCharacteristic(Profile.UUID_SERVICE_GENERIC_ACCESS, Profile.UUID_CHAR_PERIPHERAL_PREFERRED_CONNECTION_PRAMETERS, Protocol.UNKNOWN_2, null);
        setCharacteristicNotification(Profile.UUID_SERVICE_MIBAND, Profile.UUID_CHAR_DISPLAY_SETTINGS, true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isConnected) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    writeCharacteristic(Profile.UUID_SERVICE_MIBAND, Profile.UUID_CHAR_DISPLAY_SETTINGS, Protocol.UNKNOWN_3, null);
                }
            }
        }).start();
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (characteristic.getUuid().equals(mCharBatteryInfo.getUuid())) {
            int i = characteristic.getValue()[1];
            final String str = "Bateria da pulseira: " + (i & 0xFF) + "%";
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
                }
            });
        }
        super.onCharacteristicRead(gatt, characteristic, status);
        log('d', TAG, "onCharacteristicRead() - serviceUUID : "+characteristic.getService().getUuid().toString()+", characteristicUUID : "+characteristic.getUuid().toString()+" value : "+ Arrays.toString(characteristic.getValue()));
        if (BluetoothGatt.GATT_SUCCESS == status) {
            log('i', TAG, "onCharacteristicRead success");
            this.onSuccess(characteristic);
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);

        } else {
            log('i', TAG, "onCharacteristicRead fail");
            this.onFail(status, "onCharacteristicRead fail");
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        log('d', TAG, "onCharacteristicWrite() - serviceUUID : "+characteristic.getService().getUuid().toString()+", characteristicUUID : "+characteristic.getUuid().toString()+" value : "+ Arrays.toString(characteristic.getValue()));
        if (BluetoothGatt.GATT_SUCCESS == status) {
            log('i', TAG, "onCharacteristicWrite success");
            this.onSuccess(characteristic);
        } else {
            log('i', TAG, "onCharacteristicWrite fail");
            this.onFail(status, "onCharacteristicWrite fail");
        }
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        super.onReadRemoteRssi(gatt, rssi, status);
        if (BluetoothGatt.GATT_SUCCESS == status) {
            log('i', TAG, "onReadRemoteRssi success");
            this.onSuccess(rssi);
        } else {
            log('i', TAG, "onReadRemoteRssi fail");
            this.onFail(status, "onCharacteristicRead fail");
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            log('i', TAG, "onServicesDiscovered success");
            this.onSuccess(null);
            broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);

//            setConnection();

        } else {
            log('i', TAG, "onServicesDiscovered fail");
            this.onFail(status, "onServicesDiscovered fail");
            log('w', TAG, "onServicesDiscovered received: " + status);
        }
        BluetoothGattService bluetoothGattService = gatt.getService(Profile.UUID_SERVICE_MIBAND);
        mCharBatteryInfo = bluetoothGattService.getCharacteristic(Profile.UUID_CHAR_BATTERY_INFO);
        gatt.readCharacteristic(mCharBatteryInfo);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        log('d', TAG, "onCharacteristicChanged() - serviceUUID : "+characteristic.getService().getUuid().toString()+", characteristicUUID : "+characteristic.getUuid().toString()+" value : "+ Arrays.toString(characteristic.getValue()));
        if (this.notifyListeners.containsKey(characteristic.getUuid())) {
            this.notifyListeners.get(characteristic.getUuid()).onNotify(characteristic.getValue());
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);

    }

    /*
            ActionCallback
         */
    private void onSuccess(Object data) {
        if (this.mCurrentCallback != null) {
            ActionCallback callback = this.mCurrentCallback;
            this.mCurrentCallback = null;
            callback.onSuccess(data);
        }
    }

    private void onFail(int errorCode, String msg) {
        if (this.mCurrentCallback != null) {
            ActionCallback callback = this.mCurrentCallback;
            this.mCurrentCallback = null;
            callback.onFail(errorCode, msg);
        }
    }

    /*
        Broadcasting
     */

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        mContext.sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        if (Profile.UUID_CHAR_HEART_RATE_NOTIFICATION.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                log('d', TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                log('d', TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            log('d', TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            }
        }
        mContext.sendBroadcast(intent);

    }
}
