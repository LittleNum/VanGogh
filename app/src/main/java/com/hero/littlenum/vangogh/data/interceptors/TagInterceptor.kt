package com.hero.littlenum.vangogh.data.interceptors

import com.hero.littlenum.vangogh.data.Log

class TagInterceptor : LogInterceptor {
    var currentTag: String? = null
        set(value) {
            field = value
            field?.takeUnless { it.isEmpty() }?.let {
                if (!historyTags.contains(it)) {
                    historyTags.add(it)
                }
            }
        }
    val historyTags = mutableListOf<String>()

    override fun intercept(log: Log) = log.log.contains(currentTag ?: "")

    fun addNewTag(tag: String) {
        if (!historyTags.contains(tag)) {
            historyTags.add(tag)
        }
    }

    fun selectNewTag(tag: String) {
        if (!historyTags.contains(tag)) {
            historyTags.add(tag)
        }
        currentTag = tag
    }
}