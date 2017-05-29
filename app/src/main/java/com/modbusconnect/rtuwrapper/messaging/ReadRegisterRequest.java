package com.modbusconnect.rtuwrapper.messaging;

import com.modbusconnect.rtuwrapper.processing.Register;

import static com.modbusconnect.rtuwrapper.ModbusConstants.READ_REGISTER;

public class ReadRegisterRequest extends BaseRequest<Register, Integer> {

    public ReadRegisterRequest() {
        super(READ_REGISTER);
    }

    public ReadRegisterRequest(int address) {
        super(READ_REGISTER, new Register(address, 0));
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
        return getMessageProcessor().buildReadMessage(slaveId, getFunctionCode(), getModbusData().getAddress());
    }

}
