package com.hero.littlenum.vangogh.data

class PrefixInterceptor : LogInterceptor {
    var showPrefix = true

    override fun intercept(log: Log): Boolean {
        return showPrefix
    }
}