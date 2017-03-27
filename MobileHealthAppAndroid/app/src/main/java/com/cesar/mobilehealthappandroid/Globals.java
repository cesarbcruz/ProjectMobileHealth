package com.cesar.mobilehealthappandroid;

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
}
