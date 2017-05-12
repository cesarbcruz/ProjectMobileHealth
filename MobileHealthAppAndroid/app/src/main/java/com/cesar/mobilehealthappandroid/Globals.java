package com.cesar.mobilehealthappandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.widget.Toast;

/**
 * Created by cesar on 27/03/17.
 */

public class Globals {


    private static Globals instance = new Globals();
    public static final String MessageUnconfiguredUserSync="Nenhuma conta web vinculada!\nAcesse o menu: 'Sincronizar Conta Web'";
    public static final String MessageDoNotWearMiBand = "Aparentemente você não está utilizando a pulseira corretamente!";
    private Message messageSelected;
    private int heart_rate;
    private int idUser = 0;
    private String nameUser = "";
    private int minuteSync;
    public static final String ParamIdUser = "ParamIdUser";
    public static final String ParamNameUser = "ParamNameUser";
    public static final String ParamMinuteSync = "minuteSync";
    public static final String ParamEmergency = "ParamEmergency";
    public static final String UrlUploadDataServer = "http://mobilehealthweb.herokuapp.com/api/monitoramento/?format=api";
    private BluetoothLeService bluetoothLeService;
    private boolean emergency;
    private int battery;
    private int steps;
    private int distance;
    private int calories;


    public static Globals getInstance() {
        return instance;
    }

    public static void setInstance(Globals instance) {
        Globals.instance = instance;
    }

    public boolean isConfiguredSsyncUser(){
        return idUser>0;
    }

    private Globals() {

    }

    public Message getMessageSelected() {
        return messageSelected;
    }

    public void setMessageSelected(Message messageSelected) {
        this.messageSelected = messageSelected;
    }

    public int getHeart_rate() {
        return heart_rate;
    }

    public void setHeart_rate(int heart_rate) {
        this.heart_rate = heart_rate;
    }


    public Bitmap convertBlobToBitmap(byte[] blob) {
        if(blob!=null){
            return BitmapFactory.decodeByteArray(blob, 0, blob.length);
        }else{
            return null;
        }
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getMinuteSync() {
        return minuteSync;
    }

    public void setMinuteSync(int minuteSync) {
        this.minuteSync = minuteSync;
    }

    public String getNameUser() {
        if(nameUser==null){
            return "";
        }
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    @NonNull
    public String getUrlDownloadServer() {
        return "http://mobilehealthweb.herokuapp.com/api/message/?recipient__id="+ idUser+"&format=json";
    }

    @NonNull
    public String getUrlDownloadServer(String date_time) {
        return "http://mobilehealthweb.herokuapp.com/api/message/?date_time__gt="+date_time+"&recipient__id="+ idUser+"&format=json";
    }

    public void makeToast(final Context ctx, final String message, final int length){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ctx, message, length).show();
            }
        });
    }

    public BluetoothLeService getBluetoothLeService() {
        return bluetoothLeService;
    }

    public void setBluetoothLeService(BluetoothLeService bluetoothLeService) {
        this.bluetoothLeService = bluetoothLeService;
    }

    public boolean isEmergency() {
        return emergency;
    }

    public void setEmergency(boolean emergency) {
        this.emergency = emergency;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }
}
