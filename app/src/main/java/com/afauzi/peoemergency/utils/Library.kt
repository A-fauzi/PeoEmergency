package com.afauzi.peoemergency.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.afauzi.peoemergency.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

object Library {
    /**
     * LOG
     */
    const val TAG = "myAppLog"

    fun clearText(inputText: EditText) {
        Log.i(TAG, "input clear text")
        inputText.text?.clear()
    }

    /**
     * Dialog Error
     */
    fun dialogErrors(layoutInflater: LayoutInflater, context: Context, dialogText: String) {
        val dialog = MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialog_rounded)
        val dialogView: View = layoutInflater.inflate(R.layout.dialog_errors, null)
        val textDialog = dialogView.findViewById<TextView>(R.id.tv_dialog_error)
        textDialog.text = dialogText

        dialog.setView(dialogView)
        dialog.setTitle("Uppss..")
        dialog.show()

    }

}