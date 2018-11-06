package com.hero.littlenum.vangogh.present

import android.content.Context
import com.hero.littlenum.vangogh.data.Log
import com.hero.littlenum.vangogh.task.Config

interface ILogContract {
    interface ILogPresent {
        fun startShowLog()

        fun stopTask()

        fun clearLog()

        fun saveLogToLocal()

        fun postHistoryLogIfExist()

        fun handleUncaughtException()

        fun close()
    }

    interface ILogWindowView {
        fun clearLog()

        fun notifyView()

        fun showAllLogs(logs: List<Log>)

        fun updateLogList(log: Log)

        fun scrollToTop()

        fun scrollToBottom()

        fun setSuffix(suffix: String?)

        fun setMode(mode: Config.Mode)

        fun getViewContext(): Context
    }
}