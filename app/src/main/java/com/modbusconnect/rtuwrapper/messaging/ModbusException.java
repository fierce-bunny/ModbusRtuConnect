package com.modbusconnect.rtuwrapper.messaging;

public class ModbusException extends Exception {

    private int code;

    public ModbusException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
