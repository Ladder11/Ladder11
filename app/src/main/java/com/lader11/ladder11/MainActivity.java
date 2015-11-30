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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arduinoBridge = new ArduinoBridge(this);
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
        packetNum++;
        int command = 0;
        byte[] packet = {   (byte) 0xFF,        //Start Byte
                            (byte) 7,           //Size
                            (byte) (packetNum>>8),
                            (byte) packetNum,
                            (byte) (command>>8),
                            (byte) command,
                            (byte) (0xFF+7+(packetNum>>8)+(packetNum&0xFF)+(command>>8)+(command&0xFF))};
        Log.d(TAG, "Sending Packet ("+packet.length+"): "+HexDump.toHexString(packet));
        arduinoBridge.sendBytes(packet);
    }
}
