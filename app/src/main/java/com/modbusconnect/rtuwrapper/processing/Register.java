package com.modbusconnect.rtuwrapper.processing;

public class Register extends ModbusDataType<Integer> {

    //use this constructor for responses
    public Register(int value) {
        setValue(value);
    }

    public Register(int address, int value) {
        setAddress(address);
        setValue(value);
    }

    public Integer getValue() {
        return getValueInt();
    }

}
