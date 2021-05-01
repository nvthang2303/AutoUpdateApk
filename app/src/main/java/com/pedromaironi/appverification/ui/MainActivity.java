package com.pedromaironi.appverification.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.PreferenceManager;
import androidx.work.Data;

import com.pedromaironi.appverification.AutoUpdateApk;
import com.pedromaironi.appverification.R;
import com.pedromaironi.appverification.services.NotificationHelper;

public class MainActivity extends AppCompatActivity {

    AutoUpdateApk AUA;
    String URL;
    public static final String KEY_TASK_DESC = "key_task_desc";
    NotificationHelper nc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        URL = "http://updateapk.pedromaironi.com/checkversion.json";
//        AUA = new AutoUpdateApk(this, URL);

        nc = new NotificationHelper(this);
        nc.createNotification("hola", "gds");
        Data data = new Data.Builder()
                .putString(KEY_TASK_DESC, "Hey I am sending the work data")
                .build();


    }
    @Override
    public void onStart(){
        super.onStart();

        Context context = getApplicationContext();
        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = data.edit();
        edit.putInt("versionCode", 60);
        edit.apply();
    }

    @Override
    public void onResume(){
        super.onResume();
        Context context = getApplicationContext();
        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(context);
        Log.d("data", String.valueOf(data.getInt("versionCode", 2)));
    }

}