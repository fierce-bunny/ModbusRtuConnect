package com.modbusconnect.rtuwrapper.io;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.modbusconnect.rtuwrapper.ModbusConstants;
import com.modbusconnect.rtuwrapper.connection.ModbusRtuConnection;
import com.modbusconnect.rtuwrapper.messaging.BaseRequest;
import com.modbusconnect.rtuwrapper.messaging.BaseResponse;
import com.modbusconnect.rtuwrapper.messaging.ModbusException;
import com.modbusconnect.rtuwrapper.messaging.ReadCoilResponse;
import com.modbusconnect.rtuwrapper.messaging.ReadRegisterResponse;
import com.modbusconnect.rtuwrapper.messaging.WriteCoilResponse;
import com.modbusconnect.rtuwrapper.messaging.WriteRegisterResponse;
import com.modbusconnect.rtuwrapper.processing.MessageProcessor;

import java.util.ArrayList;
import java.util.List;

import static com.modbusconnect.rtuwrapper.ModbusConstants.DEFAULT_WAIT_RESPONSE_TIMEOUT;
import static com.modbusconnect.rtuwrapper.ModbusConstants.EXCEPTION_OFFSET;
import static com.modbusconnect.rtuwrapper.ModbusConstants.INVALID_RESPONSE_EXCEPTION;
import static com.modbusconnect.rtuwrapper.ModbusConstants.MAX_SLAVE_ID;
import static com.modbusconnect.rtuwrapper.ModbusConstants.NO_CRC_EXCEPTION_RESPONSE_SIZE;
import static com.modbusconnect.rtuwrapper.ModbusConstants.NO_CRC_READ_RESPONSE_SIZE;
import static com.modbusconnect.rtuwrapper.ModbusConstants.NO_CRC_WRITE_RESPONSE_SIZE;
import static com.modbusconnect.rtuwrapper.ModbusConstants.NO_RESPONSE_EXCEPTION;
import static com.modbusconnect.rtuwrapper.ModbusConstants.READ_COIL;
import static com.modbusconnect.rtuwrapper.ModbusConstants.READ_REGISTER;
import static com.modbusconnect.rtuwrapper.ModbusConstants.UNKNOWN_FUNCTION_CODE_EXCEPTION;
import static com.modbusconnect.rtuwrapper.ModbusConstants.WRITE_COIL;
import static com.modbusconnect.rtuwrapper.ModbusConstants.WRITE_REGISTER;

public class ModbusRtuTransaction implements UsbSerialInterface.UsbReadCallback {

    private int slaveId = ModbusConstants.DEFAULT_SLAVE_ID;

    private MessageProcessor messageProcessor;
    private UsbSerialDevice usbSerialDevice;

    private BaseRequest request;
    private BaseResponse response;
    private List<Byte> responseMessage;

    public ModbusRtuTransaction(ModbusRtuConnection connection) {
        messageProcessor = MessageProcessor.getInstance();
        responseMessage = new ArrayList<>();
        this.usbSerialDevice = connection.getUsbSerialDevice();
        if (usbSerialDevice != null) {
            usbSerialDevice.read(this);
        }
    }

    @Override
    public void onReceivedData(byte[] bytes) {
        for (byte b : bytes) {
            responseMessage.add(b);
        }
    }

    public void setSlaveId(int slaveId) {
        if (slaveId > MAX_SLAVE_ID || slaveId < 0) {
            this.slaveId = ModbusConstants.DEFAULT_SLAVE_ID;
        } else {
            this.slaveId = slaveId;
        }
    }

    public BaseResponse getResponse() throws ModbusException {
        if (response == null) {
            throw new ModbusException(NO_RESPONSE_EXCEPTION, messageProcessor.defyExceptionMessage(NO_RESPONSE_EXCEPTION));
        }
        return response;
    }

    public synchronized void execute(BaseRequest request) throws ModbusException {
        if (request == null || usbSerialDevice == null) return;
        this.request = request;
        response = null;
        byte[] message = request.getEncodedMessage(slaveId);
        responseMessage.clear();
        usbSerialDevice.write(message);

        int responseSizeNoCrc = getResponseExpectedSize(request.getFunctionCode());
        boolean exceptionChecked = false;
        boolean isException = false;

        //wait for response
        try {
            Thread.sleep(DEFAULT_WAIT_RESPONSE_TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (responseMessage.size() < responseSizeNoCrc + 1) {
            if (!exceptionChecked && responseMessage.size() >= 2) {
                if (isExceptionPreliminary(responseMessage.get(1))) {
                    isException = true;
                    responseSizeNoCrc = NO_CRC_EXCEPTION_RESPONSE_SIZE;
                }
                exceptionChecked = true;
            }
        }

        if (isException) {
            throw messageProcessor.getException(responseMessage);
        }
        parseResponse(convertToBytesArray(responseMessage));
    }

    private boolean isExceptionPreliminary(byte b) {
        return (b + EXCEPTION_OFFSET) == request.getFunctionCode();
    }

    private int getResponseExpectedSize(int functionCode) throws ModbusException {
        switch (functionCode) {
            case READ_COIL:
                return NO_CRC_READ_RESPONSE_SIZE;
            case READ_REGISTER:
                return NO_CRC_READ_RESPONSE_SIZE;
            case WRITE_COIL:
                return NO_CRC_WRITE_RESPONSE_SIZE;
            case WRITE_REGISTER:
                return NO_CRC_WRITE_RESPONSE_SIZE;
            default:
                throw new ModbusException(UNKNOWN_FUNCTION_CODE_EXCEPTION,
                        messageProcessor.defyExceptionMessage(UNKNOWN_FUNCTION_CODE_EXCEPTION));
        }
    }

    private void parseResponse(byte[] message) throws ModbusException {
        if (response == null) {
            switch (request.getFunctionCode()) {
                case READ_COIL:
                    response = new ReadCoilResponse(message);
                    break;
                case READ_REGISTER:
                    response = new ReadRegisterResponse(message);
                    break;
                case WRITE_COIL:
                    response = new WriteCoilResponse(message);
                    break;
                case WRITE_REGISTER:
                    response = new WriteRegisterResponse(message);
                    break;
                default:
                    throw new ModbusException(INVALID_RESPONSE_EXCEPTION,
                            messageProcessor.defyExceptionMessage(INVALID_RESPONSE_EXCEPTION));
            }
        }
    }

    private byte[] convertToBytesArray(List<Byte> bytes) {
        byte[] ret = new byte[bytes.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = bytes.get(i);
        }
        return ret;
    }

}
