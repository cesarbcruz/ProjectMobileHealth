package com.cesar.mobilehealthappandroid.sdk.listeners.model;

import java.util.UUID;

/*
    Mi band 공식 앱하고 연결하고 나서 write 가 가능함.
 */
public class Profile {
    // ========================== Service ============================

    public static final UUID UUID_SERVICE_GENERIC_ACCESS = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");

    public static final UUID UUID_CHAR_DEVICE_NAME = UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb");

    public static final UUID UUID_CHAR_APPEARANCE = UUID.fromString("00002a01-0000-1000-8000-00805f9b34fb");

    // m f b 2 value=[39, 0, 49, 0, 0, 0, -12, 1]
    public static final UUID UUID_CHAR_PERIPHERAL_PREFERRED_CONNECTION_PRAMETERS = UUID.fromString("00002a04-0000-1000-8000-00805f9b34fb");


    public static final UUID UUID_SERVICE_GENERIC_ATTRIBUTE = UUID.fromString("00001801-0000-1000-8000-00805f9b34fb");

    public static final UUID UUID_CHAR_SERVICE_CHANGED = UUID.fromString("00002a05-0000-1000-8000-00805f9b34fb");


    public static final UUID UUID_SERVICE_DEVICE_INFORMATION = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");

    // 1-4
    // getProperties() mProperties=2
    // setValue() value=[102, 57, 53, 50, 51, 100, 51, 48, 98, 53, 49, 101]
    // getValue() value=[102, 57, 53, 50, 51, 100, 51, 48, 98, 53, 49, 101]
    public static final UUID UUID_CHAR_SERIAL_NUMBER_STRING = UUID.fromString("00002a25-0000-1000-8000-00805f9b34fb");

    // 1-6
    // getProperties() mProperties=2
    // setValue() value=[86, 48, 46, 49, 46, 51, 46, 50]
    // getValue() value=[86, 48, 46, 49, 46, 51, 46, 50]
    public static final UUID UUID_CHAR_HARDWARE_REVISION_STRING = UUID.fromString("00002a27-0000-1000-8000-00805f9b34fb");

    // 1-2
    // getProperties() mProperties=2
    // setValue() value=[86, 49, 46, 48, 46, 48, 46, 53, 51]
    // getValue() value=[86, 49, 46, 48, 46, 48, 46, 53, 51]
    public static final UUID UUID_CHAR_SOFTWARE_REVISION_STRING = UUID.fromString("00002a28-0000-1000-8000-00805f9b34fb");

    // 1-3
    // getProperties() mProperties=2
    // setValue() value=[-9, 105, -32, -1, -2, 75, 85, 32]
    // getValue() value=[-9, 105, -32, -1, -2, 75, 85, 32]
    public static final UUID UUID_CHAR_SYSTEM_ID = UUID.fromString("00002a23-0000-1000-8000-00805f9b34fb");

    // 1-5
    // getProperties() mProperties=2
    // setValue() value=[1, 87, 1, 4, 0, 0, 1]
    // getValue() value=[1, 87, 1, 4, 0, 0, 1]
    public static final UUID UUID_CHAR_PNP_ID = UUID.fromString("00002a50-0000-1000-8000-00805f9b34fb");


    public static final UUID UUID_SERVICE_UNKNOWN3 = UUID.fromString("00001530-0000-3512-2118-0009af100700");

    public static final UUID UUID_CHAR_UNKNOWN10 = UUID.fromString("00001531-0000-3512-2118-0009af100700");

    public static final UUID UUID_CHAR_UNKNOWN11 = UUID.fromString("00001532-0000-3512-2118-0009af100700");


    public static final UUID UUID_SERVICE_ALERT_NOTIFICATION = UUID.fromString("00001811-0000-1000-8000-00805f9b34fb");

    public static final UUID UUID_CHAR_NEW_ALERT = UUID.fromString("00002a46-0000-1000-8000-00805f9b34fb");

    public static final UUID UUID_CHAR_ALERT_NOTIFICATION_CONTROL_POINT = UUID.fromString("00002a44-0000-1000-8000-00805f9b34fb");


