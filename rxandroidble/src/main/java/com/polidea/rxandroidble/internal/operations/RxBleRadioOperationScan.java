package com.polidea.rxandroidble.internal.operations;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.polidea.rxandroidble.exceptions.BleScanException;
import com.polidea.rxandroidble.internal.RxBleInternalScanResult;
import com.polidea.rxandroidble.internal.RxBleLog;
import com.polidea.rxandroidble.internal.RxBleRadioOperation;
import com.polidea.rxandroidble.internal.util.RxBleAdapterWrapper;
import com.polidea.rxandroidble.internal.util.UUIDUtil;

import java.util.List;
import java.util.UUID;

public class RxBleRadioOperationScan extends RxBleRadioOperation<RxBleInternalScanResult> {

    private final UUID[] filterServiceUUIDs;
    private final RxBleAdapterWrapper rxBleAdapterWrapper;
    private final UUIDUtil uuidUtil;
    private volatile boolean isStarted = false;
    private volatile boolean isStopped = false;

    private final BluetoothAdapter.LeScanCallback leScanCallback = (device, rssi, scanRecord) -> {
        if (!hasDefinedFilter() || hasDefinedFilter() && containsDesiredServiceIds(scanRecord)) {
            onNext(new RxBleInternalScanResult(device, rssi, scanRecord));
        }
    };

    private final ScanCallback leScanCallbackAPI21;

    public RxBleRadioOperationScan(UUID[] filterServiceUUIDs, RxBleAdapterWrapper rxBleAdapterWrapper, UUIDUtil uuidUtil) {
        this.filterServiceUUIDs = filterServiceUUIDs;
        this.rxBleAdapterWrapper = rxBleAdapterWrapper;
        this.uuidUtil = uuidUtil;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            leScanCallbackAPI21 = new ScanCallback() {
                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);
                    for (ScanResult result : results) {
                        nextScanResult(result);
                    }
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                }

                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    nextScanResult(result);
                }

                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                private void nextScanResult(ScanResult scanResult) {
                    ScanRecord scanRecord = scanResult.getScanRecord();
                    if (scanRecord != null && scanRecord.getBytes() != null) {
                        if (!hasDefinedFilter() || hasDefinedFilter() && containsDesiredServiceIds(scanRecord.getBytes())) {
                            onNext(new RxBleInternalScanResult(scanResult.getDevice(), scanResult.getRssi(), scanRecord.getBytes()));
                        }
                    }
                }
            };
        } else {
            leScanCallbackAPI21 = null;
        }
    }

    @Override
    protected void protectedRun() {

        try {
            boolean startLeScanStatus;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startLeScanStatus = rxBleAdapterWrapper.startLeScan(leScanCallbackAPI21);
            } else {
                startLeScanStatus = rxBleAdapterWrapper.startLeScan(leScanCallback);
            }

            if (!startLeScanStatus) {
                onError(new BleScanException(BleScanException.BLUETOOTH_CANNOT_START));
            } else {
                synchronized (this) { // synchronization added for stopping the scan
                    isStarted = true;
                    if (isStopped) {
                        stop();
                    }
                }
            }
        } catch (Throwable throwable) {
            isStarted = true;
            RxBleLog.e(throwable, "Error while calling BluetoothAdapter.startLeScan()");
            onError(new BleScanException(BleScanException.BLUETOOTH_CANNOT_START));
        } finally {
            releaseRadio();
        }
    }

    // synchronized keyword added to be sure that operation will be stopped no matter which thread will call it
    public synchronized void stop() {
        isStopped = true;
        if (isStarted) {
            // TODO: [PU] 29.01.2016 https://code.google.com/p/android/issues/detail?id=160503
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rxBleAdapterWrapper.stopLeScan(leScanCallbackAPI21);
            } else {
                rxBleAdapterWrapper.stopLeScan(leScanCallback);
            }
        }
    }

    private boolean containsDesiredServiceIds(byte[] scanRecord) {
        List<UUID> advertisedUUIDs = uuidUtil.extractUUIDs(scanRecord);

        for (UUID desiredUUID : filterServiceUUIDs) {

            if (!advertisedUUIDs.contains(desiredUUID)) {
                return false;
            }
        }

        return true;
    }

    private boolean hasDefinedFilter() {
        return filterServiceUUIDs != null && filterServiceUUIDs.length > 0;
    }
}
