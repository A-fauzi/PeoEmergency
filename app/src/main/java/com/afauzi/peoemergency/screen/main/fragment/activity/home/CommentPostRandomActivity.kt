package com.afauzi.peoemergency.screen.main.fragment.activity.home

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.afauzi.peoemergency.databinding.ActivityCommentPostRandomBinding

class CommentPostRandomActivity : AppCompatActivity() {

    companion object {
        const val TAG = "CommentPostRandomActivity"
    }

    private lateinit var binding: ActivityCommentPostRandomBinding

    private fun initView() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCommentPostRandomBinding.inflate(layoutInflater)

        setContentView(binding.root)

        getDataPost()
    }


    private fun getDataPost() {
        if (intent.extras != null) {
            val username = intent.extras?.getString("username")
            val userId = intent.extras?.getString("userId")
            val photoProfile = intent.extras?.getString("photoProfile")
            val textPost = intent.extras?.getString("textPost")
            val postId = intent.extras?.getString("postId")
            val photoPosting = intent.extras?.getString("photoPosting")
            val datePost = intent.extras?.getString("datePosting")

            Log.i(TAG, "$username")
            Log.i(TAG, "$userId")
            Log.i(TAG, "$photoProfile")
            Log.i(TAG, "$textPost")
            Log.i(TAG, "$postId")
            Log.i(TAG, "$photoPosting")
            Log.i(TAG, "$datePost")
        }
    }

}