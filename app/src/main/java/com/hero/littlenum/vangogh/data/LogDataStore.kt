package com.hero.littlenum.vangogh.data

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.hero.littlenum.vangogh.R
import com.hero.littlenum.vangogh.data.interceptors.*
import okhttp3.*
import java.io.*
import java.util.concurrent.Executors

const val MAX_BYTES: Int = 50 * 1024

class LogDataStore(var name: String = "", var logPrefix: String? = "", var url: String = "") : ILogData {
    val historyCachePath: String = "history_log.txt"
    val logCachePath: String = "log_cache.txt"
    var originLogs = StringBuilder()
    val allLogs = mutableListOf<Log>()
    val filterLogs = mutableListOf<Log>()

    val levelInterceptor = LevelInterceptor()
    val tagInterceptor = TagInterceptor()
    val keyWordInterceptor = KeyWordInterceptor()
    val prefixInterceptor = PrefixInterceptor()
    val resumeInterceptor = ResumeInterceptor()

    var maxBytes = MAX_BYTES
    private lateinit var context: Context
    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newCachedThreadPool()
    private var listener: ((Boolean) -> Unit)? = null

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
        var bw: BufferedWriter? = null
        try {
            val path = context.externalCacheDir.absolutePath + "/" + logCachePath
            val os = OutputStreamWriter(FileOutputStream(path, false), "UTF-8")
            bw = BufferedWriter(os)
            var text = originLogs.toString()
            if (text.length > maxBytes && maxBytes > 0) {
                text = text.substring(text.length - maxBytes)
                text = (logPrefix ?: "") + text
            }
            bw.write(text)
            bw.flush()
            bw.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            postResult(true)
        } finally {
            bw?.close()
        }
    }

    override fun uploadLogs(listener: (Boolean) -> Unit) {
        this.listener = listener
        postResult(false)
        executor.execute {
            saveLogToLocal()
            upload(name, logCachePath, false)
        }
    }

    private fun postResult(result: Boolean) {
        this.listener?.let {
            handler.post {
                it(result)
            }
        }
    }

    private fun upload(name: String, fileName: String, delete: Boolean) {
        val file = File(context.externalCacheDir.absolutePath + "/" + fileName)
        if (!file.exists()) {
            return
        }
        val client = OkHttpClient()
        val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", name)
                .addFormDataPart("file", fileName,
                        RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .build()

        try {
            val request = Request.Builder().post(requestBody).url(url).build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    postResult(true)
                    handler.post { Toast.makeText(context, R.string.upload_fail, Toast.LENGTH_SHORT).show() }
                    if (delete) {
                        file.delete()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    postResult(true)
                    print(response)
                    handler.post { Toast.makeText(context, R.string.upload_success, Toast.LENGTH_SHORT).show() }
                    if (delete) {
                        file.delete()
                    }
                }
            })
        } catch (e: Exception) {
            postResult(true)
            e.printStackTrace()
            if (delete) {
                file.delete()
            }
        }
    }

    override fun postHistoryLogIfExist() {
        try {
            val path = context.externalCacheDir.absolutePath + "/" + historyCachePath
            val file = File(path)
            if (file.exists()) {
                upload("historylog-" + Integer.toHexString(hashCode()), historyCachePath, true)
                android.util.Log.e("sendlog", "sendlog " + android.util.Log.getStackTraceString(Throwable("loglog")))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun handleUncaughtException() {
        var bw: BufferedWriter? = null
        try {
            val path = context.externalCacheDir.absolutePath + "/" + historyCachePath
            val os = OutputStreamWriter(FileOutputStream(path, true), "UTF-8")
            bw = BufferedWriter(os)
            var text = originLogs.toString()
            if (text.isEmpty()) {
                return
            }
            android.util.Log.e("sendlog", "handleUncaughtException " + android.util.Log.getStackTraceString(Throwable("loglog")))
            if (text.length > maxBytes && maxBytes > 0) {
                text = text.substring(text.length - maxBytes)
                text = (logPrefix ?: "") + text
            }
            bw.write(text)
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