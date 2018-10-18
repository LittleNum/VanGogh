package com.hero.littlenum.vangogh.data

interface LogInterceptor {
    fun intercept(log: Log): Boolean
}