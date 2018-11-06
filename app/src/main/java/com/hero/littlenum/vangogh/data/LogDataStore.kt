package com.hero.littlenum.vangogh.data

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.hero.littlenum.vangogh.R
import com.hero.littlenum.vangogh.data.interceptors.*
import okhttp3.*
import java.io.*

class LogDataStore(var name: String = "", var logPrefix: String? = "", var url: String = "") : ILogData {
    val historyCachePath: String = "history_log.txt"
    var originLogs = StringBuilder()
    val allLogs = mutableListOf<Log>()
    val filterLogs = mutableListOf<Log>()

    val levelInterceptor = LevelInterceptor()
    val tagInterceptor = TagInterceptor()
    val keyWordInterceptor = KeyWordInterceptor()
    val prefixInterceptor = PrefixInterceptor()
    val resumeInterceptor = ResumeInterceptor()
    private lateinit var context: Context
    private val handler = Handler(Looper.getMainLooper())

    val interceptors = listOf(
            levelInterceptor,
            tagInterceptor,
            keyWordInterceptor,
            prefixInterceptor,
            resumeInterceptor)

    override fun clearLog() {
        originLogs = StringBuilder(logPrefix)
        allLogs.clear()
        filterLogs.clear()
    }

    override fun addNewLog(log: String): Log? {
        val t = Log(log)
        allLogs.add(t)
        originLogs.append(log).append("\r")
        return intercept(t)
    }

    override fun setSuffix(suffix: String?) {
        name = suffix ?: ""
    }

    override fun setLogInfo(info: String?) {
        logPrefix = info ?: ""
        originLogs.insert(0, logPrefix)
    }

    override fun getAllLog(): List<Log> = filterLogs

    override fun saveLogToLocal() {
    }

    override fun uploadLogs() {
        postLogs(name, originLogs.toString())
    }

    private fun postLogs(name: String, log: String) {
        val client = OkHttpClient()
        val post = FormBody.Builder().add("name", name)
                .add("log", log)
                .build()
        try {
            val request = Request.Builder().post(post).url(url).build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    handler.post { Toast.makeText(context, R.string.upload_fail, Toast.LENGTH_SHORT).show() }
                }

                override fun onResponse(call: Call, response: Response) {
                    print(response)
                    handler.post { Toast.makeText(context, R.string.upload_success, Toast.LENGTH_SHORT).show() }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun postHistoryLogIfExist() {
        var br: BufferedReader? = null
        try {
            val path = context.externalCacheDir.absolutePath + historyCachePath
            val file = File(path)
            if (file.exists()) {
                val os = InputStreamReader(FileInputStream(path), "UTF-8")
                br = BufferedReader(os)
                val log = br.readText()
                br.close()
                postLogs("historylog-" + Integer.toHexString(hashCode()), log)
                file.delete()
                android.util.Log.e("sendlog","sendlog " +android.util.Log.getStackTraceString(Throwable("loglog")))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                br?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun handleUncaughtException() {
        var bw: BufferedWriter? = null
        try {
            val path = context.externalCacheDir.absolutePath + historyCachePath
            val os = OutputStreamWriter(FileOutputStream(path, true), "UTF-8")
            bw = BufferedWriter(os)
            bw.write(originLogs.toString())
            bw.flush()
            bw.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                bw?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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

    override fun setContext(ctx: Context) {
        this.context = ctx
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