    public static final UUID UUID_SERVICE_ALERT = UUID.fromString("00001802-0000-1000-8000-00805f9b34fb");

    /*
        Find band Step

        1. setValue(UUID_CHAR_ALERT) value=[3]

        alert value message [1] call [2] normal [3] stop [0]
     */
    public static final UUID UUID_CHAR_ALERT = UUID.fromString("00002a06-0000-1000-8000-00805f9b34fb");


    public static final UUID UUID_SERVICE_HEARTRATE = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");

    public static final UUID UUID_CHARACTERISTIC_REALTIME_STEPS = UUID.fromString("0000ff06-0000-1000-8000-00805f9b34fb");


        /*
            Heart rate sleep assistant Step

            1. setCharacteristicNotification(UUID_CHAR_HEART_RATE_NOTIFICATION, true)

            2. setValue(UUID_CHAR_HEART_RATE_CONTROL_POINT) value=[21, 0, 1]

//            3. setCharacteristicNotification(UUID_CHAR_HEART_RATE_NOTIFICATION, false)

            turn on value [21, 0, 1] turn off value [21, 0, 0]
         */

        /*
            Heart rate Measurement Step

            1. setCharacteristicNotification(UUID_CHAR_HEART_RATE_NOTIFICATION, true)

            2. setValue(UUID_CHAR_HEART_RATE_CONTROL_POINT) value=[21, 2, 1]

            measuring heart rate...

            3. onCharacteristicChanged(UUID_CHAR_HEART_RATE_NOTIFICATION) value=[0, 85]

            4. setCharacteristicNotification(UUID_CHAR_HEART_RATE_NOTIFICATION, false)

            start value [21, 2, 1] cancel value [21, 2, 0]
         */

    // This characteristic be used to notify measured heart rate after completed the measurement
    public static final UUID UUID_CHAR_HEART_RATE_NOTIFICATION = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");

    // This characteristic be used to measure heart rate
    public static final UUID UUID_CHAR_HEART_RATE_CONTROL_POINT = UUID.fromString("00002a39-0000-1000-8000-00805f9b34fb");

    public static final UUID UUID_UNKNOWN_CHARACTERISTIC4 = UUID.fromString("00000004-0000-3512-2118-0009af100700");
    public static final UUID UUID_CHARACTERISTIC_5_ACTIVITY_DATA = UUID.fromString("00000005-0000-3512-2118-0009af100700");


    public static final UUID UUID_SERVICE_MIBAND = UUID.fromString("0000fee0-0000-1000-8000-00805f9b34fb");

    // 1-1
    // setValue() value=[-32, 7, 10, 27, 17, 49, 4, 5, 0, 0, 36]
    // getProperties() mProperties=26
    // getValue() value=[-32, 7, 10, 27, 17, 49, 4, 5, 0, 0, 36]
    public static final UUID UUID_CHAR_CURRENT_TIME = UUID.fromString("00002a2b-0000-1000-8000-00805f9b34fb");

    public static final UUID UUID_CHAR_UNKNOWN15 = UUID.fromString("00000001-0000-3512-2118-0009af100700");

    public static final UUID UUID_CHAR_UNKNOWN16 = UUID.fromString("00000002-0000-3512-2118-0009af100700");

    // 4. setCharacteristicNotification() true -> false again when connecting miband
    // getProperties() mProperties=20
    // setValue() value=[6, 4, 0, 0]

    // touch display option
    // setCharacteristicNotification() true
    // setValue() value=[6, 4, 0, 0]
    // getValue() value=[6, 4, 0, 0]
    // setValue() value=[16, 6, 4, 0, 1]
    // getValue() value=[16, 6, 4, 0, 1]
    // setCharacteristicNotification() falses

    // touch time format
    // setCharacteristicNotification() true
    // setValue() value=[6, 10, 0, 0]
    // getValue() value=[6, 10, 0, 0]
    // setValue() value=[16, 6, 10, 0, 1]
    // getValue() value=[16, 6, 10, 0, 1]
    // setCharacteristicNotification() falses

