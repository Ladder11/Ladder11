package com.lader11.ladder11;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.ParcelUuid;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private String TAG = "Ladder11MainActivity";

    //Bluetooth Objects
    private BluetoothAdapter btAdapter = null;
    private BluetoothDevice robotBtDev = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private InputStream inStream = null;

    //Bluetooth Intent Request codes
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 2;

    //UUID for serial?  Apparently well known
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //Mac address of the device to connect to (defaults to below value)
    private static String address = "00:00:00:00:00:00";

    private TextView textView;
    private Button connectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        textView.setText("");
        connectButton = (Button) findViewById(R.id.button);
        connectButton.setEnabled(false);
        startBluetooth();
    }

    //Receive results of activities that were started for results
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            //Receive the result of allowing / denying enabling bluetooth
            case REQUEST_ENABLE_BT:
                if(resultCode == RESULT_OK) {
                    Toast.makeText(this, "Bluetooth Enabled", Toast.LENGTH_SHORT).show();
                    connectButton.setEnabled(true);
                } else {
                    Toast.makeText(this, "Bluetooth denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void startBluetooth() {
        //Get a reference to the bluetooth device in the phone
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        //Make sure that the phone has a bluetooth device
        if(btAdapter == null) {
            Toast.makeText(this, "Bluetooth is Not Supported", Toast.LENGTH_LONG);
        }

        //Check if the bluetooth adapter is enabled
        if(!btAdapter.isEnabled()) {
            //Bluetooth adapter is not currently enabled, prompt the user to start it
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            //enable the connect button
            connectButton.setEnabled(true);
        }

        //#TODO handle bluetooth on/off events with broadcast receivers
    }

    public void connectToRobot(View view) {
        //Get the list of already paired devices
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        //If there are devices paired, print them out
        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                Log.d(TAG, "Device: "+device.getName()+" Address: "+device.getAddress());
                //Toast.makeText(this, "Paired to: "+device.getName()+"/n"+device.getAddress(), Toast.LENGTH_SHORT).show();
                if(device.getName().equals("BT_01")) {
                    Toast.makeText(this, "Found robot", Toast.LENGTH_SHORT).show();
                    robotBtDev = device;
                    //Attempt a connection only when the device is found;
                    connectToDevice(robotBtDev.getAddress());
                }
            }
        }
    }

    public void connectToDevice(String address) {
        this.address = address;
        Log.d(TAG, "Attempting to connect to: " + address);
        //Toast.makeText(this, "Connecting to: "+address, Toast.LENGTH_SHORT);

        //Attempt a connection
        try {
            btSocket = robotBtDev.createRfcommSocketToServiceRecord(myUUID);
        } catch (IOException e) {
            Log.e(TAG, "Error on attempt to create socket: "+e.getMessage(), e);
        }

        //Shutdown any discovery services since we are connecting, and discovery is resource intensive
        btAdapter.cancelDiscovery();

        //Establish the connection (connect blocks until successful connect)
        try {
            btSocket.connect();
        } catch (IOException e) {
            Log.e(TAG, "Unable to connect to the socket");
            try {
                btSocket.close();
            } catch (IOException e2)  {
                Log.e(TAG, "Unable to close the socket during connection failure");
            }
        }

        //Create the data streams to start communicating with the device
        try {
            outStream = btSocket.getOutputStream();
            inStream = btSocket.getInputStream();
            readThread.start();
        } catch (IOException e) {
            Log.e(TAG, "Unable to get input and output streams: "+e.getMessage(), e);
        }
        Toast.makeText(this, "Connected to Robot", Toast.LENGTH_SHORT);
    }

    public void processPacket(final byte[] packet) {
        //Disregard the packet if it does not have a valid checksum
        if(validChecksum(packet)) {
            switch(packet[2]) {
                case TelemetryConstants.COMMAND_START:
                    break;
                case TelemetryConstants.COMMAND_STOP:
                    break;
                case TelemetryConstants.COMMAND_ROBOT_POSE:
                    //Reconstruct the data
                    int xint, yint, thetaint;
                    xint = ((int) packet[3]) << 8;
                    xint |= ((int) packet[4]) & 0xFF;
                    yint = ((int) packet[6]) << 8;
                    yint |= ((int) packet[7]) & 0xFF;
                    thetaint = ((int) packet[9]) << 8;
                    thetaint |= ((int) packet[10]) & 0xFF;

                    int xdec, ydec, thetadec;
                    xdec = ((int) packet[5]) & 0xFF;
                    ydec = ((int) packet[8]) & 0xFF;
                    thetadec = ((int) packet[11]) & 0xFF;

                    /*
                    float x, y, theta;
                    x = ((float) xint) + (((float) xdec)/100);
                    y = ((float) yint) + (((float) ydec)/100);
                    theta = ((float) thetaint) + (((float) thetadec)/100); */
                    Float x = new Float(xint + "."+ xdec);
                    Float y = new Float(yint + "."+ ydec);
                    Float theta = new Float(thetaint + "." + thetadec);

                    final String printStr = "Read RobotPose X: "+x+" Y: "+y+" Theta: "+theta;
                    Log.d(TAG, printStr);
                    textView.post(new Runnable() {
                        public void run() {
                            textView.setText(textView.getText()+printStr+"\n");
                        }
                    });
                    break;
                case TelemetryConstants.COMMAND_FLAME_LOCATION:
                    break;
                case TelemetryConstants.COMMAND_CAMERA_DATA:
                    break;
                case TelemetryConstants.COMMAND_BATT_VOLT:
                    break;
                default:
                    Log.d(TAG, "Received unknown packet type");
            }
        } else {
            Log.d(TAG, "Packet had bad checksum");
        }
    }

    public boolean validChecksum(final byte[] packet) {
        byte sum = 0;
        for(int i=0; i<(packet.length-1); i++) {
            //Upcast and bitmask
            sum += packet[i] & 0xFF;
        }
        return (packet[packet.length-1] == sum);
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

                        //Show the received data
                        textView.post(new Runnable() {
                            public void run() {
                                String dataString = "{";
                                for (int i = 0; i < packet.length; i++) {
                                    dataString += packet[i] & 0xFF;
                                    dataString += ",";
                                }
                                dataString += "}";
                                textView.setText(textView.getText() + "Read: " + dataString + "\n");
                                if (validChecksum(packet)) {
                                    textView.setText(textView.getText() + "Valid Checksum\n");
                                } else {
                                    textView.setText(textView.getText() + "Invalid Checksum\n");
                                }
                            }
                        });


                        //Process the packet
                        processPacket(packet);
                    }
                    /*final int data = inStream.read();
                    if(data != -1) {
                        //final byte[] character = new byte[1];
                        //character[0] = (byte) data;
                        textView.post(new Runnable() {
                            public void run() {
                                textView.setText(textView.getText()+" "+data);
                            }
                        });
                    }*/
                }
            } catch (IOException e) {
                Log.e(TAG+" ReadThread", "Unhandled Read exception: "+e.getMessage(), e);
            }
        }
    });
}
