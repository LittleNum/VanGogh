package com.hero.littlenum.vangogh.data.interceptors

import com.hero.littlenum.vangogh.data.Log

interface LogInterceptor {
    fun intercept(log: Log): Boolean = true
}