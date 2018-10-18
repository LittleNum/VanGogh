package com.hero.littlenum.vangogh.present

import com.hero.littlenum.vangogh.data.ILogData
import com.hero.littlenum.vangogh.data.Level
import com.hero.littlenum.vangogh.data.LogDataStore
import com.hero.littlenum.vangogh.task.LogcatTask
import com.hero.littlenum.vangogh.task.OnLogListener
import com.hero.littlenum.vangogh.view.IViewAction

class VanGoghPresent(val view: ILogContract.ILogWindowView, val data: ILogData = LogDataStore()) :
        ILogContract.ILogPresent, OnLogListener, IViewAction {
    val task = LogcatTask()

    override fun startShowLog() {
        task.execute(this)
    }

    override fun clearLog() {
        data.clearLog()
        view.clearLog()
    }

    override fun saveLogToLocal() {
        data.saveLogToLocal()
    }

    override fun uploadLogs() {
        data.uploadLogs()
    }

    override fun handleUncaughtException() {
        data.uploadLogs()
    }

    override fun onPreExecute() {
        view.clearLog()
    }

    override fun onLogUpdate(log: String?) {
        view.updateLogList(data.addNewLog(log))
    }

    override fun onPostExecute() {

    }

    override fun selectNewLevel(level: Level) {
        data.filterLogByLevel(level)
        view.showAllLogs(data.getAllLog())
    }

    override fun selectNewTag(tag: String) {
        data.filterLogByTag(tag)
        view.showAllLogs(data.getAllLog())
    }

    override fun selectNewKeyWord(key: String) {
        data.filterLogByKeyWord(key)
        view.showAllLogs(data.getAllLog())
    }

    override fun getLevels(): List<Level> = data.getLevels()

    override fun getCurrentLevel(): Level = data.getCurrentLevtl()

    override fun getHistoryTags(): List<String> = data.getTags()

    override fun getCurrentTag(): String? = data.getCurrentTag()

    override fun showPrefix(): Boolean = data.isShowPrefix()

    override fun getKeyWord(): String? = data.getKeyWord()
}