    // touch lift wrist screen light
    // setCharacteristicNotification() true
    // setValue() value=[6, 5, 0, 1]
    // getValue() value=[6, 5, 0, 1]
    // setValue() value=[16, 6, 5, 0, 1]
    // getValue() value=[16, 6, 5, 0, 1]
    // setCharacteristicNotification() falses

    // touch alert on
    // setCharacteristicNotification() true
    // setValue() value=[2, 0, 7, 0, 31]
    // getValue() value=[2, 0, 7, 0, 31]
    // setValue() value=[16, 2, 1]
    // getValue() value=[16, 2, 1]
    // setCharacteristicNotification() falses

    // touch alert off
    // setCharacteristicNotification() true
    // setValue() value=[2, -128, 7, 0, 31]
    // getValue() value=[2, -128, 7, 0, 31]
    // setValue() value=[16, 2, 1]
    // getValue() value=[16, 2, 1]
    // setCharacteristicNotification() falses

    // long time seat alert on
    // setCharacteristicNotification() true
    // setValue() value=[8, 1, 60, 0, 8, 0, 21, 0, 0, 0, 0, 0]
    // getValue() value=[8, 1, 60, 0, 8, 0, 21, 0, 0, 0, 0, 0]
    // setValue() value=[16, 8, 1]
    // getValue() value=[16, 8, 1]
    // setCharacteristicNotification() false

    // long time seat alert off
    // setCharacteristicNotification() true
    // setValue() value=[8, 0, 60, 0, 8, 0, 21, 0, 0, 0, 0, 0]
    // getValue() value=[8, 0, 60, 0, 8, 0, 21, 0, 0, 0, 0, 0]
    // setValue() value=[16, 8, 1]
    // getValue() value=[16, 8, 1]
    // setCharacteristicNotification() false

    //    [8, 1, 60, 0, 8, 0, 12, 0, 14, 0, 21, 0]

    // turn on can be searched by around device
    // setCharacteristicNotification() true
    // setValue() value=[6, 1, 0, 1]
    // getValue() value=[6, 1, 0, 1]
    // setValue() value=[16, 6, 1, 0, 1]
    // getValue() value=[16, 6, 1, 0, 1]
    // setCharacteristicNotification() false

    // turn off can be searched by around device
    // setCharacteristicNotification() true
    // setValue() value=[6, 1, 0, 0]
    // getValue() value=[6, 1, 0, 0]
    // setValue() value=[16, 6, 1, 0, 1]
    // getValue() value=[16, 6, 1, 0, 1]
    // setCharacteristicNotification() false

        /*
            Mi Band display settings Step

            1. setCharacteristicNotification(UUID_CHAR_DISPLAY_SETTINGS, true)

            2. setValue(UUID_CHAR_DISPLAY_SETTINGS) value=[6, 4, 0, 0]

            3. setValue(UUID_CHAR_DISPLAY_SETTINGS) value=[16, 6, 4, 0, 1]

            4. setCharacteristicNotification(UUID_CHAR_DISPLAY_SETTINGS, false)

            number of cases
            value [6, 4, 0, 1] [6, 4, 0, 3] [6, 4, 0, 7] [6, 4, 0, 15] [6, 4, 0, 31]
         */

        /*
            Time format Step

            1. setCharacteristicNotification(UUID_CHAR_DISPLAY_SETTINGS, true)

            2. setValue(UUID_CHAR_DISPLAY_SETTINGS) value=[6, 10, 0, 0]

            3. setValue(UUID_CHAR_DISPLAY_SETTINGS) value=[16, 6, 10, 0, 1]

            4. setCharacteristicNotification(UUID_CHAR_DISPLAY_SETTINGS, false)

            value [6, 10, 0, 0] or [6, 10, 0, 3]
         */

