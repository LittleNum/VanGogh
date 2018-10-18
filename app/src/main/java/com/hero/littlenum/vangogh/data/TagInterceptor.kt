package com.hero.littlenum.vangogh.data

class TagInterceptor : LogInterceptor {
    var currentTag: String? = null
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