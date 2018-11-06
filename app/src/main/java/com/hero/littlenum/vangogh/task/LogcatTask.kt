package com.hero.littlenum.vangogh.task

import android.os.AsyncTask
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

const val EXEC_LOG: String = "logcat -v"
const val EXEC_LOG_TIME: String = "logcat -v time"


class LogcatTask : AsyncTask<OnLogListener, String, Unit>() {
    var listener: OnLogListener? = null
    var process: Process? = null

    override fun onPreExecute() {
        listener?.onPreExecute()
    }

    override fun doInBackground(vararg p0: OnLogListener?) {
        listener = p0[0]
        try {
            Runtime.getRuntime().exec(EXEC_LOG)
            process = Runtime.getRuntime().exec(EXEC_LOG_TIME)
            val stream = process?.inputStream
            stream?.apply {
                val reader = InputStreamReader(this)
                val bufferedReader = BufferedReader(reader)

                bufferedReader.forEachLine {
                    publishProgress(it)
                }

                bufferedReader.close()
                reader.close()
                this.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onProgressUpdate(vararg values: String?) {
        values.first()?.let {
            listener?.onLogUpdate(it)
        }
    }


    override fun onPostExecute(result: Unit?) {
        listener?.onPostExecute()
    }

    fun close() {
        process?.destroy()
        process = null
        cancel(true)
    }
}