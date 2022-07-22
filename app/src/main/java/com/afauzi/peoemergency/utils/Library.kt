package com.afauzi.peoemergency.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
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
    const val TAG = "library"

    fun clearText(inputText: EditText) {
        Log.i(TAG, "input clear text")
        inputText.text?.clear()
    }

    /**
     * Dialog Error
     */
    fun dialogErrors(
        layoutInflater: LayoutInflater,
        context: Context,
        dialogText: String,
        rawRes: Int
    ) {
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


    private fun reactHandle(dataPostId: String, dataUserName: String, dataPhotoProfile: String, dataUid: String, htmlCode: String) {
        val unicode = Html.fromHtml(htmlCode)

        FirebaseServiceInstance.databaseReference = FirebaseServiceInstance.firebaseDatabase.getReference("postRandom").child(dataPostId).child("userReact").child(dataUid)
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["username"] = dataUserName
        hashMap["photoProfile"] = dataPhotoProfile
        hashMap["statusReaction"] = unicode.toString()
        hashMap["status"] = "reacted"
        FirebaseServiceInstance.databaseReference.setValue(hashMap).addOnCompleteListener { reactionUpload ->
            if (reactionUpload.isSuccessful) {
                Log.d(TAG, "$dataUserName successfully reaction $unicode")
            } else {
                Log.d(TAG, "$dataUserName not successfully reaction: ${reactionUpload.exception?.localizedMessage}")
            }
        }.addOnFailureListener { reactionUploadFail ->
            Log.d(TAG, reactionUploadFail.localizedMessage!!)
        }

    }

    /**
     * Dialog Reactions Emotion
     */
    fun dialogReactions(
        layoutInflater: LayoutInflater,
        context: Context,
        dataPostId: String,
        dataUid: String,
        dataUserName: String,
        dataPhotoProfile: String
    ) {

        val dialogView: View = layoutInflater.inflate(R.layout.dialog_reactions, null)
        val dialog = MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialog_rounded)
            .setView(dialogView)
            .setTitle("Reactions")
            .show()
        val emotLike: CardView = dialogView.findViewById(R.id.cv_emotion_like)
        val emotDislike: CardView = dialogView.findViewById(R.id.cv_emotion_dislike)
        val emotApplause: CardView = dialogView.findViewById(R.id.cv_emotion_applause)
        val emotLove: CardView = dialogView.findViewById(R.id.cv_emotion_love)
        val emotAngry: CardView = dialogView.findViewById(R.id.cv_emotion_angry)

        emotLike.setOnClickListener {
            reactHandle(dataPostId, dataUserName, dataPhotoProfile, dataUid, "&#x1f44d;")
            dialog.dismiss()
        }
        emotDislike.setOnClickListener {
            reactHandle(dataPostId, dataUserName, dataPhotoProfile, dataUid, "&#x1f44e;")
            dialog.dismiss()
        }
        emotApplause.setOnClickListener {
            reactHandle(dataPostId, dataUserName, dataPhotoProfile, dataUid, "&#x1f44f;")
            dialog.dismiss()
        }
        emotLove.setOnClickListener {
            reactHandle(dataPostId, dataUserName, dataPhotoProfile, dataUid, "&#x2764;")
            dialog.dismiss()
        }
        emotAngry.setOnClickListener {
            reactHandle(dataPostId, dataUserName, dataPhotoProfile, dataUid, "&#x1f621;")
            dialog.dismiss()
        }
    }

    /**
     * Current date and time WIB Format
     */
    @SuppressLint("SimpleDateFormat")
    val currentDateAndTime: String =
        SimpleDateFormat("dd MMM yyyy | HH:mm:ss").format(Date(System.currentTimeMillis()))

}