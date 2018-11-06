package com.hero.littlenum.vangogh.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.*
import com.hero.littlenum.vangogh.MainActivity
import com.hero.littlenum.vangogh.R
import com.hero.littlenum.vangogh.data.Level
import com.hero.littlenum.vangogh.task.Config
import com.hero.littlenum.vangogh.task.VanGoghService

class ControlBar : LinearLayout, View.OnClickListener {
    var name: String? = ""
        set(value) {
            field = value
            logName.text = name
        }
    var mode: Config.Mode = Config.Mode.Default
        set(value) {
            field = value
            val visible = if (field == Config.Mode.Default) View.VISIBLE else View.GONE
            logName.visibility = visible
            levelSelect.visibility = visible
            tagLayout.visibility = visible
            kwLayout.visibility = visible
            clear.visibility = visible
            resume.visibility = visible
            top.visibility = visible
            bottom.visibility = visible
            orientation.visibility = visible
            togglePrefix.visibility = visible
        }
    private lateinit var logName: TextView
    private lateinit var level: Spinner
    private lateinit var levelText: TextView
    private lateinit var levelSelect: RelativeLayout
    private lateinit var tagHistory: ImageView
    private lateinit var tag: TextView
    private lateinit var tagSpinner: Spinner
    private lateinit var tagLayout: RelativeLayout
    private lateinit var kwHistory: ImageView
    private lateinit var keyword: TextView
    private lateinit var kwSpinner: Spinner
    private lateinit var kwLayout: RelativeLayout
    private lateinit var clear: ImageView
    private lateinit var top: ImageView
    private lateinit var bottom: ImageView
    private lateinit var upload: ImageView
    private lateinit var orientation: ImageView
    private lateinit var togglePrefix: ImageView
    private lateinit var resume: ImageView
    private lateinit var close: ImageView
    private lateinit var tagDialog: TextEditDialog
    private lateinit var kwDialog: TextEditDialog

    private var adapter = ArrayAdapter<Level>(context, R.layout.level_spinner_item)
    private var tagAdapter = ArrayAdapter<String>(context, R.layout.level_spinner_item)
    private var kwAdapter = ArrayAdapter<String>(context, R.layout.level_spinner_item)

    private val tagEdit = { text: String? ->
        text?.let {
            listener?.selectNewTag(it)
            tagAdapter.clear()
            tagAdapter.addAll(listener?.getTags() ?: mutableListOf<String>())
            tag.text = it
        }
    }
    private val kwEdit = { text: String? ->
        text?.let {
            listener?.selectKeyWord(it)
            kwAdapter.clear()
            kwAdapter.addAll(listener?.getKeyWords() ?: mutableListOf<String>())
            keyword.text = it
        }
    }

    var listener: ControlListener? = null
        set(value) {
            field = value
            levelText.text = value?.getLevel()?.get(0)?.toString()
            adapter.addAll(value?.getLevel() ?: mutableListOf<Level>())
            tagAdapter.addAll(value?.getTags() ?: mutableListOf<String>())
        }

