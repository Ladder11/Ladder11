package com.lader11.ladder11;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements TelemetryUpdates{
    private String TAG = "Ladder11MainActivity";

    //Bluetooth Objects
    private BluetoothAdapter btAdapter = null;
    private RobotTelemetry robotTelemetry;

    //Bluetooth Intent Request codes
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 2;

    //UUID for serial?  Apparently well known
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //Mac address of the device to connect to (defaults to below value)
    private static String address = "00:00:00:00:00:00";

    private TextView textView;
    private Button connectButton;
    private Button startButton;

    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        textView.setText("");
        connectButton = (Button) findViewById(R.id.connectButton);
        connectButton.setEnabled(false);
        startButton = (Button) findViewById(R.id.startButton);
        startButton.setEnabled(false);

        robotTelemetry = new RobotTelemetry();
        robotTelemetry.registerListener(this);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                }
            }
        });

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
        if(tts != null) {
            tts.stop();
            tts.shutdown();
        }
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
                    //Attempt a connection only when the device is found;
                    robotTelemetry.connectToDevice(device);
                }
            }
        }
    }

    /**
     * Only enable robot communication buttons when the robot has succesfully connected
     */
    public void onSuccessfulConnect() {
        startButton.setEnabled(true);
    }

    public void sendRobotStart(View view) {
        if(robotTelemetry != null) {
            robotTelemetry.sendStartAsync();
        }
    }

    @Override
    public void onRobotPoseUpdate(float x, float y, float theta) {
        addToTextView("Robot pose X: "+x+" Y: "+y+" Theta: "+theta);
    }

    @Override
    public void onFlameLocationUpdate(float x, float y, float z) {
        addToTextView("Flame Location X: " + x + " Y: " + y + " Z: " + z);
        String st = "The flame is located at "+x+" inches in the x direction, "+
                     y+" inches in the y direction, and "+z+" inches from the ground.";
        tts.speak(st, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onCameraDataUpdate(int x, int y, byte size) {
        addToTextView("Camera Data X: "+x+" Y: "+y+" Size: "+size);
    }

    @Override
    public void onRobotBatteryUpdate(float voltage) {
        addToTextView("Battery Voltage: "+voltage+" Volts");
    }

    @Override
    public void onGyroDataUpdate(float theta) {
        addToTextView("Gyro Z: "+theta);
    }

    @Override
    public void onStatusUpdate(byte status, byte substatus) {
        addToTextView("Status "+status+":"+substatus);
    }

    @Override
    public void onRunningUpdate() {
        addToTextView("Robot is Running");
    }

    @Override
    public void onStoppedUpdate() {
        addToTextView("Robot is Stopped");
    }

    public void addToTextView(final String st) {
        textView.post(new Runnable() {
            public void run() {
                textView.setText(textView.getText()+st+"\n");
            }
        });
    }
}
