package com.hero.littlenum.vangogh.task

import android.content.pm.ActivityInfo

data class Config(var suffix: String = "", var logInfo: String = "") {
    var mode: Mode = Mode.Default
    var orientation: Orientation = Orientation.Portrait
    var request: PermissionRequest? = null
    var maxBytes = 50 * 1024
    var url: String = ""

    enum class Mode(val mode: Int) {
        Default(0),
        Brief(1);

        companion object {
            fun getMode(mode: Int): Mode = when (mode) {
                0 -> Default
                1 -> Brief
                else -> Default
            }
        }
    }

    enum class Orientation(val value: Int) {
        Portrait(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT),
        Landscape(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    }
}