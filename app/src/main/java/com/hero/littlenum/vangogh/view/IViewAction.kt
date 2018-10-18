package com.hero.littlenum.vangogh.view

import com.hero.littlenum.vangogh.data.Level


interface IViewAction {
    fun selectNewLevel(level: Level)

    fun selectNewTag(tag: String)

    fun selectNewKeyWord(key: String)

    fun clearLog()

    fun getLevels(): List<Level>

    fun getCurrentLevel(): Level

    fun getHistoryTags(): List<String>

    fun getCurrentTag(): String?

    fun showPrefix(): Boolean

    fun getKeyWord(): String?
}