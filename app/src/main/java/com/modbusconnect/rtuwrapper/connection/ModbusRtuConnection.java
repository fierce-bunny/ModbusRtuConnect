package com.modbusconnect.rtuwrapper.connection;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.support.annotation.Nullable;

import com.felhr.usbserial.UsbSerialDevice;
import com.modbusconnect.App;

import java.util.HashMap;
import java.util.Map;

// TODO: 23.05.17 add reconnection logic
public class ModbusRtuConnection {

    public interface OnConnectedToPlcListener {
        void onConnected();
    }

    private static final String ACTION_USB_PERMISSION = "com.modbusconnect.USB_PERMISSION";

    private Context context;
    private int vendorId;

    private UsbManager usbManager;
    private UsbDevice usbDevice;
    private UsbSerialDevice usbSerialDevice;
    private UsbDeviceConnection usbDeviceConnection;
    private SerialParameters serialParameters;

    private BroadcastReceiver broadcastReceiver;

    private boolean connectOnDeviceAttached;

    private OnConnectedToPlcListener listener;

    public ModbusRtuConnection(Context context, int vendorId, boolean connectOnDeviceAttached) {
        this.vendorId = vendorId;
        this.connectOnDeviceAttached = connectOnDeviceAttached;
        this.context = context;
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        createBroadcastReceiver();
        registerReceiver();
    }

    private void createBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                    boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                    if (granted) {
                        if (usbDevice != null) {
                            usbDeviceConnection = usbManager.openDevice(usbDevice);
                            openUsbSerialDevice(serialParameters);
                        } else {
                            if (App.IS_DEBUG) System.out.println("USB - USB DEVICE IS NULL");
                        }
                    } else {
                        if (App.IS_DEBUG) System.out.println("USB - PERMISSION NOT GRANTED");
                    }
                } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                    if (App.IS_DEBUG) System.out.println("USB - DEVICE ATTACHED");
                    if (connectOnDeviceAttached) connect(null);
                } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                    if (App.IS_DEBUG) System.out.println("USB - DEVICE DETACHED");
                    // TODO: 22.05.17 check if gets disconnected when another device is attached
                    disconnect();
                }
            }
        };
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        if (broadcastReceiver != null) {
            context.registerReceiver(broadcastReceiver, filter);
        }
    }

    private void findDevice() {
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                if (entry.getValue().getVendorId() == vendorId) { //vendor ID check
                    if (App.IS_DEBUG) System.out.println("USB - VENDOR ID MATCH");
                    usbDevice = entry.getValue();
                    break;
                }
            }
        } else if (App.IS_DEBUG) {
            System.out.println("USB DEVICES LIST IS EMPTY");
        }
    }

    private void openUsbSerialDevice(SerialParameters serialParameters) {
        usbSerialDevice = UsbSerialDevice.createUsbSerialDevice(usbDevice, usbDeviceConnection);
        if (usbSerialDevice != null) {
            if (usbSerialDevice.open()) { //Set Serial Connection Parameters.
                if (serialParameters == null) {
                    serialParameters = SerialParameters.getDefault();
                }
                usbSerialDevice.setBaudRate(serialParameters.baudRate);
                usbSerialDevice.setDataBits(serialParameters.databits);
                usbSerialDevice.setStopBits(serialParameters.stopbits);
                usbSerialDevice.setParity(serialParameters.parity);
                usbSerialDevice.setFlowControl(serialParameters.flowControl);
                if (listener != null) {
                    listener.onConnected();
                }
            } else {
                if (App.IS_DEBUG) System.out.println("USB - SERIAL DEVICE NOT OPEN");
            }
        } else {
            if (App.IS_DEBUG) System.out.println("USB - SERIAL DEVICE IS NULL");
        }
    }

    @Nullable
    public UsbSerialDevice getUsbSerialDevice() {
        if (usbSerialDevice != null) {
            return usbSerialDevice;
        }
        return null;
    }


    //--PUBLIC METHODS--//

    /**
     * Set custom serial parameters before calling <b>connect()</b>.
     * Won't work if <b>connectOnDeviceAttached = true</b>, default serial parameters
     * will be applied.
     */
    public void setSerialParameters(SerialParameters serialParameters) {
        this.serialParameters = serialParameters;
    }

    public void connect(OnConnectedToPlcListener listener) {
        this.listener = listener;
        findDevice();
        if (usbDevice != null) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                    new Intent(ACTION_USB_PERMISSION), 0);
            usbManager.requestPermission(usbDevice, pendingIntent);
        }
    }

    public void disconnect() {
        if (isConnected()) {
            usbSerialDevice.close();
        }
    }

    public boolean isConnected() {
        return usbSerialDevice != null;
    }

}
