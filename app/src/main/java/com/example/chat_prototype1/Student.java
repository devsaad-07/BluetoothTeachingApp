package com.example.chat_prototype1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class Student extends AppCompatActivity {

    Button listDevices;
    ListView listView;
    TextView status;
    String mrollNo;
    EditText rollNo;
    Button nextButton;
    private Firebase mRootRef;
    Boolean studentid, studentpassword, studentRollNo;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] btArray;
    private static final String APP_NAME = "btCHAT";
    private static final UUID MY_UUID = UUID.fromString("08c0797e-869a-11ea-bc55-0242ac130003");

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;

    int REQUEST_ENABLE_BLUETOOTH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        listDevices = (Button) findViewById(R.id.listDevices);
        listView = (ListView) findViewById(R.id.listView);
        status = (TextView) findViewById(R.id.status);

        listView.setVisibility(View.INVISIBLE);
        listDevices.setVisibility(View.INVISIBLE);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        }

        rollNo = (EditText) findViewById(R.id.rollNo);
        nextButton = (Button) findViewById(R.id.nextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mrollNo = rollNo.getText().toString();
                ((ApplicationClass)getApplication()).setRollNo(mrollNo);
                listView.setVisibility(View.VISIBLE);
                listDevices.setVisibility(View.VISIBLE);
                rollNo.setVisibility(View.GONE);
                nextButton.setVisibility(View.GONE);
            }
        });

        listDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listDevices.setVisibility(View.GONE);
                status.setText("Choose Teacher's Bluetooth Device");
                Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();
                String[] strings = new String[bt.size()];
                btArray = new BluetoothDevice[bt.size()];
                int index = 0;

                if (bt.size() > 0) {
                    for (BluetoothDevice device : bt) {
                        btArray[index] = device;
                        strings[index] = device.getName();
                        index++;
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, strings);
                    listView.setAdapter(arrayAdapter);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                status.setVisibility(View.GONE);
                listView.setVisibility(View.GONE);
                ClientClass clientClass = new ClientClass(btArray[i]);
                clientClass.start();
                status.setText("Connecting");
            }
        });
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case STATE_LISTENING:
                    status.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    status.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    status.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("Connection Failed");
                    break;
            }
            return false;
        }
    });

    public class ClientClass extends Thread {
        private BluetoothDevice device;
        public BluetoothSocket socket;

        public ClientClass(BluetoothDevice device1) {
            device = device1;
            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                socket.connect();
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);
                ((ApplicationClass) getApplication()).setBluetoothSocket(socket);
                status.setVisibility(View.GONE);
                FragmentManager fm = getSupportFragmentManager();
                StudentFrag fragment = new StudentFrag();
                fm.beginTransaction().replace(R.id.studentFrag, fragment).commit();

            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }

    }
}