        /*
            Lift wrist to view info Step

            1. setCharacteristicNotification(UUID_CHAR_DISPLAY_SETTINGS, true)

            2. setValue(UUID_CHAR_DISPLAY_SETTINGS) value=[6, 5, 0, 1]

            3. setValue(UUID_CHAR_DISPLAY_SETTINGS) value=[16, 6, 5, 0, 1]

            4. setCharacteristicNotification(UUID_CHAR_DISPLAY_SETTINGS, false)

            on [6, 5, 0, 1] off [6, 5, 0, 0]
         */

        /*
            Alarm Step

            1. setCharacteristicNotification(UUID_CHAR_DISPLAY_SETTINGS, true)

            2. setValue(UUID_CHAR_DISPLAY_SETTINGS) value=[2, 0, 7, 0, 31]

            3. setValue(UUID_CHAR_DISPLAY_SETTINGS) value=[16, 2, 1]

            4. setCharacteristicNotification(UUID_CHAR_DISPLAY_SETTINGS, false)

            number of cases
            Once : -128 | Every day : 127 | Mon to Fri : 31
            [2, 0~127(off) | -128~-1(on), hour, minute, repeat]
            off [2, 0, 7, 0, 31] on [2, -128, 7, 0, 31]
            [2, 1, 7, 0, 127] [2, -127, 7, 0, 127]
            [2, 2, 7, 0, -128] [2, -126, 7, 0, -128]
         */

        /*
            Turn on idle alerts Step

            1. setCharacteristicNotification(UUID_CHAR_DISPLAY_SETTINGS, true)

            2. setValue(UUID_CHAR_DISPLAY_SETTINGS) value=[8, 0, 60, 0, 8, 0, 12, 0, 14, 0, 21, 0]

            3. setValue(UUID_CHAR_DISPLAY_SETTINGS) value=[16, 8, 1]

            4. setCharacteristicNotification(UUID_CHAR_DISPLAY_SETTINGS, false)


            [8, off/on(0/1), interval, 0, hour(start), minute, hour(end), minute, hour(start disturb), minute, hour(end), minute]
            [8, off/on(0/1), interval, 0, hour, minute, off/on(21/12), 0, 14, 0, hour, minute]
            off [8, 0, 60, 0, 8, 0, 12, 0, 14, 0, 21, 0] on [8, 1, 60, 0, 8, 0, 12, 0, 14, 0, 21, 0]
         */

        /*
            Goals notifications Step

            1. setCharacteristicNotification(UUID_CHAR_DISPLAY_SETTINGS, true)

            2. setValue(UUID_CHAR_DISPLAY_SETTINGS) value=[6, 6, 0, 0]

            3. setValue(UUID_CHAR_DISPLAY_SETTINGS) value=[16, 6, 6, 0, 1]

            4. setCharacteristicNotification(UUID_CHAR_DISPLAY_SETTINGS, false)

            off [6, 6, 0, 0] on [6, 6, 0, 1]
         */

        /*
            Turn on Do not disturb Step

            1. setCharacteristicNotification(UUID_CHAR_DISPLAY_SETTINGS, true)

            2. setValue(UUID_CHAR_DISPLAY_SETTINGS) value=[9, 1, 22, 0, 7, 0]

            3. setValue(UUID_CHAR_DISPLAY_SETTINGS) value=[16, 9, 1]

            4. setCharacteristicNotification(UUID_CHAR_DISPLAY_SETTINGS, false)

            off [9, 2] on [9, 1, start, 0, end, 0]
         */

        /*
            Turn on Discverable mode Step

            1. setCharacteristicNotification(UUID_CHAR_DISPLAY_SETTINGS, true)

            2. setValue(UUID_CHAR_DISPLAY_SETTINGS) value=[6, 1, 0, 0]

            3. setValue(UUID_CHAR_DISPLAY_SETTINGS) value=[16, 6, 1, 0, 1]

            4. setCharacteristicNotification(UUID_CHAR_DISPLAY_SETTINGS, false)

            off [6, 1, 0, 0] on [6, 1, 0, 1]
         */

