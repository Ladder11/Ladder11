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

import com.lader11.ladder11.Views.PoseDisplay;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

//#TODO add a disconnect button
public class MainActivity extends AppCompatActivity implements TelemetryUpdates {
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
    private Button stopButton;
    private TextView statusText;
    private PoseDisplay poseDisplay;

    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        textView.setText("");
        connectButton = (Button) findViewById(R.id.connectButton);
        connectButton.setEnabled(false);
        //Ensure that the app doesn't go to sleep when the robot is connected
        connectButton.setKeepScreenOn(true);
        startButton = (Button) findViewById(R.id.startButton);
        startButton.setEnabled(false);
        stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setEnabled(false);
        statusText = (TextView) findViewById(R.id.textStatus);
        poseDisplay = (PoseDisplay) findViewById(R.id.myPose);
        //Disables hardware acceleration for the pose display
        poseDisplay.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

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
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //Release the text to speech resource
        if(tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
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
        connectButton.setText("Connecting...");
        connectButton.setEnabled(false);
        //Get the list of already paired devices
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        //If there are devices paired, print them out
        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                Log.d(TAG, "Device: "+device.getName()+" Address: "+device.getAddress());
                //Search for the robot by bluetooth name
                if(device.getName().equals("BT_01")) {
                    //Attempt a connection only when the device is found;
                    final BluetoothDevice dev = device;
                    new Thread(new Runnable() {
                        public void run() {
                            robotTelemetry.connectToDevice(dev);
                        }
                    }).start();
                    //Already found the device, so don't need to keep searching
                    break;
                }
            }
        }
    }

    /**
     * Only enable robot communication buttons when the robot has succesfully connected
     */
    public void onSuccessfulConnect() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                startButton.setEnabled(true);
                stopButton.setEnabled(true);
                connectButton.setEnabled(false);
                connectButton.setText("Connected");
                connectButton.setTextColor(getResources().getColor(R.color.success));
            }
        });
    }

    /**
     * Shows a simple message that the connection failed
     */
    public void onFailedConnect() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, "Failed to connect to robot", Toast.LENGTH_SHORT).show();
                //Reset the connect button
                connectButton.setText("Connect");
                connectButton.setEnabled(true);
            }
        });
    }

    /**
     * Sends a start message to the robot when the start button is pressed
     * @param view
     */
    public void sendRobotStart(View view) {
        if(robotTelemetry != null) {
            robotTelemetry.sendStartAsync();
        }
    }

    /**
     * Send a stop message to the robot when the stop button is pressed
     * @param view
     */
    public void sendRobotStop(View view) {
        if(robotTelemetry != null) {
            robotTelemetry.sendStopAsync();
        }
    }

    @Override
    public void onRobotPoseUpdate(final float x, final float y, final float theta) {
        //addToTextView("Robot pose X: "+x+" Y: "+y+" Theta: "+theta);
        Log.d(TAG, "Gyro Heading: "+(theta * -180.0 / Math.PI)+ "deg. Rad: "+theta);
        poseDisplay.post(new Runnable() {
            public void run() {
                poseDisplay.setRobotPose(x, y, (float) (theta * -180.0 / Math.PI));
            }
        });
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
        addToTextView("Battery Voltage: " + voltage + " Volts");
    }

    @Override
    public void onGyroDataUpdate(final float theta) {
        //addToTextView("Gyro Z: "+theta);
        poseDisplay.post(new Runnable() {
            public void run() {
                poseDisplay.setGyroHeading(theta);
            }
        });

    }

    @Override
    public void onStatusUpdate(byte status, byte substatus) {
        addToTextView("Status "+status+":"+substatus);
        final byte fstatus = status;
        final byte fsubstatus = substatus;
        statusText.post(new Runnable() {
            public void run() {
                statusText.setText("Status: " + fstatus + ": " + fsubstatus);
            }
        });
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
                textView.setText(st+"\n"+textView.getText());
            }
        });
    }
}
