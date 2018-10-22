package com.hero.littlenum.vangogh.data

import android.graphics.Color

const val VERBOSE: String = "Verbose"
const val DEBUG: String = "Debug"
const val INFO: String = "Info"
const val WARN: String = "Warn"
const val ERROR: String = "Error"
const val SYSERR: String = "System.err"
const val CONSOLE: String = "Console"

interface Signal {
    fun signal(): String

    fun level(): Int

    fun color(): Int = Color.WHITE
}

enum class Level(val text: String) : Signal {
    V(VERBOSE) {
        override fun signal(): String {
            return " V/"
        }

        override fun level(): Int = 0
    },
    D(DEBUG) {
        override fun signal(): String {
            return " D/"
        }

        override fun level(): Int = 1
    },
    I(INFO) {
        override fun signal(): String {
            return " I/"
        }

        override fun level(): Int = 2

        override fun color(): Int = Color.WHITE
    },
    W(WARN) {
        override fun signal(): String {
            return " W/"
        }

        override fun level(): Int = 3
    },
    E(ERROR) {
        override fun signal(): String {
            return " E/"
        }

        override fun level(): Int = 4

        override fun color(): Int = Color.RED
    },
    SE(SYSERR) {
        override fun signal(): String {
            return SYSERR
        }

        override fun level(): Int = 0
    },
    C(CONSOLE) {
        override fun signal(): String {
            return CONSOLE
        }

        override fun level(): Int = 0
    };

    override fun toString(): String = text

    companion object {
        fun getLevel(name: String): Level {
            return when (name) {
                VERBOSE -> V
                DEBUG -> D
                INFO -> I
                WARN -> W
                ERROR -> E
                SYSERR -> SE
                CONSOLE -> C
                else -> V
            }
        }
    }
}