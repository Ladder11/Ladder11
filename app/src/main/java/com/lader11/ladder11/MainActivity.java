package com.lader11.ladder11;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.hoho.android.usbserial.util.HexDump;

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
                Log.d(TAG, "Hello world");
                try { Thread.sleep(1000); }
                catch (InterruptedException e) {}
                Log.d(TAG, "This is a test");
            }
        }).start();
    }
}
