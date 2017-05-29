package com.modbusconnect.rtuwrapper.processing;

import android.content.Context;

import com.modbusconnect.App;
import com.modbusconnect.R;
import com.modbusconnect.rtuwrapper.messaging.ModbusException;

import java.util.List;

import static com.modbusconnect.rtuwrapper.ModbusConstants.ACKNOWLEDGE_EXCEPTION;
import static com.modbusconnect.rtuwrapper.ModbusConstants.EXCEPTION_OFFSET;
import static com.modbusconnect.rtuwrapper.ModbusConstants.GATEWAY_PATH_UNAVAILABLE_EXCEPTION;
import static com.modbusconnect.rtuwrapper.ModbusConstants.ILLEGAL_DATA_ADDRESS_EXCEPTION;
import static com.modbusconnect.rtuwrapper.ModbusConstants.ILLEGAL_DATA_VALUE_EXCEPTION;
import static com.modbusconnect.rtuwrapper.ModbusConstants.ILLEGAL_FUNCTION_EXCEPTION;
import static com.modbusconnect.rtuwrapper.ModbusConstants.INVALID_RESPONSE_EXCEPTION;
import static com.modbusconnect.rtuwrapper.ModbusConstants.MEMORY_PARITY_EXCEPTION;
import static com.modbusconnect.rtuwrapper.ModbusConstants.NEGATIVE_ACKNOWLEDGE_EXCEPTION;
import static com.modbusconnect.rtuwrapper.ModbusConstants.NO_RESPONSE_EXCEPTION;
import static com.modbusconnect.rtuwrapper.ModbusConstants.SLAVE_DEVICE_BUSY_EXCEPTION;
import static com.modbusconnect.rtuwrapper.ModbusConstants.SLAVE_DEVICE_FAILURE_EXCEPTION;
import static com.modbusconnect.rtuwrapper.ModbusConstants.TARGET_DEVICE_RESPONCE_EXCEPTION;

public class MessageProcessor {

    private static final MessageProcessor instance = new MessageProcessor();

    public static MessageProcessor getInstance() {
        return instance;
    }

    private MessageProcessor() {
    }

    private int getCRC(byte[] input) {
        int iPos = 0; // loop position in input buffer
        int crc = 0xFFFF;
        while (iPos < input.length) {
            crc ^= (input[iPos] & 0xFF);
            iPos++;
            for (int j = 0; j < 8; j++) {
                boolean bitOne = ((crc & 0x1) == 0x1);
                crc >>>= 1;
                if (bitOne) {
                    crc ^= 0x0000A001;
                }
            }
        }
        return crc;
    }

    private byte[] addCRC(byte[] input) {
        byte[] result = new byte[input.length + 2];
        int crc = getCRC(input);
        System.arraycopy(input, 0, result, 0, input.length);
        result[input.length] = (byte) crc;
        result[input.length + 1] = (byte) (crc >> 8);
        return result;
    }

    private byte[] getBytesFromInt(int val) {
        byte[] holder = new byte[2];
        holder[0] = (byte) (0xff & (val >> 8));
        holder[1] = (byte) (0xff & val);
        return holder;
    }

    private int getIntFromBytes(byte[] bytes) {
        return ((bytes[0] & 0xff) << 8 | (bytes[1] & 0xff));
    }

    private byte getFunctionCode(List<Byte> data) {
        return data.get(1);
    }

    public boolean isException(List<Byte> data) {
        return data.size() >= 3 &
                (getFunctionCode(data) == data.get(1) + EXCEPTION_OFFSET);
    }

    public ModbusException getException(List<Byte> data) {
        int code = data.get(2);
        return new ModbusException(code, defyExceptionMessage(code));
    }

    //write coil or register
    public byte[] buildWriteMessage(int slaveId, byte functionCode, ModbusDataType data) {
        byte[] address = data.getAddressBytes();
        byte[] value = data.getValueBytes();
        byte[] messageBytes = {(byte) slaveId, functionCode, address[0], address[1], value[0], value[1]};
        return addCRC(messageBytes);
    }

    public byte[] buildReadMessage(int slaveId, byte functionCode, int address) {
        byte[] addressBytes = getBytesFromInt(address);
        byte[] numberBytes = getBytesFromInt(1);
        byte[] messageBytes = {(byte) slaveId, functionCode, addressBytes[0], addressBytes[1], numberBytes[0], numberBytes[1]};
        return addCRC(messageBytes);
    }

    public int getReadResponseValue(byte[] response, boolean isCoil) {
        if (isCoil) {
            return response[3];
        }
        return getIntFromBytes(new byte[]{response[3], response[4]});
    }

    public int getWriteResponseValue(byte[] response, boolean isCoil) {
        if (isCoil) {
            return response[4] == 0 ? 0 : 1;
        }
        return getIntFromBytes(new byte[]{response[4], response[5]});
    }

    public String defyExceptionMessage(int code) {
        Context context = App.getContext();
        String message;
        switch (code) {
            case NO_RESPONSE_EXCEPTION:
                message = context.getString(R.string.modbus_err_0);
                break;
            case ILLEGAL_FUNCTION_EXCEPTION:
                message = context.getString(R.string.modbus_err_1);
                break;
            case ILLEGAL_DATA_ADDRESS_EXCEPTION:
                message = context.getString(R.string.modbus_err_2);
                break;
            case ILLEGAL_DATA_VALUE_EXCEPTION:
                message = context.getString(R.string.modbus_err_3);
                break;
            case SLAVE_DEVICE_FAILURE_EXCEPTION:
                message = context.getString(R.string.modbus_err_4);
                break;
            case ACKNOWLEDGE_EXCEPTION:
                message = context.getString(R.string.modbus_err_5);
                break;
            case SLAVE_DEVICE_BUSY_EXCEPTION:
                message = context.getString(R.string.modbus_err_6);
                break;
            case NEGATIVE_ACKNOWLEDGE_EXCEPTION:
                message = context.getString(R.string.modbus_err_7);
                break;
            case MEMORY_PARITY_EXCEPTION:
                message = context.getString(R.string.modbus_err_8);
                break;
            case GATEWAY_PATH_UNAVAILABLE_EXCEPTION:
                message = context.getString(R.string.modbus_err_9);
                break;
            case TARGET_DEVICE_RESPONCE_EXCEPTION:
                message = context.getString(R.string.modbus_err_10);
                break;
            case INVALID_RESPONSE_EXCEPTION:
                message = context.getString(R.string.modbus_err_invalid_resp);
                break;
            default:
                message = context.getString(R.string.modbus_err_unknown);
                break;
        }
        return message;
    }

}
