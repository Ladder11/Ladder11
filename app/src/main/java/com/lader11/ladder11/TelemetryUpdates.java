package com.lader11.ladder11;

/**
 * Created by jordanbrobots on 12/12/15.
 */
public interface TelemetryUpdates {
    void onSuccessfulConnect();
    void onFailedConnect();
    void onRobotPoseUpdate(float x, float y, float theta);
    void onFlameLocationUpdate(float x, float y, float z);
    void onCameraDataUpdate(int x, int y, byte size);
    void onRobotBatteryUpdate(float voltage);
    void onGyroDataUpdate(float theta);
    void onStatusUpdate(byte status, byte substatus);
    void onRunningUpdate();
    void onStoppedUpdate();

}
