package com.hero.littlenum.vangogh.data

import com.hero.littlenum.vangogh.data.interceptors.*
import okhttp3.*
import java.io.IOException
import java.lang.StringBuilder

class LogDataStore : ILogData {
    var originLogs = StringBuilder()
    val allLogs = mutableListOf<Log>()
    val filterLogs = mutableListOf<Log>()

    val levelInterceptor = LevelInterceptor()
    val tagInterceptor = TagInterceptor()
    val keyWordInterceptor = KeyWordInterceptor()
    val prefixInterceptor = PrefixInterceptor()
    val resumeInterceptor = ResumeInterceptor()

    val interceptors = listOf(
            levelInterceptor,
            tagInterceptor,
            keyWordInterceptor,
            prefixInterceptor,
            resumeInterceptor)

    override fun clearLog() {
        originLogs = StringBuilder()
        allLogs.clear()
        filterLogs.clear()
    }

    override fun addNewLog(log: String): Log? {
        val t = Log(log)
        allLogs.add(t)
        originLogs.append(log).append("\r")
        return intercept(t)
    }

    override fun getAllLog(): List<Log> = filterLogs

    override fun saveLogToLocal() {
    }

    override fun uploadLogs(name: String) {
        val client = OkHttpClient()
        val post = FormBody.Builder().add("name", name)
                .add("log", originLogs.toString())
                .build()
        val request = Request.Builder().post(post).url("http://10.10.26.16:8000/vangogh/uploadlog/").build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                print(response)
            }
        })

    }

    override fun filterLogByLevel(level: Level) {
        levelInterceptor.level = level
        intercept()
    }

    override fun filterLogByTag(tag: String) {
        tagInterceptor.currentTag = tag
        intercept()
    }

    override fun filterLogByKeyWord(key: String) {
        keyWordInterceptor.keyWord = key
        intercept()
    }

    override fun getLevels(): List<Level> = Level.values().asList()

    override fun getCurrentLevtl(): Level = levelInterceptor.level

    override fun getTags(): List<String> = tagInterceptor.historyTags

    override fun getCurrentTag(): String? = tagInterceptor.currentTag

    override fun getKeyWords(): List<String> = keyWordInterceptor.historyKws

    override fun getKeyWord(): String? = keyWordInterceptor.keyWord

    override fun isShowPrefix(): Boolean = prefixInterceptor.showPrefix

    override fun togglePrefix() {
        prefixInterceptor.showPrefix = !prefixInterceptor.showPrefix
    }

    override fun isResume(): Boolean = resumeInterceptor.doResume

    override fun toggleResume() {
        resumeInterceptor.doResume = !resumeInterceptor.doResume
    }

    private fun intercept(log: Log): Log? {
        interceptors.forEach {
            if (!it.intercept(log)) {
                return null
            }
        }
        return log
    }

    private fun intercept() {
        filterLogs.clear()
        allLogs.forEach loop@{ log ->
            interceptors.forEach {
                if (!it.intercept(log)) {
                    return@loop
                }
            }
            filterLogs.add(log)
        }
    }
}