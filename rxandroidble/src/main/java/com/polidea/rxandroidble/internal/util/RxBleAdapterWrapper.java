package com.polidea.rxandroidble.internal.util;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.os.Build;
import android.support.annotation.Nullable;

import java.util.Set;

public class RxBleAdapterWrapper {

    private final BluetoothAdapter bluetoothAdapter;
    private final BluetoothLeScanner bluetoothLeScanner;

    public RxBleAdapterWrapper(@Nullable BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        } else {
            bluetoothLeScanner = null;
        }
    }

    public BluetoothDevice getRemoteDevice(String macAddress) {
        return bluetoothAdapter.getRemoteDevice(macAddress);
    }

    public boolean hasBluetoothAdapter() {
        return bluetoothAdapter != null;
    }

    public boolean isBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    public boolean startLeScan(BluetoothAdapter.LeScanCallback leScanCallback) {
        return bluetoothAdapter.startLeScan(leScanCallback);
    }

    public void stopLeScan(BluetoothAdapter.LeScanCallback leScanCallback) {
        bluetoothAdapter.stopLeScan(leScanCallback);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean startLeScan(ScanCallback callback) {
        if (bluetoothLeScanner != null) {
            bluetoothLeScanner.startScan(callback);
            return true;
        }

        return false;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void stopLeScan(ScanCallback callback) {
        if (bluetoothLeScanner != null) {
            bluetoothLeScanner.stopScan(callback);
        }
    }

    public Set<BluetoothDevice> getBondedDevices() {
        return bluetoothAdapter.getBondedDevices();
    }
}
