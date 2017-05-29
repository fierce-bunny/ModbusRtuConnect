package com.modbusconnect.rtuwrapper.messaging;

import com.modbusconnect.rtuwrapper.processing.MessageProcessor;
import com.modbusconnect.rtuwrapper.processing.ModbusDataType;

public abstract class BaseRequest<T extends ModbusDataType, V> implements BaseMessage {

    private MessageProcessor messageProcessor;

    private byte functionCode;
    private T modbusData;

    BaseRequest(byte functionCode) {
        this.messageProcessor = MessageProcessor.getInstance();
        this.functionCode = functionCode;
    }

    BaseRequest(byte functionCode, T modbusData) {
        this.messageProcessor = MessageProcessor.getInstance();
        this.functionCode = functionCode;
        this.modbusData = modbusData;
    }

    @Override
    public byte getFunctionCode() {
        return functionCode;
    }

    protected MessageProcessor getMessageProcessor() {
        return messageProcessor;
    }

    protected T getModbusData() {
        return modbusData;
    }

    protected void setModbusDataObject(T modbusData) {
        this.modbusData = modbusData;
    }

    public abstract void setData(int address, V value);

    public abstract void setValue(V value);

    public abstract byte[] getEncodedMessage(int slaveId);

}