    //  setCharacteristicNotification(UUID_CHAR_DISPLAY_SETTINGS, true) -> this is maybe touch listener?
    // m f b 3 value=[3, 1]  [16, 3, 1, 1, 15, -76, 3, 0, 0, 0, 45, 0, -23, 0, 110, 2]
    //setValue() - service uuid=00001802-0000-1000-8000-00805f9b34fb char uuid=00002a06-0000-1000-8000-00805f9b34fb value=[-1, 100, 0, -106, 0, 2]
    //value=[-1, -6, 0, -106, 0, 3]
    //[-1, -6, 0, -106, 0, 3]
    //[-1, 100, 0, 0, 0, 1]
    //=[-1, -6, 0, -106, 0, 3]
    //[-1, 100, 0, -106, 0, 3]
    //[-1, 100, 0, -106, 0, 2]
    //[-1, -6, 0, -106, 0, 3]
    //[-1, -24, 3, 0, 0, 1]

    public static final UUID UUID_CHAR_DISPLAY_SETTINGS = UUID.fromString("00000003-0000-3512-2118-0009af100700");

//        public static final UUID UUID_CHAR_PERIPHERAL_PREFERRED_CONNECTION_PRAMETERS = UUID.fromString("00002a04-0000-1000-8000-00805f9b34fb");

    // 5. setCharacteristicNotification() true -> false again when connecting miband
    // unpair?
    public static final UUID UUID_CHAR_UNKNOWN18 = UUID.fromString("00000004-0000-3512-2118-0009af100700");

    // 6. setCharacteristicNotification() true -> false when connecting miband
    public static final UUID UUID_CHAR_UNKNOWN19 = UUID.fromString("00000005-0000-3512-2118-0009af100700");

    // 1-7
    // getProperties() mProperties=18
    // setValue() value=[15, 70, 0, -32, 7, 10, 21, 12, 13, 9, 36, -32, 7, 10, 21, 12, 13, 16, 36, 100]
    // getValue() value=[15, 70, 0, -32, 7, 10, 21, 12, 13, 9, 36, -32, 7, 10, 21, 12, 13, 16, 36, 100]
    // 2. setCharacteristicNotification() true when connecting miband

    // m f b 1
    public static final UUID UUID_CHAR_BATTERY_INFO = UUID.fromString("00000006-0000-3512-2118-0009af100700");

    // 2-1
    // getProperties() mProperties=18
    // setValue() value=[12, 12, 1, 0, 0, -84, 0, 0, 0, 6, 0, 0, 0]
    // getValue() value=[12, 12, 1, 0, 0, -84, 0, 0, 0, 6, 0, 0, 0]
    // 3. setCharacteristicNotification() true when connecting miband
    public static final UUID UUID_CHARACTERISTIC_7_REALTIME_STEPS = UUID.fromString("00000007-0000-3512-2118-0009af100700");

    // 3-1
    // setValue() value=[79, 0, 0, -55, 7, 12, 1, 0, -86, 0, 32, 53, -53, 26, 20, 98]
    // getProperties() mProperties=24
    // getValue() value=[79, 0, 0, -55, 7, 12, 1, 0, -86, 0, 32, 53, -53, 26, 20, 98]

    // value=[32, 0, 0, -126] is maybe relate band location left or right wrist

    /*
        Band location Step

        1. setValue(UUID_CHAR_BAND_LOCATION) value=[32, 0, 0, -126]

        left hand value [32, 0, 0, 2] right hand value [32, 0, 0, -126]
     */
    public static final UUID UUID_CHAR_BAND_LOCATION = UUID.fromString("00000008-0000-3512-2118-0009af100700");

    public static final UUID UUID_CHAR_UNKNOWN23 = UUID.fromString("00000010-0000-3512-2118-0009af100700");


    public static final UUID UUID_SERVICE_MIBAND2 = UUID.fromString("0000fee1-0000-1000-8000-00805f9b34fb");

