package com.ladder11.ladder11;

/**
 * Abstraction for any device running the LINX firmware
 * Created by jordanbrobots on 11/29/15.
 */
abstract class LinxDevice {
    int packetNum;

    LinxDevice() {
        packetNum = 0;
    }

    abstract byte[] buildPacket(int command);
    abstract byte[] buildPacket(int command, int[] data);
    abstract byte calcChecksum(byte[] packet);
    abstract byte[] sendPacket();

    abstract void sendSync();
    abstract void setBaudRate();
    abstract int getMaxBaudRate();

    abstract void setPWMDutyCycle();

    abstract void servoOpen();
    abstract void servoSetPulseWidth();
    abstract void servoClose();

    abstract void analogRead();
    abstract void digitalRead();
    abstract void digitalWrite();

    abstract void i2COpen();
    abstract void i2CRead();
    abstract void i2CWrite();
}
