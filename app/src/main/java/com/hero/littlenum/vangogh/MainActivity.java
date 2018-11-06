package com.hero.littlenum.vangogh;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.hero.littlenum.vangogh.task.Config;
import com.hero.littlenum.vangogh.task.PermissionRequest;
import com.hero.littlenum.vangogh.task.VanGoghService;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private boolean logResume = true;
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;



        setContentView(R.layout.activity_main);
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
        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "text", Toast.LENGTH_SHORT).show();
                VanGoghService.Companion.startVanGogh(MainActivity.this);
            }
        });
        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "text", Toast.LENGTH_SHORT).show();
                VanGoghService.Companion.stopVanGogh(MainActivity.this);
            }
        });
        findViewById(R.id.land).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, Land.class);
                startActivity(it);
            }
        });
        findViewById(R.id.exception).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String x = "xxx";
                int y = Integer.parseInt(x);
                System.out.print(y);
            }
        });
        register();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && cb != null) {
            cb.onResult();
        }
    }

    PermissionRequest.Callback cb;

    private void register() {
        Config config = new Config(Build.BRAND + "-" + Integer.toHexString(hashCode()), "null");
//        config.setMode(Config.Mode.Brief);
        config.setUrl("http://10.10.26.16:8000/vangogh/uploadlog/");
        VanGoghService.Companion.init(config);
        config.setRequest(new PermissionRequest() {
            @Override
            public void request(@NotNull String[] permission, @NotNull Callback callback) {

            }

            @Override
            public void request(@NotNull String permission, @NotNull Callback callback) {

            }

            @Override
            public void requestSpecial(@NotNull String permission, @NotNull Callback callback) {
                cb = callback;
                Intent it = new Intent(permission);
                startActivityForResult(it, 100);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logResume = false;
        VanGoghService.Companion.stopVanGogh(this);
        VanGoghService.Companion.registerPermissionRequestContext(null);
    }
}
