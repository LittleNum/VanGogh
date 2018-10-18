package com.hero.littlenum.vangogh.present

import com.hero.littlenum.vangogh.data.Level
import com.hero.littlenum.vangogh.data.Log

interface ILogContract {
    interface ILogPresent {
        fun startShowLog()

        fun clearLog()

        fun saveLogToLocal()

        fun uploadLogs()

        fun handleUncaughtException()
    }

    interface ILogWindowView {
        fun clearLog()

        fun showAllLogs(logs: List<Log>)

        fun updateLogList(log: Log)

        fun scrollToTop()

        fun scrollToBottom()
    }
}