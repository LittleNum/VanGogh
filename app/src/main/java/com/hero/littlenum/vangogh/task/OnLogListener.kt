package com.hero.littlenum.vangogh.task

interface OnLogListener {
    fun onPreExecute()

    fun onLogUpdate(log: String)

    fun onPostExecute()
}