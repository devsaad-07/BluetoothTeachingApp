package com.example.chat_prototype1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.IOException;
import java.util.UUID;

public class Teacher extends AppCompatActivity {

    TextView status;
    EditText id, password;
    Button nextButton;
    private Firebase mRootRef;
    Boolean teacherid, teacherpassword;
    BluetoothAdapter bluetoothAdapter;
    public BluetoothSocket socket;

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;

    final String TAG = "devsTag";
    int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final String APP_NAME = "btCHAT";
    private static final UUID MY_UUID = UUID.fromString("08c0797e-869a-11ea-bc55-0242ac130003");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
        teacherid = false;
        teacherpassword = false;
        //Log.i(TAG, "oncreateFRag");
        mRootRef = new Firebase("https://chat-prototype1-139d0.firebaseio.com/Classroom/Users/Teacher");
        //Firebase maRef = mRootRef.child("yo");
        //maRef.setValue("2");

        status = (TextView) findViewById(R.id.status);
        id = (EditText) findViewById(R.id.id);
        password = (EditText) findViewById(R.id.password);
        nextButton = (Button) findViewById(R.id.nextButton);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!bluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BLUETOOTH);
        }

        final String[] tID = new String[1];
        final String[] tPass = new String[1];

        mRootRef.child("id").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.i(TAG,"ondatachange");
                String value = dataSnapshot.getValue(String.class);
                //Toast.makeText(getApplicationContext(),value,Toast.LENGTH_LONG).show();
                tID[0] = value;
                //Toast.makeText(getApplicationContext(),tID[0],Toast.LENGTH_LONG).show();

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //Log.i(TAG, "cancelled");
            }
        });

        mRootRef.child("password").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                tPass[0] = value;
                //Toast.makeText(getApplicationContext(),tPass[0],Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mID = id.getText().toString();
                final String mPass = password.getText().toString();
                String a = mID;
                String b = tID[0];
                String c = mPass;
                String d = tPass[0];

                if (a.equals(b)){
                    teacherid = true;
                    if (c.equals(d)){
                        teacherpassword = true;
                        id.setVisibility(View.GONE);
                        password.setVisibility(View.GONE);
                        nextButton.setVisibility(View.GONE);
                        //status.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_LONG).show();
                        ServerClass serverClass = new ServerClass();
                        serverClass.start();
                    }
                    else{
                        status.setText("Incorrect Password");
                    }
                }
                else {
                    status.setText("Incorrect ID");
                }
            }
        });


    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what){
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

    public class ServerClass extends Thread {
        //Teacher
        BluetoothServerSocket serverSocket;
        int i;
        public ServerClass() {
            //Toast.makeText(getApplicationContext(), "Server me", Toast.LENGTH_LONG).show();
            try {
                i=0;
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {

            while (socket == null) {
                //Toast.makeText(getApplicationContext(),String.valueOf(i),Toast.LENGTH_SHORT).show();
                try {
                    Message message = Message.obtain();
                    message.what = STATE_LISTENING;
                    //Toast.makeText(getApplicationContext(),"run in server try",Toast.LENGTH_LONG).show();
                    handler.sendMessage(message);
                    socket = serverSocket.accept();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Server exception in run method",Toast.LENGTH_LONG).show();
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }

                if (socket != null) {

                    //Toast.makeText(getApplicationContext(),"server connected",Toast.LENGTH_LONG).show();
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);
                    ((ApplicationClass)getApplication()).setBluetoothSocket(socket);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            status.setVisibility(View.GONE);
                        }
                    });
                    FragmentManager fm = getSupportFragmentManager();
                    TeacherFrag fragment = new TeacherFrag();
                    fm.beginTransaction().replace(R.id.teacherFrag,fragment).commit();
                    break;
                }
            }
        }
    }
}
