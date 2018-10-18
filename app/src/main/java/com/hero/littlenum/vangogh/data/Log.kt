package com.hero.littlenum.vangogh.data

const val PKG = "com.hexin.plat.android"

data class Log(val log: String) {
    var level: Level = Level.V
    var signal: String = level.signal()
    var prefix: String
    var content: String

    init {
        level = when {
            log.contains(Level.V.signal()) -> Level.V
            log.contains(Level.D.signal()) -> Level.D
            log.contains(Level.I.signal()) -> Level.I
            log.contains(Level.W.signal()) -> Level.W
            log.contains(Level.E.signal()) -> Level.E
            log.contains(Level.SE.signal()) -> Level.SE
            log.contains(Level.C.signal()) -> Level.C
            else -> Level.V
        }
        signal = level.signal()
        var index = log.indexOf(signal)
        index = if (index >= 0) index else log.indexOf(PKG)
        val text = log.substring(Math.max(0, index))
        val mid = text.indexOf(": ")
        prefix = if (mid >= 0) log.substring(0, mid + 2) else ""
        content = if (mid >= 0) log.substring(mid + 2) else log
    }

    operator fun component2() = level
    operator fun component3() = prefix
    operator fun component4() = content
}