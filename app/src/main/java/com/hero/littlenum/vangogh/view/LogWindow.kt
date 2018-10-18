package com.hero.littlenum.vangogh.view

import android.content.Context
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.TextView
import com.hero.littlenum.vangogh.R
import com.hero.littlenum.vangogh.data.Log
import com.hero.littlenum.vangogh.present.ILogContract

class LogWindow : ConstraintLayout, ILogContract.ILogWindowView {
    lateinit var viewAction: IViewAction
    private var logList = mutableListOf<Log>()
    private lateinit var logListRv: RecyclerView
    private lateinit var tabScrollView: HorizontalScrollView
    private lateinit var levelSelect: TextView
    private lateinit var tagSelect: TextView
    private var adapter = LogAdapter()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override fun onFinishInflate() {
        super.onFinishInflate()
        logListRv = findViewById(R.id.log_list)
        tabScrollView = findViewById(R.id.tab_scrollview)
        levelSelect = findViewById(R.id.level)
        tagSelect = findViewById(R.id.tag)
        logListRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        logListRv.adapter = adapter
    }

    override fun clearLog() {
        logList.clear()
        adapter.notifyDataSetChanged()
    }

    override fun showAllLogs(logs: List<Log>) {
        logList.clear()
        logList.addAll(logs)
    }

    override fun updateLogList(log: Log) {
        logList.add(log)
        adapter.notifyItemInserted(logList.size - 1)
    }

    override fun scrollToTop() {
        logListRv.scrollToPosition(0)
    }

    override fun scrollToBottom() {
        logListRv.scrollToPosition(adapter.itemCount - 1)
    }

    inner class LogAdapter : RecyclerView.Adapter<VH>() {
        override fun getItemCount(): Int = logList.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
                VH(TextView(context))

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.updateLog(logList[position])
        }
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {

        fun updateLog(log: Log) {
            val v = itemView as TextView
            val key = viewAction.getKeyWord()
            v.text = if (viewAction.showPrefix()) log.log else log.content
            v.setTextColor(Color.WHITE)
        }
    }
}