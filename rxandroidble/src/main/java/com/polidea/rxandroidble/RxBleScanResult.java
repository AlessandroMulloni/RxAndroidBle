package com.polidea.rxandroidble;

import android.bluetooth.le.ScanResult;

/**
 * Represents a scan result from Bluetooth LE scan.
 */
public class RxBleScanResult {

    private final RxBleDevice bleDevice;
    private final int rssi;
    private final byte[] scanRecord;
    private final int advertiseFlags;

    public RxBleScanResult(RxBleDevice bleDevice, int rssi, byte[] scanRecords) {
        this(bleDevice, rssi, scanRecords, -1);
    }

    public RxBleScanResult(RxBleDevice bleDevice, int rssi, byte[] scanRecords, int advertiseFlags) {
        this.bleDevice = bleDevice;
        this.rssi = rssi;
        this.scanRecord = scanRecords;
        this.advertiseFlags = advertiseFlags;
    }

    /**
     * Returns {@link RxBleDevice} which is a handle for Bluetooth operations on a device. It may be used to establish connection,
     * get MAC address and/or get the device name.
     */
    public RxBleDevice getBleDevice() {
        return bleDevice;
    }

    /**
     * Returns signal strength indication received during scan operation.
     *
     * @return the rssi value
     */
    public int getRssi() {
        return rssi;
    }

    /**
     * The scan record of Bluetooth LE advertisement.
     *
     * @return Array of data containing full ADV packet.
     */
    public byte[] getScanRecord() {
        return scanRecord;
    }

    /**
     * The advertise flags of Bluetooth LE advertisement.
     *
     * @return the flags as an integer value
     */
    public int getAdvertiseFlags() {
        return advertiseFlags;
    }
}
