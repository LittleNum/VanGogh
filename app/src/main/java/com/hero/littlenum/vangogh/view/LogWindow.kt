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
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.TextView
import com.hero.littlenum.vangogh.R
import com.hero.littlenum.vangogh.data.Level
import com.hero.littlenum.vangogh.data.Log
import com.hero.littlenum.vangogh.present.ILogContract

class LogWindow : ConstraintLayout, ILogContract.ILogWindowView, ControlBar.ControlListener {
    var viewAction: IViewAction? = null
        set(value) {
            field = value
            controlBar.listener = this
        }
    private var logList = mutableListOf<Log>()
    private lateinit var controlBar: ControlBar
    private lateinit var logListRv: RecyclerView
    private lateinit var tabScrollView: HorizontalScrollView
    private var adapter = LogAdapter()
    private var scrollToBottom = true

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override fun onFinishInflate() {
        super.onFinishInflate()
        controlBar = findViewById(R.id.control_bar)
        logListRv = findViewById(R.id.log_list)
        tabScrollView = findViewById(R.id.tab_scrollview)
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

    override fun upload(name: String) {
        viewAction?.upload(name)
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

    override fun getLogName(): String = controlBar.name

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
}