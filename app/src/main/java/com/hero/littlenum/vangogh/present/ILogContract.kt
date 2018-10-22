package com.hero.littlenum.vangogh.present

import com.hero.littlenum.vangogh.data.Log

interface ILogContract {
    interface ILogPresent {
        fun startShowLog()

        fun clearLog()

        fun saveLogToLocal()

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
    }
}