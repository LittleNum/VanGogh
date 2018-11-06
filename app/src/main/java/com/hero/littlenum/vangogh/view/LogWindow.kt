package com.hero.littlenum.vangogh.view

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Rect
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.hero.littlenum.vangogh.R
import com.hero.littlenum.vangogh.data.Level
import com.hero.littlenum.vangogh.data.Log
import com.hero.littlenum.vangogh.present.ILogContract
import com.hero.littlenum.vangogh.task.Config
import com.hero.littlenum.vangogh.view.widget.SpecialHorizontalScrollView

class LogWindow : ConstraintLayout, ILogContract.ILogWindowView, ControlBar.ControlListener {
    var viewAction: IViewAction? = null
        set(value) {
            field = value
            controlBar.listener = this
        }
    var adjustView: AdjustView? = null
    var viewMode = Config.Mode.Default
    private var logList = mutableListOf<Log>()
    private lateinit var controlBar: ControlBar
    private lateinit var logListRv: RecyclerView
    private lateinit var tabScrollView: SpecialHorizontalScrollView
    private lateinit var zoom: ImageView
    private lateinit var positionAdjust: ImageView
    private var adapter = LogAdapter()
    private var scrollToBottom = true

    private val drag = Drag()
    private val position = Drag()
    private val dragInBrief = Drag()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override fun onFinishInflate() {
        super.onFinishInflate()
        controlBar = findViewById(R.id.control_bar)
        logListRv = findViewById(R.id.log_list)
        tabScrollView = findViewById(R.id.tab_scrollview)
        zoom = findViewById(R.id.zoom)
        positionAdjust = findViewById(R.id.position_adjust)
        logListRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        logListRv.adapter = adapter
        logListRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                scrollToBottom = when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        val last = (logListRv.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                        last == adapter.itemCount - 1
                    }
                    else -> false
                }
            }
        })
        logListRv.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect?, itemPosition: Int, parent: RecyclerView?) {
                outRect?.top = 5
                outRect?.bottom = 5
            }
        })
        zoom.setOnTouchListener { _, event: MotionEvent? -> zoomOnTouchEvent(event) }
        positionAdjust.setOnTouchListener { _, event: MotionEvent? -> adjustPosition(event) }
        tabScrollView.setOnTouchListener { _, event: MotionEvent? ->
            adjustPositionInBrief(event)
        }
    }

    private fun adjustPositionInBrief(event: MotionEvent?): Boolean {
        if (viewMode == Config.Mode.Brief) {
            val x = event?.rawX ?: 0f
            val y = event?.rawY ?: 0f
            when (event?.action) {
                MotionEvent.ACTION_MOVE -> {
                    if (dragInBrief.dragStartX == 0f || dragInBrief.dragStartY == 0f) {
                        dragInBrief.dragStartX = x
                        dragInBrief.dragStartY = y
                        adjustView?.onMoveStart()
                    } else {
                        val dx = dragInBrief.dx(x)
                        val dy = dragInBrief.dy(y)
                        adjustView?.onMoveUpdate(dx, dy)
                    }
                    return true
                }
                else -> {
                    dragInBrief.drag = false
                    dragInBrief.dragStartX = 0f
                    dragInBrief.dragStartY = 0f
                }
            }
            return false
        }
        return false
    }

    private fun adjustPosition(event: MotionEvent?): Boolean {
        val x = event?.rawX ?: 0f
        val y = event?.rawY ?: 0f
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                position.drag = true
                position.dragStartX = x
                position.dragStartY = y
                adjustView?.onMoveStart()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = position.dx(x)
                val dy = position.dy(y)
                if (position.drag) {
                    adjustView?.onMoveUpdate(dx, dy)
                    return true
                }
            }
            else -> {
                if (position.drag) {
                    adjustView?.onMoveEnd()
                }
                position.drag = false
            }
        }
        return false
    }

    private fun zoomOnTouchEvent(event: MotionEvent?): Boolean {
        val x = event?.x ?: 0f
        val y = event?.y ?: 0f
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                drag.drag = true
                drag.dragStartX = x
                drag.dragStartY = y
                adjustView?.onZoomStart()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = drag.dx(x)
                val dy = drag.dy(y)
                if (drag.drag) {
                    adjustView?.onZoomUpdate(dx, dy)
                    return true
                }
            }
            else -> {
                if (drag.drag) {
                    adjustView?.onZoomEnd()
                }
                drag.drag = false
            }
        }
        return false
    }

    fun setControlOrientation(orientation: ControlBar.WindowAction) {
        controlBar.windowAction = orientation
    }

    override fun getLevel(): List<Level> = viewAction?.getLevels() ?: mutableListOf()

    override fun selectNewLevel(level: Level) {
        viewAction?.selectNewLevel(level)
    }

    override fun getTags(): List<String>? = viewAction?.getHistoryTags()

    override fun selectNewTag(tag: String) {
        viewAction?.selectNewTag(tag)
    }

    override fun getKeyWords(): List<String> = viewAction?.getKeyWords() ?: mutableListOf()

    override fun selectKeyWord(kw: String) {
        viewAction?.selectNewKeyWord(kw)
    }

    override fun clearLogByControl() {
        viewAction?.clearLog()
    }

    override fun upload() {
        viewAction?.upload()
    }

    override fun showPrefix(): Boolean = viewAction?.showPrefix() ?: true

    override fun orientation(): Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    override fun isResume(): Boolean = viewAction?.isResume() ?: true

    override fun togglePrefix() {
        viewAction?.togglePrefix()
    }

    override fun toggleResume() {
        viewAction?.toggleResume()
    }

    override fun clearLog() {
        logList.clear()
        adapter.notifyDataSetChanged()
    }

    override fun notifyView() {
        adapter.notifyDataSetChanged()
    }

    override fun showAllLogs(logs: List<Log>) {
        logList.clear()
        logList.addAll(logs)
        adapter.notifyDataSetChanged()
        if (adapter.itemCount > 0) {
            logListRv.scrollToPosition(adapter.itemCount - 1)
        }
    }

    override fun updateLogList(log: Log) {
        logList.add(log)
        if (scrollToBottom) {
            logListRv.scrollToPosition(adapter.itemCount - 1)
        }
        adapter.notifyItemInserted(adapter.itemCount - 1)
    }

    override fun scrollToTop() {
        scrollToBottom = false
        logListRv.scrollToPosition(0)
    }

    override fun scrollToBottom() {
        scrollToBottom = true
        logListRv.scrollToPosition(adapter.itemCount - 1)
    }

    override fun setSuffix(suffix: String?) {
        controlBar.name = suffix ?: ""
    }

    override fun setMode(mode: Config.Mode) {
        this.viewMode = mode
        when (mode) {
            Config.Mode.Default -> {
                tabScrollView.intercept = false
                controlBar.mode = mode
                logListRv.visibility = View.VISIBLE
                positionAdjust.visibility = View.VISIBLE
                zoom.visibility = View.VISIBLE
            }
            Config.Mode.Brief -> {
                tabScrollView.intercept = true
                controlBar.mode = mode
                logListRv.visibility = View.GONE
                zoom.visibility = View.GONE
                positionAdjust.visibility = View.GONE
            }
        }
    }

    override fun getViewContext(): Context = context

    inner class LogAdapter : RecyclerView.Adapter<VH>() {
        override fun getItemCount(): Int = logList.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(TextView(context))

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.updateLog(logList[position])
        }
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {

        fun updateLog(log: Log) {
            val v = itemView as TextView
            v.text = log.log
            viewAction?.let {
                val key = it.getKeyWord()
                var text = if (showPrefix()) log.log else log.content
                v.setTextColor(Color.WHITE)
                val sp = SpannableString(text)
                sp.setSpan(ForegroundColorSpan(log.level.color()), 0, text.length, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE)
                key?.takeUnless { s ->
                    s.isEmpty()
                }?.also { s ->
                    //标记关键字符串
                    var index = text.indexOf(s)
                    var position = index
                    while (index >= 0 && position + s.length < text.length) {
                        sp.setSpan(BackgroundColorSpan(Color.YELLOW), position, position + s.length, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE)
                        text = text.substring(position + s.length)
                        index = text.indexOf(s)
                        position += s.length + index
                    }
                }
                v.text = sp
            }
        }
    }

    interface AdjustView {
        fun onZoomStart()
        fun onZoomUpdate(dx: Float, dy: Float)
        fun onZoomEnd()

        fun onMoveStart()
        fun onMoveUpdate(dx: Float, dy: Float)
        fun onMoveEnd()
    }

    data class Drag(var dragStartX: Float = 0f, var dragStartY: Float = 0f, var drag: Boolean = false) {
        fun dx(x: Float) = x - dragStartX

        fun dy(y: Float) = y - dragStartY
    }
}