package com.hero.littlenum.vangogh.task

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.*
import com.hero.littlenum.vangogh.R
import com.hero.littlenum.vangogh.present.ILogContract
import com.hero.littlenum.vangogh.present.VanGoghPresent
import com.hero.littlenum.vangogh.view.IViewAction
import com.hero.littlenum.vangogh.view.LogWindow
import java.lang.Exception

class VanGoghService : Service() {
    private lateinit var wView: LogWindow
    private lateinit var mWindowManager: WindowManager
    private lateinit var mWMLayoutParams: WindowManager.LayoutParams
    private lateinit var displayMetrics: DisplayMetrics

    private lateinit var vgPresent: ILogContract.ILogPresent

    private var inited = false

    override fun onCreate() {
        super.onCreate()
        initWindow()
        initParams()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.getIntExtra(INTENT_KEY_ACTION, Action.Invalid.action)
        when (action) {
            Action.Start.action -> {
                addWindow()
                vgPresent.startShowLog()
            }
            Action.Stop.action -> removeWindow()
            Action.UncaughtException.action -> vgPresent.handleUncaughtException()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        removeWindow()
    }

    private fun initWindow() {
        if (inited) return else {
            inited = true
            mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            displayMetrics = DisplayMetrics()
            mWindowManager.defaultDisplay.getMetrics(displayMetrics)
            mWMLayoutParams = WindowManager.LayoutParams()
            mWMLayoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            mWMLayoutParams.type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) TYPE_APPLICATION_OVERLAY else TYPE_SYSTEM_ALERT
            mWMLayoutParams.format = PixelFormat.TRANSLUCENT
            mWMLayoutParams.gravity = Gravity.START or Gravity.TOP
            mWMLayoutParams.x = 0
            mWMLayoutParams.y = 100
            mWMLayoutParams.flags = FLAG_NOT_TOUCH_MODAL or FLAG_NOT_FOCUSABLE
            mWMLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            mWMLayoutParams.height = 1000
        }
    }

    private fun initParams() {
        wView = LayoutInflater.from(this).inflate(R.layout.log_contain_layout, null, false) as LogWindow
        vgPresent = VanGoghPresent(wView)
        wView.viewAction = vgPresent as IViewAction
    }

    private fun addWindow() {
        mWindowManager.addView(wView, mWMLayoutParams)
    }

    private fun removeWindow() {
        try {
            mWindowManager.removeView(wView)
            vgPresent.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        stopSelf()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    companion object {
        enum class Action(val action: Int) {
            Invalid(-1),
            Start(0),
            Stop(1),
            UncaughtException(2)
        }

        const val INTENT_KEY_ACTION = "action"
        const val INTENT_KEY_ORIENTATION = "orientation"

        fun startVanGogh(activity: Activity, orientation: Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            val it = Intent(activity, VanGoghService::class.java)
            it.putExtra(INTENT_KEY_ACTION, Action.Start.action)
            it.putExtra(INTENT_KEY_ORIENTATION, orientation)
            activity.startService(it)
        }

        fun stopVanGogh(activity: Activity) {
            val it = Intent(activity, VanGoghService::class.java)
            it.putExtra(INTENT_KEY_ACTION, Action.Stop.action)
            activity.startService(it)
        }

        fun handleUnCaughtException(activity: Activity) {
            val it = Intent(activity, VanGoghService::class.java)
            it.putExtra(INTENT_KEY_ACTION, Action.UncaughtException.action)
            activity.startService(it)
        }
    }
}