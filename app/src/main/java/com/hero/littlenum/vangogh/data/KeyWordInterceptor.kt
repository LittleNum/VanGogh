package com.hero.littlenum.vangogh.data

class KeyWordInterceptor : LogInterceptor {
    var keyWord: String? = ""

    override fun intercept(log: Log) = log.log.contains(keyWord ?: "")
}