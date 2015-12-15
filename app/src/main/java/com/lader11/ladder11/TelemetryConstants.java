package com.lader11.ladder11;

/**
 * Created by jordanbrobots on 12/10/15.
 */
public class TelemetryConstants {

    static final byte START_BYTE = (byte) 0xFF;

    static final byte COMMAND_START = 0x01;
    static final byte COMMAND_STOP = 0x02;
    static final byte COMMAND_ROBOT_POSE = 0x03;
    static final byte COMMAND_FLAME_LOC = 0x04;
    static final byte COMMAND_CAMERA_DATA = 0x05;
    static final byte COMMAND_BATT_VOLT = 0x06;
    static final byte COMMAND_GYRO_DATA = 0x07;
    static final byte COMMAND_STATUS = 0x08;
    static final byte COMMAND_RUNNING = 0x09;
    static final byte COMMAND_STOPPED = 0x0A;

    static final byte LEN_START = 4;
    static final byte LEN_STOP = 4;
    static final byte LEN_ROBOT_POSE = 13;
    static final byte LEN_FLAME_LOC = 13;
    static final byte LEN_CAMERA_DATA = 9;
    static final byte LEN_BATT_VOLT = 7;
    static final byte LEN_GYRO_DATA = 7;
    static final byte LEN_STATUS = 6;
    static final byte LEN_RUNNING = 4;
    static final byte LEN_STOPPED = 4;

    static final byte STATUS_WAIT4START = 0x01;
    static final byte STATUS_WALLFOLLOW = 0x02;
    static final byte STATUS_APPROACH_FLAME = 0x03;
    static final byte STATUS_CALC_FLAME_LOC = 0x04;
    static final byte STATUS_EXT_FLAME = 0x05;
    static final byte STATUS_RETURN_HOME = 0x06;
    static final byte STATUS_HOME = 0x07;

    static final String STATUS_WAIT4START_STRING = "Wait for Start";
    static final String STATUS_WALLFOLLOW_STRING = "Wall Following";
    static final String STATUS_APPROACH_FLAME_STRING = "Approaching Flame";
    static final String STATUS_CALC_FLAME_LOC_STRING = "Calculating Flame Location";
    static final String STATUS_EXT_FLAME_STRING = "Extinguishing Flame";
    static final String STATUS_RETURN_HOME_STRING = "Returning Home";
    static final String STATUS_HOME_STRING = "Home";

    static final byte SUBSTATUS_NONE = (byte) 0xFF;
    static final byte SUBSTATUS_TOOFARFROMWALL = 0x01;
    static final byte SUBSTATUS_TURNING_TO_CANDLE_POS = 0x02;
    static final byte SUBSTATUS_DRIVING_TO_CANDLE = 0x03;
    static final byte SUBSTATUS_WALLFOLLOW = 0x04;

    static final String SUBSTATUS_TOOFARFROMWALL_STRING = "Too Far";
    static final String SUBSTATUS_TURNING_TO_CANDLE_POS_STRING = "Turning";
    static final String SUBSTATUS_DRIVING_TO_CANDLE_POS_STRING = "Driving";
    static final String SUBSTATUS_WALLFOLLOW_STRING = "Normal";
}
