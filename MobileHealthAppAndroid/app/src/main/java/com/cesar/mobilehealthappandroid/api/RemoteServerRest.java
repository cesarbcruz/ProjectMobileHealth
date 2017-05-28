package com.cesar.mobilehealthappandroid.api;

import android.util.Log;

import com.cesar.mobilehealthappandroid.Globals;
import com.cesar.mobilehealthappandroid.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cesar on 28/05/17.
 */

public class RemoteServerRest {

    private static final int NET_CONNECT_TIMEOUT_MILLIS = 15000;
    private static final int NET_READ_TIMEOUT_MILLIS = 10000;

    public static void requestEmergency(final Emergency emergency) throws Exception {

        new Thread(new Runnable() {
            public void run() {
                try {

                    GsonBuilder builder = new GsonBuilder();
                    if (emergency.getId() == 0) {
                        builder.excludeFieldsWithoutExposeAnnotation();
                    }
                    String json = builder.create().toJson(emergency, Emergency.class);
                    URL url = new URL(Globals.getInstance().getUrlUploadEmergency(emergency.getId()));
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoOutput(true);
                    if (emergency.getId() == 0) {
                        httpURLConnection.setRequestMethod("POST");
                    } else {
                        httpURLConnection.setRequestMethod("PUT");
                    }
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.connect();
                    DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                    wr.writeBytes(json);
                    wr.flush();
                    wr.close();

                    InputStream is = httpURLConnection.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while ((line = rd.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    rd.close();
                } catch (Exception ex) {
                    Log.e("ERRO", ex.getMessage(), ex);
                }

            }
        }).start();
    }

    public static void cancelEmergency(final int idUser) {
        new Thread(new Runnable() {
            public void run() {
                try {

                    InputStream stream = null;
                    URL url = new URL(Globals.getInstance().getUrlDownloadEmergency(idUser, StatusEmergencyENUM.DONE.getId()));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(NET_READ_TIMEOUT_MILLIS);
                    conn.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();
                    stream = conn.getInputStream();
                    Reader reader = new InputStreamReader(stream, "UTF-8");
                    Type listType = new TypeToken<ArrayList<Emergency>>() {
                    }.getType();
                    List<Emergency> emergencies = new Gson().fromJson(reader, listType);
                    for (Emergency e : emergencies) {
                        e.setStatus(StatusEmergencyENUM.CANCEL.getId());
                        requestEmergency(e);
                    }
                } catch (Exception ex) {
                    Log.e("ERRO", ex.getMessage(), ex);
                }

            }
        }).start();
    }

}