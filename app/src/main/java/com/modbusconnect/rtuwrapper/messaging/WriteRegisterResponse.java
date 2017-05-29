package com.modbusconnect.rtuwrapper.messaging;

import com.modbusconnect.rtuwrapper.processing.Register;

import static com.modbusconnect.rtuwrapper.ModbusConstants.WRITE_REGISTER;

public class WriteRegisterResponse extends BaseResponse<Register, Integer> {

    public WriteRegisterResponse(byte[] message) {
        super(WRITE_REGISTER, message);
    }

    @Override
    public Register decodeMessage(byte[] message) {
        int value = getMessageProcessor().getWriteResponseValue(message, false);
        return new Register(value);
    }

}
