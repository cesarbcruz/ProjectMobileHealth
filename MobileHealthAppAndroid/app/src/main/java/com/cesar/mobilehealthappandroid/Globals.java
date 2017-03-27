package com.cesar.mobilehealthappandroid;

/**
 * Created by cesar on 27/03/17.
 */

public class Globals {


    private static Globals instance = new Globals();

    // Getter-Setters
    public static Globals getInstance() {
        return instance;
    }

    public static void setInstance(Globals instance) {
        Globals.instance = instance;
    }

    private Message messageSelected;


    private Globals() {

    }


    public Message getMessageSelected() {
        return messageSelected;
    }

    public void setMessageSelected(Message messageSelected) {
        this.messageSelected = messageSelected;
    }
}
