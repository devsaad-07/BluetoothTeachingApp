package com.example.chat_prototype1;

import android.app.Application;
import android.bluetooth.BluetoothSocket;

import com.firebase.client.Firebase;

public class ApplicationClass extends Application {

    public static String TAG = "dev";
    public BluetoothSocket socket;
    public String rollNo;
    public MainActivity mainActivity;
    public int count;
    String quizNo;

    private static ApplicationClass app;

    public ApplicationClass getApp(){
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }

    public void setQuizNo(String string){
        this.quizNo = string;
    }

    public String getQuizNo(){
        return quizNo;
    }

    public void setCount(int a){
        this.count = a;
    }
    public int getCount(){
        return count;
    }

    public void setRollNo(String s1){
        this.rollNo = s1;
        //Toast.makeText(getApplicationContext(),"setS "+ s,Toast.LENGTH_LONG).show();
    }

    public String getRollNo(){
        ///Toast.makeText(getApplicationContext(),"getS "+s,Toast.LENGTH_LONG).show();
        return rollNo;
    }

    public BluetoothSocket mySocket(){
        return socket;
    }
    public void setBluetoothSocket(BluetoothSocket socket1){
        this.socket = socket1;
    }
}
