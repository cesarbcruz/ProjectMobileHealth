package com.cesar.mobilehealthappandroid;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;

/**
 * Created by cesar on 27/03/17.
 */

public class Globals {


    private static Globals instance = new Globals();

    public static Globals getInstance() {
        return instance;
    }

    public static void setInstance(Globals instance) {
        Globals.instance = instance;
    }

    private Message messageSelected;
    private int heart_rate;
    private int idUser = 1;
    private int minuteSync;


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
}
