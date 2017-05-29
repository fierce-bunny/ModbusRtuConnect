package com.modbusconnect.rtuwrapper.processing;

public abstract class ModbusDataType<T> {

    private byte[] address = new byte[2];
    private byte[] value = new byte[2];

    ModbusDataType() {
    }

    void setAddress(int addr) {
        intToBytes(addr, address);
    }

    void setValue(int val) {
        intToBytes(val, value);
    }

    void setValueBoolean(boolean val) {
        if (val) {
            value[0] = (byte) 0xff;
        } else {
            value[0] = (byte) 0x00;
        }
        value[1] = (byte) 0x00;
    }

    int getValueInt() {
        return intFromBytes(value);
    }

    private int intFromBytes(byte[] bytes) {
        return ((bytes[0] & 0xff) << 8 | (bytes[1] & 0xff));
    }

    private void intToBytes(int val, byte[] holder) {
        holder[0] = (byte) (0xff & (val >> 8));
        holder[1] = (byte) (0xff & val);
    }

    public int getAddress() {
        return intFromBytes(address);
    }

    public byte[] getAddressBytes() {
        return address;
    }

    public byte[] getValueBytes() {
        return value;
    }

    public abstract T getValue();

}
