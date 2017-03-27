package com.cesar.mobilehealthappandroid;

import android.graphics.Bitmap;

import java.sql.Timestamp;

/**
 * Created by cesar on 23/03/17.
 */

public class Message {

    private int id;
    private int issuer;
    private int recipient;
    private Timestamp date_time;
    private String subject;
    private String msg;
    private String issuer_name;
    private String issuer_img;
    private Bitmap img;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIssuer() {
        return issuer;
    }

    public void setIssuer(int issuer) {
        this.issuer = issuer;
    }

    public int getRecipient() {
        return recipient;
    }

    public void setRecipient(int recipient) {
        this.recipient = recipient;
    }

    public Timestamp getDate_time() {
        return date_time;
    }

    public void setDate_time(Timestamp date_time) {
        this.date_time = date_time;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getIssuer_name() {
        return issuer_name;
    }

    public void setIssuer_name(String issuer_name) {
        this.issuer_name = issuer_name;
    }

    public String getIssuer_img() {
        return issuer_img;
    }

    public void setIssuer_img(String issuer_img) {
        this.issuer_img = issuer_img;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }
}
