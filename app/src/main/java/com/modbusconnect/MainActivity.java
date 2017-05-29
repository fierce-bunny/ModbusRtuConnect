package com.modbusconnect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.modbusconnect.rtuwrapper.connection.ModbusRtuConnection;
import com.modbusconnect.rtuwrapper.io.ModbusRtuTransaction;
import com.modbusconnect.rtuwrapper.messaging.ModbusException;
import com.modbusconnect.rtuwrapper.messaging.ReadCoilRequest;
import com.modbusconnect.rtuwrapper.messaging.ReadCoilResponse;
import com.modbusconnect.rtuwrapper.messaging.ReadRegisterRequest;
import com.modbusconnect.rtuwrapper.messaging.ReadRegisterResponse;
import com.modbusconnect.rtuwrapper.messaging.WriteCoilRequest;
import com.modbusconnect.rtuwrapper.messaging.WriteCoilResponse;
import com.modbusconnect.rtuwrapper.messaging.WriteRegisterRequest;
import com.modbusconnect.rtuwrapper.messaging.WriteRegisterResponse;

import static com.modbusconnect.rtuwrapper.ModbusConstants.VENDOR_ID;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    EditText etRegNum;
    EditText etRegVal;
    EditText editText;
    ToggleButton toggleButton;

    Button buttonWriteRegister;
    Button buttonWriteCoil;
    Button buttonSetAllCoils;
    Button buttonReadRegs;
    Button buttonReadCoils;

    ModbusRtuConnection connection;
    ModbusRtuTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        editText = (EditText) findViewById(R.id.editText);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        etRegNum = (EditText) findViewById(R.id.etRegNum);
        etRegVal = (EditText) findViewById(R.id.etRegVal);

        System.out.println(etRegNum.getText().toString());

        Button buttonConnect = (Button) findViewById(R.id.buttonConnect);
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });

        buttonWriteRegister = (Button) findViewById(R.id.buttonWriteRegister);
        buttonWriteRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int address = Integer.parseInt(etRegNum.getText().toString());
                int value = Integer.parseInt(etRegVal.getText().toString());
                WriteRegisterRequest request = new WriteRegisterRequest(address, value);
                WriteRegisterResponse response;
                try {
                    transaction.execute(request);
                    response = (WriteRegisterResponse) transaction.getResponse();
                    log("reg " + address + " = " + response.getValue());
                } catch (ModbusException e) {
                    e.printStackTrace();
                }
            }
        });
        buttonWriteCoil = (Button) findViewById(R.id.buttonWriteCoil);
        buttonWriteCoil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int address = Integer.parseInt(editText.getText().toString());
                boolean value = toggleButton.isChecked();
                WriteCoilRequest request = new WriteCoilRequest(address, value);
                WriteCoilResponse response;
                try {
                    transaction.execute(request);
                    response = (WriteCoilResponse) transaction.getResponse();
                    log("coil " + address + " = " + response.getValue());
                } catch (ModbusException e) {
                    e.printStackTrace();
                }
            }
        });
        buttonSetAllCoils = (Button) findViewById(R.id.buttonWriteAllCoils);
        buttonSetAllCoils.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllCoilsOnOff();
            }
        });
        buttonReadRegs = (Button) findViewById(R.id.buttonReadRegs);
        buttonReadRegs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadRegisterRequest request = new ReadRegisterRequest();
                ReadRegisterResponse response;
                for (int i = 0; i < 7; i++) {
                    request.setData(i, 0);
                    try {
                        transaction.execute(request);
                    } catch (ModbusException e) {
                        e.printStackTrace();
                    }
                    try {
                        response = (ReadRegisterResponse) transaction.getResponse();
                        log("reg " + i + " = " + response.getValue());
                    } catch (ModbusException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        buttonReadCoils = (Button) findViewById(R.id.buttonReadCoils);
        buttonReadCoils.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadCoilRequest request = new ReadCoilRequest();

                ReadCoilResponse response;
                for (int i = 1000; i < 1007; i++) {
                    request.setData(i, false);
                    try {
                        transaction.execute(request);
                    } catch (ModbusException e) {
                        e.printStackTrace();
                    }
                    try {
                        response = (ReadCoilResponse) transaction.getResponse();
                        log("coil " + i + " = " + response.getValue());
                    } catch (ModbusException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        enableUi(false);
    }

    @Override
    protected void onDestroy() {
        if (connection != null) {
            connection.disconnect();
        }
        super.onDestroy();
    }

    private void enableUi(boolean enable) {
        buttonWriteRegister.setEnabled(enable);
        buttonWriteCoil.setEnabled(enable);
        buttonSetAllCoils.setEnabled(enable);
        buttonReadRegs.setEnabled(enable);
        buttonReadCoils.setEnabled(enable);
    }

    private void log(String text) {
        textView.setText(textView.getText().toString() + " // " + text);
    }

    private void connect() {
        connection = new ModbusRtuConnection(getApplicationContext(), VENDOR_ID, false);
        connection.connect(new ModbusRtuConnection.OnConnectedToPlcListener() {
            @Override
            public void onConnected() {
                if (connection.isConnected()) {
                    transaction = new ModbusRtuTransaction(connection);
                }
                log("Connected - " + connection.isConnected());
                enableUi(true);
            }
        });
    }

    //setCoilsOnOff vals
    int current = 1000;
    int startAddress = 1000;
    int endAddress = 1006;
    boolean isUp = true;

    private void setAllCoilsOnOff() {
        if (connection == null || !connection.isConnected()) return;
        WriteCoilRequest request = new WriteCoilRequest();

        if (current == endAddress) {
            isUp = false;
        } else if (current == startAddress) {
            isUp = true;
        }

        try {
            if (isUp) {
                while (current < endAddress) {
                    request.setData(current, true);
                    transaction.execute(request);
                    ++current;
                }
                if (current == endAddress) {
                    request.setData(current, true);
                    transaction.execute(request);
                }
            } else {
                while (current > startAddress) {
                    request.setData(current, false);
                    transaction.execute(request);
                    current--;
                }
                if (current == startAddress) {
                    request.setData(current, false);
                    transaction.execute(request);
                }
            }
        } catch (ModbusException e) {
            e.printStackTrace();
        }
    }

}
