package com.hero.littlenum.vangogh.data.interceptors

class KeyWordInterceptor : LogInterceptor {
    var keyWord: String? = null
        set(value) {
            field = value
            field?.takeUnless { it.isEmpty() }?.let {
                if (!historyKws.contains(it)) {
                    historyKws.add(it)
                }
            }
        }

    val historyKws = mutableListOf<String>()
}