package com.modbusconnect.rtuwrapper.messaging;

import com.modbusconnect.rtuwrapper.processing.Coil;

import static com.modbusconnect.rtuwrapper.ModbusConstants.READ_COIL;

public class ReadCoilResponse extends BaseResponse<Coil, Boolean> {

    public ReadCoilResponse(byte[] message) {
        super(READ_COIL, message);
    }

    @Override
    public Coil decodeMessage(byte[] message) {
        int value = getMessageProcessor().getReadResponseValue(message, true);
        return new Coil(value);
    }

}
