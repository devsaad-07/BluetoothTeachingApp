package com.example.chat_prototype1;

import android.content.Intent;
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

public class ShowQuiz extends AppCompatActivity {


    TextView status;

    EditText id, password, rollNo;
    Button nextButton;
    private Firebase mRootRef;
    Boolean studentid, studentpassword, studentRollNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_quiz);


        studentid = false;
        studentpassword = false;
        studentRollNo = false;
        //Log.i(TAG, "oncreateFRag");
        mRootRef = new Firebase("https://chat-prototype1-139d0.firebaseio.com/Classroom/Users/Students");
        //Firebase maRef = mRootRef.child("yo");
        //maRef.setValue("2");

        status = (TextView) findViewById(R.id.status);
        rollNo = (EditText) findViewById(R.id.rollNo);
        id = (EditText) findViewById(R.id.id);
        password = (EditText) findViewById(R.id.password);
        nextButton = (Button) findViewById(R.id.nextButton);


        final String[] tID = new String[1];
        final String[] tPass = new String[1];
        final String[] mRollNo = new String[1];
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (studentRollNo == false || studentid == false || studentpassword == false) {
                    final String mID = id.getText().toString();
                    final String mPass = password.getText().toString();
                    mRollNo[0] = rollNo.getText().toString();

                    mRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.hasChild(mRollNo[0])) {
                                // run some code
                                Toast.makeText(getApplicationContext(),"yo",Toast.LENGTH_LONG).show();
                                studentRollNo = true;
                                final Firebase mRollRef = mRootRef.child(mRollNo[0]);
                                mRollRef.child("id").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String value = dataSnapshot.getValue(String.class);
                                        tID[0] = value;
                                        studentid = true;
                                        if (tID[0].equals(mID)){
                                            mRollRef.child("password").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    String value = dataSnapshot.getValue(String.class);
                                                    tPass[0] = value;
                                                    //Toast.makeText(getApplicationContext(),tPass[0],Toast.LENGTH_LONG).show();
                                                    if (tPass[0].equals(mPass)){
                                                        studentpassword = true;
                                                        rollNo.setVisibility(View.GONE);
                                                        password.setVisibility(View.GONE);
                                                        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();

                                                        id.getText().clear();
                                                        id.setHint("Enter Quiz no.");
                                                    }
                                                    else {
                                                        status.setText("Incorrect Password");
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(FirebaseError firebaseError) {
                                                }
                                            });
                                        }
                                        else{
                                            status.setText("Incorrect ID");
                                        }
                                        //Toast.makeText(getApplicationContext(), tID[0], Toast.LENGTH_LONG).show();
                                    }
                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {
                                        //Log.i(TAG, "cancelled");
                                    }
                                });
                            }
                            else {
                                status.setText("Incorrect Roll no");
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                } else {
                    String quizNo = id.getText().toString();
                    Intent intent = new Intent(getApplicationContext(), ShowMarks.class);
                    intent.putExtra("rollNo", mRollNo[0]);
                    intent.putExtra("quizNo", quizNo);
                    startActivity(intent);
                }
            }
        });
    }
}
