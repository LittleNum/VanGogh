package com.hero.littlenum.vangogh.task

interface PermissionRequest {
    fun request(permission: Array<String>, callback: Callback)
    fun request(permission: String, callback: Callback)
    fun requestSpecial(permission: String, callback: Callback)

    interface Callback {
        fun onResult()
    }
}