package com.modbusconnect.rtuwrapper.messaging;

import com.modbusconnect.rtuwrapper.processing.Register;

import static com.modbusconnect.rtuwrapper.ModbusConstants.WRITE_REGISTER;

public class WriteRegisterRequest extends BaseRequest<Register, Integer> {

    public WriteRegisterRequest() {
        super(WRITE_REGISTER);
    }

    public WriteRegisterRequest(int address, int value) {
        super(WRITE_REGISTER, new Register(address, value));
    }

    @Override
    public void setData(int address, Integer value) {
        setModbusDataObject(new Register(address, value));
    }

    @Override
    public void setValue(Integer value) {
        setModbusDataObject(new Register(-1, value));
    }

    @Override
    public byte[] getEncodedMessage(int slaveId) {
        return getMessageProcessor().buildWriteMessage(slaveId, getFunctionCode(), getModbusData());
    }

}
