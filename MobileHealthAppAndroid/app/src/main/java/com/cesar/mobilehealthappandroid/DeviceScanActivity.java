package com.cesar.mobilehealthappandroid;

import android.util.Log;

public class DeviceScanActivity {

    public static void log(Character c, String localTag, String msg) {
        log(c, localTag, msg, null);
    }
    public static void log(Character c, String localTag, String msg, Throwable tw) {
        switch (c) {
            case 'd' :
                Log.d("MyBand", localTag +" : "+ msg);
                break;
            case 'w' :
                Log.w("MyBand", localTag +" : "+ msg);
                break;
            case 'i' :
                Log.i("MyBand", localTag +" : "+ msg);
                break;
            case 'e' :
                Log.e("MyBand", localTag +" : "+ msg, tw);
                break;
        }
    }
}
