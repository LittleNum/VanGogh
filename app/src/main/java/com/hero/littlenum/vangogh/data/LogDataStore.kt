package com.hero.littlenum.vangogh.data

class LogDataStore : ILogData {
    val allLogs = mutableListOf<Log>()
    val filterLogs = mutableListOf<Log>()

    val levelInterceptor = LevelInterceptor()
    val tagInterceptor = TagInterceptor()
    val keyWordInterceptor = KeyWordInterceptor()
    val prefixInterceptor = PrefixInterceptor()

    val interceptors = listOf(levelInterceptor,
            tagInterceptor,
            keyWordInterceptor,
            prefixInterceptor)

    override fun clearLog() {
        allLogs.clear()
        filterLogs.clear()
    }

    override fun addNewLog(log: String?): Log {
        return Log(log ?: "")
    }

    override fun getAllLog(): List<Log> = filterLogs

    override fun saveLogToLocal() {
    }

    override fun uploadLogs() {
    }

    override fun filterLogByLevel(level: Level) {
        levelInterceptor.level = level
        intercept()
    }

    override fun filterLogByTag(tag: String) {
        tagInterceptor.currentTag = tag
        intercept()
    }

    override fun filterLogByKeyWord(key: String) {
        keyWordInterceptor.keyWord = key
        intercept()
    }

    override fun getLevels(): List<Level> = Level.values().asList()

    override fun getCurrentLevtl(): Level = levelInterceptor.level

    override fun getTags(): List<String> = tagInterceptor.historyTags

    override fun getCurrentTag(): String? = tagInterceptor.currentTag

    override fun getKeyWord(): String? = keyWordInterceptor.keyWord

    override fun isShowPrefix(): Boolean = prefixInterceptor.showPrefix

    private fun intercept() {
        filterLogs.clear()
        allLogs.forEach loop@{ log ->
            interceptors.forEach {
                if (!it.intercept(log)) {
                    return@loop
                }
            }
            filterLogs.add(log)
        }
    }
}