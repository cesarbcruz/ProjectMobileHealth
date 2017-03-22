package api;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * Created by cesar on 03/03/17.
 */

public class Monitoring {
    private String date_time;
    private int heart_rate;
    private int user;
    private double latitude;
    private double longitude;
    private int steps;

    public Monitoring(int heart_rate, double latitude, double longitude, int steps) {
        this.date_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(GregorianCalendar.getInstance().getTime());
        this.heart_rate = heart_rate;
        this.user = 1;
        this.latitude = latitude;
        this.longitude = longitude;
        this.steps = steps;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public int getHeart_rate() {
        return heart_rate;
    }

    public void setHeart_rate(int heart_rate) {
        this.heart_rate = heart_rate;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
