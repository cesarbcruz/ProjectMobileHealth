package com.cesar.mobilehealthappandroid;

import java.util.Date;

/**
 * Created by cesar on 23/03/17.
 */

public class Message {
    private Date date;
    private String sender;
    private String msg;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
