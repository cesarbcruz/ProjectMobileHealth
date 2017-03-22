package com.cesar.mobilehealthappandroid.sdk.listeners.model;

public class Protocol {
    public static final byte[] PAIR = {2};

    public static final byte[] UNKNOWN_1 = {15, 90, 0, -32, 7, 10, 21, 12, 13, 9, 36, -32, 7, 10, 30, 4, 28, 5, 36, 99};
    public static final byte[] UNKNOWN_2 = {39, 0, 49, 0, 0, 0, -12, 1};
    public static final byte[] UNKNOWN_3 = {3, 1};

    public static final byte[] ALERT_MESSAGE = {1, 1};
    public static final byte[] ALERT_CALL = {2, 2};
    public static final byte[] ALERT_NORMAL = {3};
    public static final byte[] ALERT_STOP = {0};

    public static final byte[] DISCOVERABLE_ON = {6, 1, 0, 1};
    public static final byte[] DISCOVERABLE_OFF = {6, 1, 0, 0};

    public static final byte[] BAND_LOCATION_LEFT = {32, 0, 0, 2};
    public static final byte[] BAND_LOCATION_RIGHT = {32, 0, 0, -126};

    public static final byte[] TIME_ONLY = {6, 10, 0, 0};
    public static final byte[] TIME_DATE = {6, 10, 0, 3};

    public static final byte[] LIFT_WRIST_ON = {6, 5, 0, 1};
    public static final byte[] LIFT_WRIST_OFF = {6, 5, 0, 0};

    public static final byte[] HEART_RATE_SLEEP_ASSISTANT_ON = {21, 0, 1};
    public static final byte[] HEART_RATE_SLEEP_ASSISTANT_OFF = {21, 0, 0};

    public static final byte[] ENABLE_REALTIME_STEPS_NOTIFY = {3, 1};
    public static final byte[] DISABLE_REALTIME_STEPS_NOTIFY = {3, 0};
    public static final byte[] ENABLE_SENSOR_DATA_NOTIFY = {18, 1};
    public static final byte[] DISABLE_SENSOR_DATA_NOTIFY = {18, 0};
    public static final byte[] SET_COLOR_RED = {14, 6, 1, 2, 1};
    public static final byte[] SET_COLOR_BLUE = {14, 0, 6, 6, 1};
    public static final byte[] SET_COLOR_ORANGE = {14, 6, 2, 0, 1};
    public static final byte[] SET_COLOR_GREEN = {14, 4, 5, 0, 1};
    public static final byte[] START_HEARTRATE_SCAN = {21, 2, 1};
    public static final byte[] STOP_HEARTRATE_SCAN = {21, 2, 0};

    public static final byte[] REBOOT = {12};
    public static final byte[] REMOTE_DISCONNECT = {1};
    public static final byte[] FACTORY_RESET = {9};
    public static final byte[] SELF_TEST = {2};
}
