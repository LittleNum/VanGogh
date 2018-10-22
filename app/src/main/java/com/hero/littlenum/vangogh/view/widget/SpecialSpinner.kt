package com.hero.littlenum.vangogh.view.widget

import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.Spinner

class SpecialSpinner : Spinner {

    constructor(context: Context) : super(context)
    constructor(context: Context, mode: Int) : super(context, mode)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    //isVisibleToUser
    override fun getGlobalVisibleRect(r: Rect?, globalOffset: Point?) = true
}