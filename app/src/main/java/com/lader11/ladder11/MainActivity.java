package com.lader11.ladder11;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.hoho.android.usbserial.util.HexDump;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private String TAG = "Ladder11MainActivity";

    private ArduinoBridge arduinoBridge;
    private int packetNum = 0;
    private LinxSerialDevice myLinx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arduinoBridge = new ArduinoBridge(this);
        myLinx = new LinxSerialDevice();
    }

    @Override
    protected void onResume() {
        super.onResume();
        arduinoBridge.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Close the connection for safety
        arduinoBridge.onPause();
    }

    public void sendSyncMsg(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] data = myLinx.buildPacket(0);
                Log.d(TAG, "Sending packet");
                arduinoBridge.sendBytes(data);
                //Wait for packet receipt
                //#TODO make a secondary storage space for the incoming data, since the packet is not recieved as one packet
                long timeout = 250;     //Amount of milliseconds until waiting for a read times out
                long startTime = System.currentTimeMillis();
                ArrayList<Byte> packet = new ArrayList<>();
                byte[] buffer = new byte[32];
                int bytesRead;
                while((System.currentTimeMillis()-startTime) < timeout) {
                    Log.d(TAG, "Loop Time: "+(System.currentTimeMillis()-startTime)+": "+((System.currentTimeMillis()-startTime) < timeout));
                    bytesRead = arduinoBridge.readBytes(buffer, 10);
                    if(bytesRead > 0) {
                        //Add the bytes to the list
                        Log.d(TAG, "Initial list size: "+packet.size());
                        Log.d(TAG, "Number of bytes read: "+bytesRead);
                        for(int i=0; i<bytesRead; i++) {
                            packet.add(buffer[i]);
                        }
                        Log.d(TAG, "New list: "+packet.toString());
                    }
                }
                Log.d(TAG, "Done reading");
            }
        }).start();
    }
}
