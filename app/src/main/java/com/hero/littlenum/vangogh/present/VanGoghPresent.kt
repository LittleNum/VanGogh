package com.hero.littlenum.vangogh.present

import android.os.AsyncTask
import com.hero.littlenum.vangogh.data.ILogData
import com.hero.littlenum.vangogh.data.Level
import com.hero.littlenum.vangogh.data.LogDataStore
import com.hero.littlenum.vangogh.task.LogcatTask
import com.hero.littlenum.vangogh.task.OnLogListener
import com.hero.littlenum.vangogh.view.IViewAction

class VanGoghPresent(val view: ILogContract.ILogWindowView, var data: ILogData = LogDataStore()) :
        ILogContract.ILogPresent, OnLogListener, IViewAction {

    init {
        data.setContext(view.getViewContext())
    }

    val task = LogcatTask()

    override fun startShowLog() {
        if (task.status == AsyncTask.Status.RUNNING) {
            return
        }
        task.execute(this)
    }

    override fun stopTask() {
        task.close()
    }

    override fun clearLog() {
        data.clearLog()
        view.clearLog()
    }

    override fun saveLogToLocal() {
        data.saveLogToLocal()
    }

    override fun postHistoryLogIfExist() {
        data.postHistoryLogIfExist()
    }

    override fun handleUncaughtException() {
        data.handleUncaughtException()
    }

    override fun close() {
        task.close()
    }

    override fun onPreExecute() {
        view.clearLog()
    }

    override fun onLogUpdate(log: String) {
        data.addNewLog(log)?.let {
            view.updateLogList(it)
        }
    }

    override fun onPostExecute() {

    }

    override fun upload(listener: (Boolean) -> Unit) {
        data.uploadLogs(listener)
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

    override fun getKeyWords(): List<String> = data.getKeyWords()

    override fun getKeyWord(): String? = data.getKeyWord()

    override fun showPrefix(): Boolean = data.isShowPrefix()

    override fun togglePrefix() {
        data.togglePrefix()
        view.notifyView()
    }

    override fun toggleResume() {
        data.toggleResume()
    }

    override fun isResume(): Boolean = data.isResume()
}