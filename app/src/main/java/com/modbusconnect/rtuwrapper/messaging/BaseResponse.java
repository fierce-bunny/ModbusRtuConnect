package com.modbusconnect.rtuwrapper.messaging;

import com.modbusconnect.rtuwrapper.processing.MessageProcessor;
import com.modbusconnect.rtuwrapper.processing.ModbusDataType;

public abstract class BaseResponse<T extends ModbusDataType, V> implements BaseMessage {

    private MessageProcessor messageProcessor;

    private byte functionCode;
    private T modbusData;

    BaseResponse(byte functionCode, byte[] message) {
        this.messageProcessor = MessageProcessor.getInstance();
        this.functionCode = functionCode;
        this.modbusData = decodeMessage(message);
    }

    @Override
    public byte getFunctionCode() {
        return functionCode;
    }

    protected MessageProcessor getMessageProcessor() {
        return messageProcessor;
    }

    public V getValue() {
        return (V) modbusData.getValue();
    }

    public abstract T decodeMessage(byte[] message);

}
