package com.example.chat_prototype1;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.firebase.client.Firebase;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class TeacherFrag extends Fragment {

    ApplicationClass app;
    EditText queText, aquizNo;
    TextView ansView, textView, textView2;
    Button sendQue, endQuiz, nextButton;
    BluetoothSocket socket;
    SendReceive sendReceive;
    String rollNo, quizNo;
    private Firebase mRootRef;
    private Firebase childRef;
    int i;

    public TeacherFrag() {
        // Required empty public constructor
    }


    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        socket = ((ApplicationClass)getActivity().getApplication()).mySocket();
        i = 1;
        mRootRef = new Firebase("https://chat-prototype1-139d0.firebaseio.com/Quiz");
        sendReceive = new SendReceive(socket);
        sendReceive.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher, container, false);
        queText = (EditText) view.findViewById(R.id.queText);
        ansView = (TextView) view.findViewById(R.id.ansView);
        sendQue = (Button) view.findViewById(R.id.sendQue);
        endQuiz = (Button) view.findViewById(R.id.endQuiz);
        textView = (TextView) view.findViewById(R.id.textView);
        aquizNo = (EditText) view.findViewById(R.id.quizNo);
        nextButton = (Button) view.findViewById(R.id.nextButton);
        textView2 = (TextView) view.findViewById(R.id.textView2);

        i = 1;
        textView.setVisibility(View.INVISIBLE);
        queText.setVisibility(View.INVISIBLE);
        ansView.setVisibility(View.INVISIBLE);
        endQuiz.setVisibility(View.INVISIBLE);
        sendQue.setVisibility(View.INVISIBLE);
        textView2.setVisibility(View.INVISIBLE);


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = String.valueOf(aquizNo.getText());
                sendReceive.write(string.getBytes());
                quizNo = string;
                ((ApplicationClass)getActivity().getApplication()).setQuizNo(string);
                nextButton.setVisibility(View.GONE);
                aquizNo.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                queText.setVisibility(View.VISIBLE);
                ansView.setVisibility(View.VISIBLE);
                endQuiz.setVisibility(View.VISIBLE);
                sendQue.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.VISIBLE);
                textView.setText("Question " + String.valueOf(i));
            }
        });


        sendQue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String string = String.valueOf(queText.getText());
                //Toast.makeText(getContext(),"onClick",Toast.LENGTH_LONG).show();
                sendReceive.write(string.getBytes());

                childRef = mRootRef.child("Quiz" + quizNo);
                Firebase QueRef = childRef.child("Questions");
                Firebase a = QueRef.child("Question" + String.valueOf(i));
                a.setValue(string);
                i++;
                textView.setText("Question " + String.valueOf(i));
            }
        });

        endQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = "end quiz";
                sendReceive.write(string.getBytes());
                try {
                    socket.close();
                    Firebase marksRef = childRef.child("no");
                    marksRef.setValue(i-1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                getActivity().onBackPressed();
            }
        });

        return view;
    }


    public class SendReceive extends Thread{
        private final BluetoothSocket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive (BluetoothSocket socket1){
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

        public void run(){

            byte[] buffer = new byte[1024];
            int bytes;
            String s;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    s = new String(buffer, 0, bytes);
                    final String finalS = s;
                    if(rollNo == null){
                        rollNo = s;
                        ((ApplicationClass)getActivity().getApplication()).setRollNo(rollNo);
                    }
                    else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ansView.setText(finalS);
                        }
                    });}
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes){
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
