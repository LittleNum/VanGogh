package com.hero.littlenum.vangogh.data.interceptors

import com.hero.littlenum.vangogh.data.Log

class ResumeInterceptor : LogInterceptor {
    var doResume = true

    override fun intercept(log: Log) = doResume
}