    // 1. setCharacteristicNotification() true when connecting miband
    // getProperties() mProperties=22
    // setValue() value=[2, 0]
    // getValue() value=[2, 0]
    // setValue() value=[16, 2, 1, -5, 51, 104, -60, 77, 109, 106, -18, 127, 7, 76, 120, -111, 1, 14, 98]
    // getValue() value=[16, 2, 1, -5, 51, 104, -60, 77, 109, 106, -18, 127, 7, 76, 120, -111, 1, 14, 98]
    // setValue() value=[3, 0, -123, -119, -123, 48, 47, -91, -2, 90, -116, -117, -74, 77, 71, 98, 73, 19]
    // getValue() value=[3, 0, -123, -119, -123, 48, 47, -91, -2, 90, -116, -117, -74, 77, 71, 98, 73, 19]
    // setValue() value=[16, 3, 1]
    // getValue() value=[16, 3, 1]
    // setCharacteristicNotification() false
    public static final UUID UUID_CHAR_UNKNOWN24 = UUID.fromString("00000009-0000-3512-2118-0009af100700");

    public static final UUID UUID_CHAR_UNKNOWN25 = UUID.fromString("0000fedd-0000-1000-8000-00805f9b34fb");

    public static final UUID UUID_CHAR_UNKNOWN26 = UUID.fromString("0000fede-0000-1000-8000-00805f9b34fb");

    public static final UUID UUID_CHAR_UNKNOWN27 = UUID.fromString("0000fedf-0000-1000-8000-00805f9b34fb");

    public static final UUID UUID_CHAR_UNKNOWN28 = UUID.fromString("0000fed0-0000-1000-8000-00805f9b34fb");

    public static final UUID UUID_CHAR_UNKNOWN29 = UUID.fromString("0000fed1-0000-1000-8000-00805f9b34fb");

    public static final UUID UUID_CHAR_UNKNOWN30 = UUID.fromString("0000fed2-0000-1000-8000-00805f9b34fb");

    public static final UUID UUID_CHAR_UNKNOWN31 = UUID.fromString("0000fed3-0000-1000-8000-00805f9b34fb");

    public static final UUID UUID_CHAR_UNKNOWN32 = UUID.fromString("0000fec1-0000-3512-2118-0009af100700");

    // ========================== Service end ============================


    // ========================== Descriptor ============================

    public static final UUID UUID_DESCRIPTOR_UPDATE_NOTIFICATION = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    // ========================== Descriptor end ============================


    // ========================== Characteristic ============================

    // ========================== Characteristic end ============================


    /*
        UUID_CHAR_DISPLAY_SETTINGS
        onChraracteristicChanged(), onDescriptorWrite() -> getValue
        azr.onDescriptorWrite() -> azp.m() -> setValue([3, 1])
        onNotify() -> setValue([16, 3, 1, 1, -98, 18, 0, 0, 1, 0, 1, 0, 74, 0, 0, 0])

        setValue() - service uuid=0000fee0-0000-1000-8000-00805f9b34fb char uuid=00000003-0000-3512-2118-0009af100700 value=[16, 3, 1, 1, -98, 18, 0, 0, 1, 0, 1, 0, 74, 0, 0, 0]
        getValue() - service uuid=0000fee0-0000-1000-8000-00805f9b34fb char uuid=00000003-0000-3512-2118-0009af100700 value=[16, 3, 1, 1, -98, 18, 0, 0, 1, 0, 1, 0, 74, 0, 0, 0]
        setValue() - service uuid=0000fee0-0000-1000-8000-00805f9b34fb char uuid=00000003-0000-3512-2118-0009af100700 value=[3, 1]
        getProperties() - service uuid=0000fee0-0000-1000-8000-00805f9b34fb char uuid=00000003-0000-3512-2118-0009af100700 mProperties=20
        getProperties() - service uuid=0000fee0-0000-1000-8000-00805f9b34fb char uuid=00000003-0000-3512-2118-0009af100700 mProperties=20
        getValue() - service uuid=0000fee0-0000-1000-8000-00805f9b34fb char uuid=00000003-0000-3512-2118-0009af100700 value=[3, 1]
        getValue() - service uuid=0000fee0-0000-1000-8000-00805f9b34fb char uuid=00000003-0000-3512-2118-0009af100700 value=[3, 1]

        */

}
