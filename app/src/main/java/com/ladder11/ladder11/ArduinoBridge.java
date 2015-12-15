package com.ladder11.ladder11;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jordanbrobots on 11/19/15.
 */

//#TODO Handle exception on wakeup when phone goes to sleep but arduino is still connected
    //#TODO Fix having to press reset button on Arduino to make things magically work
public class ArduinoBridge {
    private Context appContext;
    private String TAG = "ArduinoBridge";

    //USBDriver, Serial Port, and IoManager for the Arduino
    private UsbSerialDriver arduinoDriver;
    private UsbSerialPort arduinoPort;
    private SerialInputOutputManager arduinoSIOManager;

    //Executor to detect data events
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    ArduinoBridge(Context context) {
        appContext = context;
    }


    /**
     * Handle any actions that need to happen when the device is reconnected
     */
    public void onResume() {
        Log.d(TAG, "Connecting to the first device");

        //Get a new reference to the usbManager (might not be necessary_
        UsbManager usbManager = (UsbManager) appContext.getSystemService(Context.USB_SERVICE);

        //Get a list of all connected "Arduino" like serial devices
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);
        if(availableDrivers.isEmpty()) {
            //No devices were detected, set the connection to null, and exit
            Log.d(TAG, "No devices were detected");
            arduinoPort = null;
            return;
        }

        //Attempt to open a connection to the first device
        arduinoDriver = availableDrivers.get(0);
        UsbDeviceConnection connection = usbManager.openDevice(arduinoDriver.getDevice());
        if(connection == null) {
            Log.d(TAG, "Opening connection to the device failed");
            return;
        }

        //Get the first "port" on the device which is typicallly the serial connection
        arduinoPort = arduinoDriver.getPorts().get(0);
        Log.d(TAG, "Opened new arduino serial port: " + arduinoPort);

        //Try to initialize the port
        try {
            //Connect to the port, and set the UART parameters
            arduinoPort.open(connection);
            arduinoPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
        } catch (IOException e){
            Log.e(TAG, "Error during attempted serial setup: " + e.getMessage(), e);
            //Close the port
            closePort();
            return;
        }
        //Restarts the IO Manager to handle events with the new device
        restartIOManager();
    }

    /**
     * Handle any actions that need to hapen when the device should be paused
     */
    public void onPause() {
        Log.d(TAG, "Shutting down connection");
        //Stop the event handling
        stopIoManager();
        closePort();
    }

    /**
     * Closes the connection to the field device
     */
    private void closePort() {
        Log.d(TAG, "Closing the Serial port");
        try {
            arduinoPort.close();
        } catch (Exception e) {
            //Don't care about any exceptions when closing the device
        }
        arduinoPort = null;
    }

    /**
     * Starts the IoManager to handle data events
     */
    private void startIOManager() {
        if (arduinoPort != null) {
            Log.d(TAG, "Starting IO Manager");
            arduinoSIOManager = new SerialInputOutputManager(arduinoPort, serialListener);
            mExecutor.submit(arduinoSIOManager);
        }
    }

    /**
     * Stop the IOManager handling the data events
     */
    private void stopIoManager() {
        if(arduinoSIOManager != null) {
            Log.d(TAG, "Stopping IO Manager");
            //Shut down the data event handler
            arduinoSIOManager.stop();
            arduinoSIOManager = null;
            //Shut down the executor thread #TODO might already be handled
            mExecutor.shutdownNow();
        }
    }

    /**
     * Restart the IOManager
     */
    private void restartIOManager() {
        stopIoManager();
        //startIOManager();     //#TODO Temporarily disables event notification
    }

    /**
     * Handle various data and error events
     */
    private final SerialInputOutputManager.Listener serialListener = new SerialInputOutputManager.Listener() {
                @Override
                public void onRunError(Exception e) {
                    Log.e(TAG, "Data Event thread error: "+e.getMessage(), e);
                    //Possible do something
                }

                @Override
                public void onNewData(final byte[] data) {
                    //Do something when new data is received
                    //Add the data to a queue for parsing, and log the data in the log file
                    Log.d(TAG, "Packet Received at: "+System.currentTimeMillis());
                    Log.d(TAG, "Received: ("+data.length+"): "+ HexDump.dumpHexString(data));
                }
            };

    /**
     * Check if the serial port is connected
     */
    public boolean isConnected() {
        return arduinoPort != null;
    }

    /**
     * Send a byte by starting a new thread
     */
    public void sendByteAsync(final byte data) {
        //Do nothing if the arduino is not connected
        if(arduinoPort == null) {
            return;
        }

        Thread thread = new Thread() {
            public void run() {
                byte[] dataValues = { data };
                try {
                    Log.d(TAG, "Packet Sent at: "+System.currentTimeMillis());
                    arduinoPort.write(dataValues, 250);
                } catch (IOException e) {
                    Log.e(TAG, "SendByte exception: "+e.getMessage(), e);
                }
            }
        };
        thread.start();
        return;
    }

    /**
     * Send a byte array by starting a new thread
     */
    public void sendBytesAsync(final byte[] data) {
        //Do nothing if the arduino is not connected
        if(arduinoPort == null) {
            return;
        }

        Thread thread = new Thread() {
            public void run() {
                try {
                    Log.d(TAG, "Packet Sent at: "+System.currentTimeMillis());
                    arduinoPort.write(data, 250);
                } catch (IOException e) {
                    Log.e(TAG, "Send Bytes exception: "+e.getMessage(), e);
                }
            }
        };
        thread.start();
        return;
    }

    /**
     * Send a byte array in place
     */
    public void sendBytes(byte[] data) {
        //Do nothing if the arduino is not connected
        if(arduinoPort == null) {
            return;
        }

        try {
            arduinoPort.write(data, 250);
        } catch (IOException e) {
            Log.e(TAG, "Sending byte exception: "+e.getMessage(), e);
        }
    }

    /**
     * Read bytes into a buffer array in place
     */
    public int readBytes(byte[] buff, int timeout) {
        //Do nothing if the arduino is not connected
        if(arduinoPort == null) {
            return 0;
        }
        int val = 0;
        try {
            val = arduinoPort.read(buff, timeout);
        } catch (IOException e) {
            Log.e(TAG, "Reading byte exception: "+e.getMessage(), e);
        }
        return val;
    }
}
