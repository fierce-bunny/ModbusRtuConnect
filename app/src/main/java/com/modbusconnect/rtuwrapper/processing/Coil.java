package com.modbusconnect.rtuwrapper.processing;

public class Coil extends ModbusDataType<Boolean> {

    //use this constructor for responses
    public Coil(int value) {
        setValue(value);
    }

    public Coil(int address, boolean value) {
        setAddress(address);
        setValueBoolean(value);
    }

    public Boolean getValue() {
        return getValueInt() == 1;
    }

}
