package com.hero.littlenum.vangogh.data

import android.content.Context

interface ILogData {
    fun clearLog()

    fun addNewLog(log: String): Log?

    fun setSuffix(suffix: String?)

    fun setLogInfo(info: String?)

    fun getAllLog(): List<Log>

    fun saveLogToLocal()

    fun uploadLogs(listener: (Boolean) -> Unit)

    fun postHistoryLogIfExist()

    fun handleUncaughtException()

    fun filterLogByLevel(level: Level)

    fun filterLogByTag(tag: String)

    fun filterLogByKeyWord(key: String)

    fun getLevels(): List<Level>

    fun getCurrentLevtl(): Level

    fun getTags(): List<String>

    fun getCurrentTag(): String?

    fun getKeyWords(): List<String>

    fun getKeyWord(): String?

    fun isShowPrefix(): Boolean

    fun isResume(): Boolean

    fun togglePrefix()

    fun toggleResume()

    fun setContext(ctx: Context)
}