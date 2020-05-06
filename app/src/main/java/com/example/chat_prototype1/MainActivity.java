package com.example.chat_prototype1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    Button teacherButton, studentButton, showQuiz, checkQuiz, register, createQuiz;
    final String TAG = "devsTag";
    ApplicationClass app;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        app = ((ApplicationClass)getApplication()).getApp();
        teacherButton = (Button) findViewById(R.id.teacherButton);
        studentButton = (Button) findViewById(R.id.studentButton);
        showQuiz = (Button) findViewById(R.id.showQuiz);
        checkQuiz = (Button) findViewById(R.id.checkQuiz);
        register = (Button) findViewById(R.id.register);
        createQuiz = (Button) findViewById(R.id.createQuiz);

        createQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateQuiz.class);
                startActivity(intent);
            }
        });

        teacherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "teacherButton");
                Intent intent = new Intent(getApplicationContext(), Teacher.class);
                startActivity(intent);

            }
        });

        studentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Student.class);
                startActivity(intent);
            }
        });

        checkQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CheckQuiz.class);
                startActivity(intent);
            }
        });

        showQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ShowQuiz.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
            }
        });
    }
}
