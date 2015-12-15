package com.lader11.ladder11;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by jordanbrobots on 12/12/15.
 * #TODO Find a way to safely disconnect
 */
public class RobotTelemetry {
    private final String TAG = "RobotTelemetry";

    private BluetoothDevice robotbtDevice;
    private BluetoothSocket btSocket = null;

    private InputStream inStream;
    private OutputStream outStream;

    //UUID for serial?  Apparently well known
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //Mac address of the device to connect to (defaults to below value)
    private static String address = "00:00:00:00:00:00";

    private List<TelemetryUpdates> listeners;

    RobotTelemetry() {
        listeners = new ArrayList<TelemetryUpdates>();
    }

    void registerListener(TelemetryUpdates item) {
        listeners.add(item);
    }

    public void connectToDevice(BluetoothDevice btDevice) {
        if(btDevice == null) {
            Log.e(TAG, "Cannot connect to a null device");
            //Notify listeners of a failed connection
            for(TelemetryUpdates item: listeners) {
                item.onFailedConnect();
            }
            return;
        }
        robotbtDevice = btDevice;
        this.address = robotbtDevice.getAddress();
        Log.d(TAG, "Attempting to connect to: " + address);

        //Attempt a connection
        try {
            btSocket =robotbtDevice.createRfcommSocketToServiceRecord(myUUID);
        } catch (IOException e) {
            Log.e(TAG, "Error on attempt to create socket: "+e.getMessage(), e);
            //Notify listeners of a failed connection
            for(TelemetryUpdates item: listeners) {
                item.onFailedConnect();
            }
            return;
        }

        //Establish the connection (connect blocks until successful connect)
        try {
            btSocket.connect();
        } catch (IOException e) {
            Log.e(TAG, "Unable to connect to the socket");
            try {
                btSocket.close();
            } catch (IOException e2)  {
                Log.e(TAG, "Unable to close the socket during connection failure");
                return;
            }
            //Notify listeners of a failed connection
            for(TelemetryUpdates item: listeners) {
                item.onFailedConnect();
            }
            return;
        }

        //Create the data streams to start communicating with the device
        try {
            outStream = btSocket.getOutputStream();
            inStream = btSocket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Unable to get input and output streams: "+e.getMessage(), e);
            //Notify listeners of a failed connection
            for(TelemetryUpdates item: listeners) {
                item.onFailedConnect();
            }
            return;
        }

        //Everything has succeeded, so start the read thread, and notify listeners
        readThread.start();
        for(TelemetryUpdates item: listeners) {
            item.onSuccessfulConnect();
        }
        Log.d(TAG, "Connected to Robot");
        //Toast.makeText(this, "Connected to Robot", Toast.LENGTH_SHORT);
    }

