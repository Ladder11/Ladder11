package com.lader11.ladder11;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private ArduinoBridge arduinoBridge;

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
}
