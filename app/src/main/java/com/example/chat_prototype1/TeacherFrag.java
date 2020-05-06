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

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class TeacherFrag extends Fragment {

    ApplicationClass app;
    EditText aquizNo;
    TextView queText, ansView, textView, textView2;
    Button sendQue, endQuiz, nextButton;
    BluetoothSocket socket;
    SendReceive sendReceive;
    String rollNo, quizNo;
    private Firebase mRootRef;
    private Firebase queRef;
    private Firebase mTeacherRef;
    private Firebase teachRef;
    int i, j;

    public TeacherFrag() {
        // Required empty public constructor
    }


    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        socket = ((ApplicationClass)getActivity().getApplication()).mySocket();
        i = 1;
        j=1;
        sendReceive = new SendReceive(socket);
        sendReceive.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher, container, false);
        queText = (TextView) view.findViewById(R.id.queText);
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

        mRootRef = new Firebase("https://chat-prototype1-139d0.firebaseio.com/Quiz");

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String string = String.valueOf(aquizNo.getText());
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

                String a = ((ApplicationClass)getActivity().getApplication()).getRollNo();
                teachRef = new Firebase("https://chat-prototype1-139d0.firebaseio.com/Quiz/" + "Quiz" + quizNo + "/" + rollNo);
                mTeacherRef = new Firebase("https://chat-prototype1-139d0.firebaseio.com/Quiz/" + "Quiz" + quizNo);
                queRef = mTeacherRef.child("Questions");

                final String[] que = new String[1];

                queRef.child("Question" + String.valueOf(i)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        que[0] = value;
                        sendReceive.write(que[0].getBytes());
                        queText.setText(que[0]);
                        //Toast.makeText(getContext(), value ,Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });
                i++;
            }
        });


        sendQue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] que = new String[1];

                queRef.child("Question" + String.valueOf(i)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        que[0] = value;
                        sendReceive.write(que[0].getBytes());
                        queText.setText(que[0]);
                    }
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
                textView.setText("Question " + String.valueOf(i));
                i++;
            }
        });

        endQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = "end quiz";
                sendReceive.write(string.getBytes());
                try {
                    socket.close();
                    Firebase marksRef = mTeacherRef.child("no");
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
                        final String finalS1 = s;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), finalS1,Toast.LENGTH_LONG).show();
                            }
                        });
                        ((ApplicationClass)getActivity().getApplication()).setRollNo(rollNo);
                    }
                    else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ansView.setText(finalS);
                            Firebase AnsRef = teachRef.child("Answers");
                            Firebase b = AnsRef.child("Answer" + String.valueOf(j));
                            j++;
                            b.setValue(finalS);

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