    Thread readThread = new Thread(new Runnable() {
        public void run() {
            try {
                while(true) {
                    if(inStream.available() >= 2) {
                        Log.d(TAG, "Starting to read in packet");
                        //Check the start byte;
                        if(inStream.read() != 0xFF) {
                            Log.d(TAG, "Not a start byte");
                            continue;
                        }
                        //Read and check the length byte
                        int length = inStream.read();
                        if(length < 4) {
                            Log.d(TAG, "Bad length byte: "+length);
                            continue;
                        }
                        final byte [] packet = new byte[length];
                        packet[0] = (byte) 0xFF;
                        packet[1] = (byte) length;
                        //Wait for the rest of the packet
                        while(inStream.available() < (length - 2)) {}
                        //Read in the rest of the bytes into the packet
                        inStream.read(packet, 2, length-2);

                        //Process the packet
                        processPacket(packet);
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Unhandled Read exception: "+e.getMessage(), e);
            }
        }
    });

    public void processPacket(final byte[] packet) {
        //Print out the received packet
        String printstr = "Processing Packet: {";
        for(byte x : packet) { printstr += x + ","; }
        printstr += "}";
        Log.d(TAG, printstr);

        //Disregard the packet if it does not have a valid checksum
        if(validChecksum(packet)) {
            switch(packet[2]) {
                case TelemetryConstants.COMMAND_START:
                    break;
                case TelemetryConstants.COMMAND_STOP:
                    break;
                case TelemetryConstants.COMMAND_ROBOT_POSE:
                    //Reconstruct the data
                    float robotX = reconstructFloat(packet[3], packet[4], packet[5]);
                    float robotY = reconstructFloat(packet[6], packet[7], packet[8]);
                    float robotTheta = reconstructFloat(packet[9], packet[10], packet[11]);
                    //Log.d(TAG, "Read RobotPose X: "+robotX+" Y: "+robotY+" Theta: "+robotTheta);

                    //Call all listeners
                    for(TelemetryUpdates item : listeners) {
                        item.onRobotPoseUpdate(robotX, robotY, robotTheta);
                    }
                    break;
                case TelemetryConstants.COMMAND_FLAME_LOC:
                    float flameX = reconstructFloat(packet[3], packet[4], packet[5]);
                    float flameY = reconstructFloat(packet[6], packet[7], packet[8]);
                    float flameZ = reconstructFloat(packet[9], packet[10], packet[11]);
                    //Log.d(TAG, "Read Flame Loc. X: "+flameX+" Y: "+flameY+" Z: "+flameZ);

                    for(TelemetryUpdates item : listeners) {
                        item.onFlameLocationUpdate(flameX, flameY, flameZ);
                    }
                    break;
                case TelemetryConstants.COMMAND_CAMERA_DATA:
                    int camX = (packet[3] << 8) | (packet[4] & 0xFF);
                    int camY = (packet[5] << 8) | (packet[6] & 0xFF);
                    byte camSize = packet[7];
                    //Log.d(TAG, "Read Camera Data X: "+camX+" Y: "+camY+" Size: "+camSize);

                    for(TelemetryUpdates item: listeners) {
                        item.onCameraDataUpdate(camX, camY, camSize);
                    }
                    break;
                case TelemetryConstants.COMMAND_BATT_VOLT:
                    float battVolt = reconstructFloat(packet[3], packet[4], packet[5]);
                    //Log.d(TAG, "Read Battery Voltage: "+battVolt+" V");

                    for(TelemetryUpdates item: listeners) {
                        item.onRobotBatteryUpdate(battVolt);
                    }
                    break;
                case TelemetryConstants.COMMAND_GYRO_DATA:
                    float gyroZ = reconstructFloat(packet[3], packet[4], packet[5]);
                    //Log.d(TAG, "Read Gyro Data Z: "+gyroZ+" deg.");

                    for(TelemetryUpdates item: listeners) {
                        item.onGyroDataUpdate(gyroZ);
                    }
                    break;
                case TelemetryConstants.COMMAND_STATUS:
                    byte status = packet[3];
                    byte substatus = packet[4];
                    //Log.d(TAG, "Read Status: "+status+":"+substatus);

                    for(TelemetryUpdates item: listeners) {
                        item.onStatusUpdate(status, substatus);
                    }
                    break;
                case TelemetryConstants.COMMAND_RUNNING:
                    //Log.d(TAG, "Read Running packet");
                    for(TelemetryUpdates item: listeners) {
                        item.onRunningUpdate();
                    }
                    break;
                case TelemetryConstants.COMMAND_STOPPED:
                    //Log.d(TAG, "Read Stopped packet");
                    for(TelemetryUpdates item: listeners) {
                        item.onStoppedUpdate();
                    }
                    break;
                default:
                    Log.d(TAG, "Received unknown packet type");
            }
        } else {
            Log.d(TAG, "Packet has bad checksum");
        }
    }

    //Hacked way to reconstruct a float. Probably not the greatest way
    public float reconstructFloat(byte highByte, byte lowByte, byte decByte) {
        int intPart = ((int) highByte) << 8;
        intPart |= ((int) lowByte) & 0xFF;
        int decPart = ((int) decByte) & 0xFF;
        Float parse = new Float(intPart + "." + decPart);
        return parse.floatValue();
    }

    public boolean validChecksum(final byte[] packet) {
        return (packet[packet.length-1] == calcChecksum(packet));
    }

    public byte calcChecksum(final byte[] packet) {
        byte sum = 0;
        for (int i = 0; i < (packet.length - 1); i++) {
            sum += packet[i] & 0xFF;
        }
        return sum;
    }

    /** Sends a start message to the robot
     */
    public void sendStartAsync() {
        if(outStream == null) {
            return;     //Not connected, just ignore it
        }
        new Thread( new Runnable() {
            public void run() {
                byte[] packet = new byte[TelemetryConstants.LEN_START];
                packet[0] = TelemetryConstants.START_BYTE;
                packet[1] = TelemetryConstants.LEN_START;
                packet[2] = TelemetryConstants.COMMAND_START;
                packet[3] = calcChecksum(packet);
                try {
                    Log.d(TAG, "Sending Start Packet");
                    outStream.write(packet);
                } catch (IOException e) {
                    Log.e(TAG, "Couldn't send Start packet: "+e.getMessage(), e);
                }
            }
        }).start();
    }

    /** Sends a stop message to the robot
     */
    public void sendStopAsync() {
        if(outStream == null) {
            return;     //Not connected, just ignore it
        }
        new Thread( new Runnable() {
            public void run() {
                byte[] packet = new byte[TelemetryConstants.LEN_STOP];
                packet[0] = TelemetryConstants.START_BYTE;
                packet[1] = TelemetryConstants.LEN_STOP;
                packet[2] = TelemetryConstants.COMMAND_STOP;
                packet[3] = calcChecksum(packet);
                try {
                    Log.d(TAG, "Sending Stop packet");
                    outStream.write(packet);
                } catch (IOException e) {
                    Log.e(TAG, "Couldn't send Stop packet");
                }
            }
        }).start();
    }
}