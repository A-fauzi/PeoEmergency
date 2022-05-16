package com.afauzi.peoemergency.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.afauzi.peoemergency.R
import com.airbnb.lottie.LottieAnimationView
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
    fun dialogErrors(layoutInflater: LayoutInflater, context: Context, dialogText: String, rawRes: Int) {
        val dialog = MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialog_rounded)
        val dialogView: View = layoutInflater.inflate(R.layout.dialog_errors, null)
        val textDialog = dialogView.findViewById<TextView>(R.id.tv_dialog_error)
        val animationDialog = dialogView.findViewById<LottieAnimationView>(R.id.animationDialog)

        animationDialog.setAnimation(rawRes)
        textDialog.text = dialogText

        dialog.setView(dialogView)
        dialog.setTitle("Uppss..")
        dialog.show()

    }
  /**
     * Dialog Reactions Emotion
     */
    fun dialogReactions(layoutInflater: LayoutInflater, context: Context, data: String) {
        val dialog = MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialog_rounded)
        val dialogView: View = layoutInflater.inflate(R.layout.dialog_reactions, null)
        val emotLike: CardView = dialogView.findViewById(R.id.cv_emotion_like)
        val emotDislike: CardView = dialogView.findViewById(R.id.cv_emotion_dislike)
        val emotApplause: CardView = dialogView.findViewById(R.id.cv_emotion_applause)
        val emotLove: CardView = dialogView.findViewById(R.id.cv_emotion_love)
        val emotAngry: CardView = dialogView.findViewById(R.id.cv_emotion_angry)

        emotLike.setOnClickListener { Toast.makeText(context, "$data Like Clicked", Toast.LENGTH_SHORT).show() }
        emotDislike.setOnClickListener { Toast.makeText(context, "$data Dislike Clicked", Toast.LENGTH_SHORT).show() }
        emotApplause.setOnClickListener { Toast.makeText(context, "$data Applause Clicked", Toast.LENGTH_SHORT).show() }
        emotLove.setOnClickListener { Toast.makeText(context, "$data Love Clicked", Toast.LENGTH_SHORT).show() }
        emotAngry.setOnClickListener { Toast.makeText(context, "$data Angry Clicked", Toast.LENGTH_SHORT).show() }

        dialog.setView(dialogView)
        dialog.setTitle("Reactions")
        dialog.show()

    }

    /**
     * Current date and time WIB Format
     */
    @SuppressLint("SimpleDateFormat")
    val currentDateAndTime: String = SimpleDateFormat("dd MMM yyyy | HH:mm:ss zzz").format(Date(System.currentTimeMillis()))

}