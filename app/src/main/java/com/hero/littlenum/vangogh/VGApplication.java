package com.hero.littlenum.vangogh;

import android.app.Application;

public class VGApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ExceptionHandler handler = new ExceptionHandler(getApplicationContext());
        handler.init();
    }
}
