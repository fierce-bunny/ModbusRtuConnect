package com.modbusconnect.rtuwrapper.messaging;

import com.modbusconnect.rtuwrapper.processing.Coil;

import static com.modbusconnect.rtuwrapper.ModbusConstants.WRITE_COIL;

public class WriteCoilResponse extends BaseResponse<Coil, Boolean> {

    public WriteCoilResponse(byte[] message) {
        super(WRITE_COIL, message);
    }

    @Override
    public Coil decodeMessage(byte[] message) {
        int value = getMessageProcessor().getWriteResponseValue(message, true);
        return new Coil(value);
    }

}
