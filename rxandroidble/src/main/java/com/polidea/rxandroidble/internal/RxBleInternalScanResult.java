package com.polidea.rxandroidble.internal;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;

public class RxBleInternalScanResult {

    private final BluetoothDevice bluetoothDevice;
    private final int rssi;
    private final byte[] scanRecord;
    private final int advertiseFlags;

    public RxBleInternalScanResult(BluetoothDevice bleDevice, int rssi, byte[] scanRecords) {
        this(bleDevice, rssi, scanRecords, -1);
    }

    public RxBleInternalScanResult(BluetoothDevice bleDevice, int rssi, byte[] scanRecords, int advertiseFlags) {
        this.bluetoothDevice = bleDevice;
        this.rssi = rssi;
        this.scanRecord = scanRecords;
        this.advertiseFlags = advertiseFlags;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public int getRssi() {
        return rssi;
    }

    public byte[] getScanRecord() {
        return scanRecord;
    }

    public int getAdvertiseFlags() {
        return advertiseFlags;
    }
}
