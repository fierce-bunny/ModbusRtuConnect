package com.modbusconnect.rtuwrapper.messaging;

import com.modbusconnect.rtuwrapper.processing.Register;

import static com.modbusconnect.rtuwrapper.ModbusConstants.READ_REGISTER;

public class ReadRegisterResponse extends BaseResponse<Register, Integer> {

    public ReadRegisterResponse(byte[] message) {
        super(READ_REGISTER, message);
    }

    @Override
    public Register decodeMessage(byte[] message) {
        int value = getMessageProcessor().getReadResponseValue(message, false);
        return new Register(-1, value);
    }

}
