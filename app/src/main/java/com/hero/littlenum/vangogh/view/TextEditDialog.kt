package com.hero.littlenum.vangogh.view

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import com.hero.littlenum.vangogh.R


class TextEditDialog(activity: Activity, val title: String, private val listener: TextEditListener) {
    private var dialog: Dialog = AlertDialog.Builder(activity)
            .setTitle(title)
            .setCancelable(true)
            .setPositiveButton(R.string.ensure) { _, _ -> clickPositive() }
            .setNegativeButton(R.string.cancel) { _, _ -> dismiss() }
            .setView(LayoutInflater.from(activity).inflate(R.layout.edit_dialog_text, null, false))
            .create()

    init {
        dialog.window.setType(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }

    private fun clickPositive() {
        val text = dialog.findViewById<EditText?>(R.id.edit)?.text?.toString()
        listener.onEdit(text)
    }

    companion object {
        fun showEditDialog(activity: Activity, title: String, listener: TextEditListener): TextEditDialog {
            val dialog = TextEditDialog(activity, title, listener)
            dialog.show()
            return dialog
        }

        fun showEditDialog(activity: Activity, title: Int, listener: TextEditListener): TextEditDialog {
            val dialog = TextEditDialog(activity, activity.getString(title), listener)
            dialog.show()
            return dialog
        }
    }


    interface TextEditListener {
        fun onEdit(text: String?)
    }
}