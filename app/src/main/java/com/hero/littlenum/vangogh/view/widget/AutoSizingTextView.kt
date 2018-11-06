package com.hero.littlenum.vangogh.view.widget

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.TextView

class AutoSizingTextView : TextView {
    private var textWidth = 0
    private var tsOrigin = textSize.toInt()
    private val tPaint = Paint()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val w = measuredWidth
        if (w != textWidth) {
            textWidth = w
            adjustTextSize()
        }
    }


    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        adjustTextSize()
    }

    private fun adjustTextSize() {
        if (textWidth <= 0) {
            return
        }
        text?.let {
            tPaint.textSize = tsOrigin.toFloat()
            val text = it.toString()
            val len = tPaint.measureText(text)
            if (len > textWidth * 2) {
                for (size in tsOrigin downTo 1 step 1) {
                    tPaint.textSize = size.toFloat()
                    if (tPaint.measureText(text) < textWidth * 2) {
                        setTextSize(TypedValue.COMPLEX_UNIT_PX, size.toFloat())
                        break
                    }
                }
            }
        }
    }
}