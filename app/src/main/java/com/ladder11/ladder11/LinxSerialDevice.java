package com.ladder11.ladder11;

/**
 * Created by jordanbrobots on 11/29/15.
 */
//#TODO make all methods Thread safe so that they can operate in separate threads! (synchronize on this)
public class LinxSerialDevice extends LinxDevice {

    public LinxSerialDevice() {
        super();
    }

    public LinxSerialDevice(int startBaudRate) {
        //#TODO pass in either a serial device, or do all the necessary stuff here like the bridge
        super();
    }

    @Override
    byte[] buildPacket(int command) {
        packetNum++;
        byte[] packet = new byte[7];
        packet[0] = (byte) 0xFF;            //Start byte
        packet[1] = 7;                      //Length byte
        packet[2] = (byte) (packetNum>>8);  //Packet number high byte
        packet[3] = (byte) (packetNum);     //Packet number low byte
        packet[4] = (byte) (command>>8);    //Command high byte
        packet[5] = (byte) (command);       //Command low byte
        packet[6] = calcChecksum(packet);   //Checksum
        return packet;
    }

    @Override
    byte[] buildPacket(int command, int[] data) {
        packetNum++;
        byte[] packet = new byte[7+data.length];
        packet[0] = (byte) 0xFF;            //Start byte
        packet[1] = (byte) (7+data.length); //Length byte
        packet[2] = (byte) (packetNum>>8);  //Packet number high byte
        packet[3] = (byte) (packetNum);     //Packet number low byte
        packet[4] = (byte) (command>>8);    //Command high byte
        packet[5] = (byte) (command);       //Command low byte

        for(int i=0; i<data.length; i++) {  //Add the data bytes to the packet
            packet[6+i] = (byte) data[i];
        }
        packet[packet.length-1] = calcChecksum(packet); //Checksum

        return packet;
    }

    @Override
    byte calcChecksum(byte[] packet) {
        int sum = 0;
        for(int i=0; i<=packet.length-2; i++) {
            sum += packet[i];
        }
        return (byte) sum;
    }

    @Override
    byte[] sendPacket() {
        return new byte[0];
    }

    @Override
    void sendSync() {

    }

    @Override
    void setBaudRate() {

    }

    @Override
    int getMaxBaudRate() {
        return 0;
    }

    @Override
    void setPWMDutyCycle() {

    }

    @Override
    void servoOpen() {

    }

    @Override
    void servoSetPulseWidth() {

    }

    @Override
    void servoClose() {

    }

    @Override
    void analogRead() {

    }

    @Override
    void digitalRead() {

    }

    @Override
    void digitalWrite() {

    }

    @Override
    void i2COpen() {

    }

    @Override
    void i2CRead() {

    }

    @Override
    void i2CWrite() {

    }
}
