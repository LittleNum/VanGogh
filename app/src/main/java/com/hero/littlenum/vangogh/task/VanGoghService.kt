package com.hero.littlenum.vangogh.task

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.*
import android.widget.Toast
import com.hero.littlenum.vangogh.R
import com.hero.littlenum.vangogh.data.LogDataStore
import com.hero.littlenum.vangogh.present.ILogContract
import com.hero.littlenum.vangogh.present.VanGoghPresent
import com.hero.littlenum.vangogh.view.ControlBar
import com.hero.littlenum.vangogh.view.IViewAction
import com.hero.littlenum.vangogh.view.LogWindow
import java.lang.IllegalStateException

const val RATIO = 3f / 4
const val ORIGIN_X = 0
const val ORIGIN_Y = 0
const val CHECK_SLEEP = 5000L

class VanGoghService : Service() {
    private lateinit var wView: LogWindow
    private lateinit var mWindowManager: WindowManager
    private lateinit var mWMLayoutParams: WindowManager.LayoutParams
    private lateinit var displayMetrics: DisplayMetrics

    private lateinit var vgPresent: ILogContract.ILogPresent
    private lateinit var dataStore: LogDataStore

    private var added = false

    private val originPosition = Origin()

    private var checkMetrics = true

    override fun onCreate() {
        super.onCreate()
        serviceExisted = true
        initWindow()
        initParams()
        vgPresent.startShowLog()
        Thread {
            while (checkMetrics) {
                try {
                    Thread.sleep(CHECK_SLEEP)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                mWindowManager.defaultDisplay.getMetrics(displayMetrics)
            }
        }.start()
        ue = {
            vgPresent.handleUncaughtException()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.getIntExtra(INTENT_KEY_ACTION, Action.Invalid.action)
                ?: Action.Invalid.action
        when (action) {
            Action.Start.action -> {
                vgPresent.postHistoryLogIfExist()
                addWindow(config?.orientation, config?.mode)
            }
            Action.Stop.action -> {
                vgPresent.stopTask()
                removeWindow()
            }
            Action.UncaughtException.action -> {
                vgPresent.handleUncaughtException()
                removeWindow()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        removeWindow()
        config?.request = null
        checkMetrics = false
        serviceExisted = false
    }

    private fun initWindow() {
        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        displayMetrics = DisplayMetrics()
        mWindowManager.defaultDisplay.getMetrics(displayMetrics)
        mWMLayoutParams = WindowManager.LayoutParams()
        mWMLayoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        mWMLayoutParams.type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) TYPE_APPLICATION_OVERLAY else TYPE_SYSTEM_ALERT
        mWMLayoutParams.format = PixelFormat.TRANSLUCENT
        mWMLayoutParams.gravity = Gravity.START or Gravity.TOP
        mWMLayoutParams.x = ORIGIN_X
        mWMLayoutParams.y = ORIGIN_Y
        mWMLayoutParams.flags = FLAG_NOT_TOUCH_MODAL or FLAG_NOT_FOCUSABLE
        val portrait = displayMetrics.widthPixels < displayMetrics.heightPixels
        mWMLayoutParams.width = if (portrait) displayMetrics.widthPixels else displayMetrics.widthPixels / 2
        mWMLayoutParams.height = ((if (portrait) displayMetrics.widthPixels else displayMetrics.heightPixels) * RATIO).toInt()
    }

    private fun initParams() {
        wView = LayoutInflater.from(this).inflate(R.layout.log_contain_layout, null, false) as LogWindow
        wView.setSuffix(config?.suffix)
        dataStore = LogDataStore(config?.suffix ?: "", config?.logInfo, config?.url ?: "")
        vgPresent = VanGoghPresent(wView, dataStore)
        wView.viewAction = vgPresent as IViewAction
        wView.setControlOrientation(object : ControlBar.WindowAction {
            override fun toggleOrientation() {
                val ori = mWMLayoutParams.screenOrientation
                if (ori == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    mWMLayoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                } else {
                    mWMLayoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
                mWindowManager.updateViewLayout(wView, mWMLayoutParams)
            }

            override fun closeWindow() {
                removeWindow()
            }
        })
        wView.adjustView = object : LogWindow.AdjustView {

            override fun onZoomStart() {
            }

            override fun onZoomUpdate(dx: Float, dy: Float) {
                val w = displayMetrics.widthPixels
                val h = displayMetrics.heightPixels
                val size = Math.min(w, h)
                val cw = if (mWMLayoutParams.width <= 0) w else mWMLayoutParams.width
                val ch = if (mWMLayoutParams.height <= 0) h else mWMLayoutParams.height
                mWMLayoutParams.width = Math.min(Math.max(cw + dx.toInt(), size / 2), w)
                mWMLayoutParams.height = Math.min(Math.max(ch + dy, size / 2 * RATIO).toInt(), h)
                mWindowManager.updateViewLayout(wView, mWMLayoutParams)
            }

            override fun onZoomEnd() {
            }

            override fun onMoveStart() {
                originPosition.p = mWMLayoutParams.x
                originPosition.q = mWMLayoutParams.y
            }

            override fun onMoveUpdate(dx: Float, dy: Float) {
                mWMLayoutParams.x = Math.min(Math.max(0, originPosition.p + dx.toInt()), displayMetrics.widthPixels - mWMLayoutParams.width)
                mWMLayoutParams.y = Math.min(Math.max(0, originPosition.q + dy.toInt()), displayMetrics.heightPixels - mWMLayoutParams.height)
                mWindowManager.updateViewLayout(wView, mWMLayoutParams)
            }

            override fun onMoveEnd() {
            }
        }
    }

    private val addView = { orientation: Config.Orientation ->
        mWMLayoutParams.screenOrientation = orientation.value
        mWindowManager.addView(wView, mWMLayoutParams)
        added = true
    }

    private fun addWindow(orientation: Config.Orientation?, mode: Config.Mode?) {
        if (added) {
            return
        }
        wView.setMode(mode ?: Config.Mode.Default)
        wView.setSuffix(config?.suffix)
        dataStore.name = config?.suffix ?: Build.BRAND + "-" + Integer.toHexString(hashCode())
        dataStore.logPrefix = config?.logInfo ?: ""
        dataStore.url = config?.url ?: ""
        if (mode == Config.Mode.Default) {
            val portrait = displayMetrics.widthPixels < displayMetrics.heightPixels
            mWMLayoutParams.width = if (portrait) displayMetrics.widthPixels else displayMetrics.widthPixels / 2
            mWMLayoutParams.height = ((if (portrait) displayMetrics.widthPixels else displayMetrics.heightPixels) * RATIO).toInt()
        } else {
            mWMLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
            mWMLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(applicationContext)) {
            addView(orientation ?: Config.Orientation.Portrait)
        } else {
            requestSpecialPermission(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, object : PermissionRequest.Callback {
                override fun onResult() {
                    if (Settings.canDrawOverlays(applicationContext)) {
                        addView(orientation ?: Config.Orientation.Portrait)
                    } else {
                        Toast.makeText(applicationContext, R.string.permission_request, Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }

    private fun removeWindow() {
        try {
            vgPresent.close()
            if (added) {
                mWindowManager.removeView(wView)
            }
            added = false
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
        const val INTENT_KEY_ORIENTATION = "value"
        const val INTENT_KEY_MODE = "mode"
        private var serviceExisted = false
        private var config: Config? = null
        private var ue = {}

        fun startVanGogh(activity: Activity) {
            if (config == null) {
                throw IllegalStateException("config is null,must set config before start service")
            }
            config?.apply {
                val it = Intent(activity, VanGoghService::class.java)
                it.putExtra(INTENT_KEY_ACTION, Action.Start.action)
                it.putExtra(INTENT_KEY_ORIENTATION, this.orientation.value)
                it.putExtra(INTENT_KEY_MODE, this.mode)
                activity.startService(it)
            }
        }

        fun stopVanGogh(activity: Activity) {
            if (serviceExisted) {
                val it = Intent(activity, VanGoghService::class.java)
                it.putExtra(INTENT_KEY_ACTION, Action.Stop.action)
                activity.startService(it)
            }
        }

        fun handleUnCaughtException(activity: Context) {
            if (serviceExisted) {
                ue()
            }
        }

        fun registerPermissionRequestContext(request: PermissionRequest?) {
            config?.let {
                it.request = request
            }
        }

        private fun requestPermission(permission: Array<String>, callback: PermissionRequest.Callback) {
            config?.request?.request(permission, callback)
        }

        private fun requestPermission(permission: String, callback: PermissionRequest.Callback) {
            config?.request?.request(permission, callback)
        }

        private fun requestSpecialPermission(permission: String, callback: PermissionRequest.Callback) {
            config?.request?.requestSpecial(permission, callback)
        }

        /**
         * use Config().apply{
         *  suffix = ""
         *  loginfo = ""
         *  ...
         * }
         */
        fun init(config: Config) {
            this.config = config
        }
    }

    data class Origin(var p: Int = 0, var q: Int = 0)
}