    var windowAction: WindowAction? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attributes: AttributeSet) : super(context, attributes)
    constructor(context: Context, attributes: AttributeSet, defStyleAttr: Int) : super(context, attributes, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()
        logName = findViewById(R.id.log_name)
        level = findViewById(R.id.level)
        levelText = findViewById(R.id.level_text)
        levelSelect = findViewById(R.id.level_select)
        tagLayout = findViewById(R.id.tag_layout)
        tagHistory = findViewById(R.id.tag_history)
        tag = findViewById(R.id.tag)
        tagSpinner = findViewById(R.id.tag_history_spinner)
        kwLayout = findViewById(R.id.kw_layout)
        kwHistory = findViewById(R.id.kw_history)
        keyword = findViewById(R.id.keyword)
        kwSpinner = findViewById(R.id.kw_history_spinner)
        clear = findViewById(R.id.clear)
        top = findViewById(R.id.top)
        bottom = findViewById(R.id.bottom)
        upload = findViewById(R.id.upload)
        orientation = findViewById(R.id.orientation)
        togglePrefix = findViewById(R.id.prefix)
        resume = findViewById(R.id.resume)
        close = findViewById(R.id.close)
        level.adapter = adapter
        tagSpinner.adapter = tagAdapter
        kwSpinner.adapter = kwAdapter

        initListener()
    }

    private fun initListener() {
        logName.text = name
        clear.setOnClickListener(this)
        levelSelect.setOnClickListener(this)
        tagHistory.setOnClickListener(this)
        tag.setOnClickListener(this)
        kwHistory.setOnClickListener(this)
        keyword.setOnClickListener(this)
        top.setOnClickListener(this)
        bottom.setOnClickListener(this)
        upload.setOnClickListener(this)
        orientation.setOnClickListener(this)
        togglePrefix.setOnClickListener(this)
        resume.setOnClickListener(this)
        close.setOnClickListener(this)
        level.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                listener?.also {
                    val level = it.getLevel()[position]
                    it.selectNewLevel(level)
                    levelText.text = level.toString()
                }
            }
        }
        tagSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                listener?.apply {
                    val t = this.getTags()?.get(position)
                    t?.let {
                        this.selectNewTag(t)
                        tag.text = t
                    }
                }
            }
        }
        kwSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                listener?.apply {
                    val kw = this.getKeyWords().get(position)
                    this.selectKeyWord(kw)
                    keyword.text = kw
                }
            }
        }
    }

    private fun showLevelSpinner() {
        level.performClick()
    }

    private fun showTagEdit() {
        if (::tagDialog.isInitialized) {
            tagDialog.show()
        } else {
            tagDialog = TextEditDialog.showEditDialog(MainActivity.activity, R.string.edit_tag_title, tagEdit)
        }
    }

    private fun showKeyWordEdit() {
        if (::kwDialog.isInitialized) {
            kwDialog.show()
        } else {
            kwDialog = TextEditDialog.showEditDialog(MainActivity.activity, "Key Word", kwEdit)
        }
    }

    private fun showTagHistorySpinner() {
        tagSpinner.performClick()
    }

    private fun showKwSpinner() {
        kwSpinner.performClick()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.level_select -> showLevelSpinner()
            R.id.tag_history -> showTagHistorySpinner()
            R.id.tag -> showTagEdit()
            R.id.kw_history -> showKwSpinner()
            R.id.keyword -> showKeyWordEdit()
            R.id.clear -> listener?.clearLogByControl()
            R.id.top -> listener?.scrollToTop()
            R.id.bottom -> listener?.scrollToBottom()
            R.id.upload -> {
                listener?.upload(v)
                Log.e("clickupload", "clickupload clickupload clickupload clickupload")
            }
            R.id.orientation -> windowAction?.toggleOrientation()
            R.id.prefix -> {
                listener?.togglePrefix()
            }
            R.id.resume -> {
                listener?.toggleResume()
                val show = listener?.isResume() ?: false
                resume.setImageResource(if (show) R.drawable.log_resume_new else R.drawable.log_stop_new)
            }
            R.id.close -> {
                windowAction?.closeWindow()
            }
        }
    }

    interface ControlListener {
        fun getLevel(): List<Level>
        fun selectNewLevel(level: Level)
        fun getTags(): List<String>?
        fun selectNewTag(tag: String)
        fun getKeyWords(): List<String>
        fun selectKeyWord(kw: String)
        fun clearLogByControl()
        fun scrollToTop()
        fun scrollToBottom()
        fun upload(v: View?)
        fun showPrefix(): Boolean
        fun orientation(): Int
        fun isResume(): Boolean
        fun togglePrefix()
        fun toggleResume()
    }

    interface WindowAction {
        fun toggleOrientation()
        fun closeWindow()
    }
}