package com.afauzi.peoemergency.utils

import android.annotation.SuppressLint
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText
import java.text.SimpleDateFormat
import java.util.*

object Library {
    /**
     * Declaration simpleDateFormat milik java
     */
    @SuppressLint("SimpleDateFormat")
    val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd MMM yyyy")

    /**
     * Declaration calendar milik java plus getInstance()
     */
    private val calendar: Calendar = Calendar.getInstance()

    /**
     * Variabel date berisi nilai date, yang sudah di konversikan dari simpleDateFormat.format() kedalam calendar.time
     */
    val currentDate: String = simpleDateFormat.format(calendar.time)

    /**
     * LOG
     */
    const val TAG = "myAppLog"

    fun clearText(inputText: EditText) {
        Log.i(TAG, "input clear text")
        inputText.text?.clear()
    }

}