package com.example.chat_prototype1;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class StudentFrag extends Fragment {

    ApplicationClass app;
    EditText ansText;
    TextView queView, textView, textView2;
    Button sendAns, nextButton;
    BluetoothSocket socket;
    SendReceive sendReceive;
    String rollNo;
    String quizNo;
    int i;
    Boolean flag = false;

    public StudentFrag() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        socket = ((ApplicationClass) getActivity().getApplication()).mySocket();
        i = 1;
        sendReceive = new SendReceive(socket);
        sendReceive.start();
        rollNo = ((ApplicationClass) getActivity().getApplication()).getRollNo();
        sendReceive.write(rollNo.getBytes());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student, container, false);
        ansText = (EditText) view.findViewById(R.id.ansText);
        queView = (TextView) view.findViewById(R.id.queView);
        sendAns = (Button) view.findViewById(R.id.sendAns);
        textView = (TextView) view.findViewById(R.id.textView);
        nextButton = (Button) view.findViewById(R.id.nextButton);
        textView2 = (TextView) view.findViewById(R.id.textView2);
        i = 1;
        textView.setText("Question " + String.valueOf(i));

        nextButton.setVisibility(View.INVISIBLE);


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        sendAns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    String string = String.valueOf(ansText.getText());
                    ansText.getText().clear();
                    //Toast.makeText(getContext(),"onClick",Toast.LENGTH_LONG).show();
                    sendReceive.write(string.getBytes());
                    i++;
                    textView.setText("Question " + String.valueOf(i));
                }
        });
        return view;
    }

    public class SendReceive extends Thread {
        private final BluetoothSocket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive(BluetoothSocket socket1) {
            socket = socket1;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = socket.getInputStream();
                tempOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = tempIn;
            outputStream = tempOut;

        }

        public void run() {

            byte[] buffer = new byte[1024];
            int bytes;
            String s;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    s = new String(buffer, 0, bytes);
                    final String finalS = s;

                    if (finalS.equals("end quiz")) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setVisibility(View.GONE);
                                ansText.setVisibility(View.GONE);
                                queView.setVisibility(View.GONE);
                                sendAns.setVisibility(View.GONE);
                                textView2.setVisibility(View.GONE);
                                nextButton.setVisibility(View.VISIBLE);
                                Toast.makeText(getContext(), "Quiz ended by teacher", Toast.LENGTH_LONG).show();
                                nextButton.setText("HOME");
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                queView.setText(finalS);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
