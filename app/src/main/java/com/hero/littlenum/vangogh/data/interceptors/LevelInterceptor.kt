package com.hero.littlenum.vangogh.data.interceptors

import com.hero.littlenum.vangogh.data.Level
import com.hero.littlenum.vangogh.data.Log

class LevelInterceptor : LogInterceptor {
    var level: Level = Level.V

    override fun intercept(log: Log): Boolean {
        return when (level) {
            Level.SE -> log.level == Level.SE
            Level.C -> log.level == Level.C
            else -> log.level.level() >= level.level()
        }
    }
}