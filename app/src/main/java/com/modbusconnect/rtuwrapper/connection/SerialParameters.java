package com.modbusconnect.rtuwrapper.connection;

import com.felhr.usbserial.UsbSerialInterface;

class SerialParameters {

    int baudRate;
    int flowControl;
    int databits;
    int stopbits;
    int parity;

    static SerialParameters getDefault() {
        return new SerialParameters();
    }

    private SerialParameters() {
        this.baudRate = 9600;
        this.flowControl = UsbSerialInterface.FLOW_CONTROL_OFF;
        this.databits = UsbSerialInterface.DATA_BITS_8;
        this.stopbits = UsbSerialInterface.STOP_BITS_1;
        this.parity = UsbSerialInterface.PARITY_EVEN;
    }

    public SerialParameters(int baudRate,
                            int flowControl,
                            int databits,
                            int stopbits,
                            int parity) {
        this.baudRate = baudRate;
        this.flowControl = flowControl;
        this.databits = databits;
        this.stopbits = stopbits;
        this.parity = parity;
    }

}
