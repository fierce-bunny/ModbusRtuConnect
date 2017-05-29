package com.modbusconnect.rtuwrapper.messaging;

import com.modbusconnect.rtuwrapper.processing.Coil;

import static com.modbusconnect.rtuwrapper.ModbusConstants.READ_COIL;

public class ReadCoilRequest extends BaseRequest<Coil, Boolean> {

    public ReadCoilRequest() {
        super(READ_COIL);
    }

    public ReadCoilRequest(int address) {
        super(READ_COIL, new Coil(address, false));
    }

    @Override
    public void setData(int address, Boolean value) {
        setModbusDataObject(new Coil(address, value));
    }

    @Override
    public void setValue(Boolean value) {
        setModbusDataObject(new Coil(-1, value));
    }

    @Override
    public byte[] getEncodedMessage(int slaveId) {
        return getMessageProcessor().buildReadMessage(slaveId, getFunctionCode(), getModbusData().getAddress());
    }

}
