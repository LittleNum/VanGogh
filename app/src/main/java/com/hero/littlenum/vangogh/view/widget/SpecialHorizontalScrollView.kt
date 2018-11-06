package com.hero.littlenum.vangogh.view.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.HorizontalScrollView

class SpecialHorizontalScrollView : HorizontalScrollView {
    var intercept = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean = intercept && ev?.action == MotionEvent.ACTION_MOVE
            || super.onInterceptTouchEvent(ev)
}