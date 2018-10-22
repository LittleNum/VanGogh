package com.hero.littlenum.vangogh;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.hero.littlenum.vangogh.task.VanGoghService;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private boolean logResume = true;
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_main);
        VanGoghService.Companion.startVanGogh(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (logResume) {
                        Thread.sleep(500);
                        Log.e("vangogh brillant", "--------------- log :" + new Random().nextInt());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "text", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logResume = false;
        VanGoghService.Companion.stopVanGogh(this);
    }
}
