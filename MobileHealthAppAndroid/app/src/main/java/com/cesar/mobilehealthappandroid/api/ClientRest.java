package com.cesar.mobilehealthappandroid.api;

import android.os.AsyncTask;

import com.google.gson.GsonBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by cesar on 03/03/17.
 */
public class ClientRest  extends AsyncTask<Monitoring, Void, String> {

    private static final Logger LOG = LoggerFactory.getLogger(ClientRest.class);
    @Override
    protected String doInBackground(Monitoring... params) {
        String json = new GsonBuilder().create().toJson(params[0], Monitoring.class);
        System.out.println(json);
        try{
            LOG.debug(makeRequest("http://mobilehealthweb.herokuapp.com/api/monitoramento/?format=api", json));
        }catch(Exception ex){
            LOG.error(ex.getMessage(), ex);
        }
        return null;
    }

    private String makeRequest(String uri, String json) throws Exception {
            URL url = new URL(uri);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
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
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
    }
}
