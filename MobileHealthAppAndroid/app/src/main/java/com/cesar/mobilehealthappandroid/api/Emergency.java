package com.cesar.mobilehealthappandroid.api;

import com.google.gson.annotations.Expose;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * Created by cesar on 28/05/17.
 */

public class Emergency {
    int id;
    @Expose
    int status;
    @Expose
    String date_time;
    @Expose
    int user;

    public Emergency(int status, int user) {
        this.date_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(GregorianCalendar.getInstance().getTime());
        this.status = status;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }
}
