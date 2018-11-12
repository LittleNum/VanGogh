package com.hero.littlenum.vangogh;

import android.content.Context;
import android.util.Log;

import com.hero.littlenum.vangogh.task.VanGoghService;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    Context mctx;
    Thread.UncaughtExceptionHandler mDefault;

    public ExceptionHandler(Context context) {
        super();
        this.mctx = context;
        mDefault = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void init() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        Log.e("sss", "uncaught");
        VanGoghService.Companion.handleUnCaughtException(mctx);
        mDefault.uncaughtException(t, e);
    }
}
