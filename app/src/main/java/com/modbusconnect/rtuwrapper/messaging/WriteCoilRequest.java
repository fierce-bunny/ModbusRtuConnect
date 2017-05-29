package com.modbusconnect.rtuwrapper.messaging;

import com.modbusconnect.rtuwrapper.processing.Coil;

import static com.modbusconnect.rtuwrapper.ModbusConstants.WRITE_COIL;

public class WriteCoilRequest extends BaseRequest<Coil, Boolean> {

    public WriteCoilRequest() {
        super(WRITE_COIL);
    }

    public WriteCoilRequest(int address, boolean status) {
        super(WRITE_COIL, new Coil(address, status));
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
        return getMessageProcessor().buildWriteMessage(slaveId, getFunctionCode(), getModbusData());
    }

}
