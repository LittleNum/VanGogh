package com.hero.littlenum.vangogh.data

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