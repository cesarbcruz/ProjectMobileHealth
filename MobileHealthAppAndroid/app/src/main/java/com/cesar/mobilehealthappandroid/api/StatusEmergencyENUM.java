package com.cesar.mobilehealthappandroid.api;

/**
 * Created by cesar on 28/05/17.
 */

public enum StatusEmergencyENUM {

    PENDING(0),
    PROGRESS(1),
    DONE(2),
    CANCEL(3);

    private int id;
    StatusEmergencyENUM(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
