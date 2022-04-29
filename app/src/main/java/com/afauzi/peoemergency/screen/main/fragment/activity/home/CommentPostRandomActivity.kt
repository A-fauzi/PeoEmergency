package com.afauzi.peoemergency.screen.main.fragment.activity.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.databinding.ActivityCommentPostRandomBinding
import com.afauzi.peoemergency.screen.main.MainActivity
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.auth
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.databaseReference
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.firebaseDatabase
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class CommentPostRandomActivity : AppCompatActivity() {

    companion object {
        const val TAG = "CommentPostRandomActivity"
    }

    private lateinit var binding: ActivityCommentPostRandomBinding

    private lateinit var imageProfile: CircleImageView
    private lateinit var tvUsername: TextView
    private lateinit var tvPostDate: TextView
    private lateinit var tvPostText: TextView
    private lateinit var ivPostImage: ImageView
    private lateinit var rvCommentPostRandom: ShimmerRecyclerView
    private lateinit var ivPhotoProfileUserReply: CircleImageView
    private lateinit var etInputCommentReply: EditText
    private lateinit var btnSendReply: Button

    // get Data CurrentUSer
    private var photoProfileCurrentUserDataBundle: String? = null
    private var usernameCurrentUserDataBundle: String? = null

    // Get data post
    private var postId: String? = null

    private fun initView() {
        imageProfile = binding.ivDetailProfileUserPostRandom
        tvUsername = binding.tvDetailUsernamePostRandom
        tvPostDate = binding.tvDetailDatePostRandom
        tvPostText = binding.tvDetailDescPostRandom
        ivPostImage = binding.ivDetailPostRandom
        rvCommentPostRandom = binding.rvReplyComment
        ivPhotoProfileUserReply = binding.ivPhotoProfileUserReply
        etInputCommentReply = binding.etInputComment
        btnSendReply = binding.btnSendReply
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCommentPostRandomBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initView()

        getDataPost()
    }

    override fun onResume() {
        super.onResume()

        btnSendReply.setOnClickListener {
            storeCommentReplyPostRandom()
        }

    }


    private fun getDataPost() {
        if (intent.extras != null) {

            // Data Reply Current User
            photoProfileCurrentUserDataBundle = intent.extras?.getString("photoProfileCurrentUser")
            usernameCurrentUserDataBundle = intent.extras?.getString("usernameCurrentUser")

            val username = intent.extras?.getString("username")
            Log.i(TAG, username.toString())

            val userId = intent.extras?.getString("userId")
            Log.i(TAG, userId.toString())

            val photoProfile = intent.extras?.getString("photoProfile")
            Log.i(TAG, photoProfile.toString())

            val textPost = intent.extras?.getString("textPost")
            Log.i(TAG, textPost.toString())

            postId = intent.extras?.getString("postId")
            Log.i(TAG, postId.toString())

            val photoPosting = intent.extras?.getString("photoPosting")
            val datePost = intent.extras?.getString("datePosting")


            Picasso
                .get()
                .load(photoProfileCurrentUserDataBundle)
                .placeholder(R.drawable.person_place_holder)
                .into(ivPhotoProfileUserReply)

            Picasso
                .get()
                .load(photoProfile)
                .placeholder(R.drawable.person_place_holder)
                .into(imageProfile)
            tvUsername.text = username
            tvPostDate.text = datePost
            tvPostText.text = textPost

            if (photoPosting == null || photoPosting == "") {
                ivPostImage.visibility = View.GONE
            } else {
                Picasso
                    .get()
                    .load(photoPosting)
                    .resize(500, 500)
                    .centerCrop()
                    .placeholder(R.drawable.image_post_place_holder)
                    .into(ivPostImage)
            }

        }

        rvCommentPostRandom.visibility = View.GONE
    }

    private fun storeCommentReplyPostRandom() {
        // get data photoprofile currentUser
        Log.i(TAG, "Photo profile currentUser: $photoProfileCurrentUserDataBundle")

        // get data username currentUser
        Log.i(TAG, "Username currentUser: $usernameCurrentUserDataBundle")

        // create data current date reply currentUser
        val currentDate = SimpleDateFormat("dd MMM yyyy | HH:mm:ss zzz").format(Date(System.currentTimeMillis()))
        Log.i(TAG, "current date reply: $currentDate")

        // Create Text Input Reply
        val textInputReply = etInputCommentReply.text
        Log.i(TAG, "Text input reply: $textInputReply")

        val uid = auth.currentUser!!.uid
        databaseReference = firebaseDatabase.getReference("postRandom").child(postId.toString()).child("userReplyPost").child(uid)
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["userPhoto"] = photoProfileCurrentUserDataBundle.toString()
        hashMap["username"] = usernameCurrentUserDataBundle.toString()
        hashMap["dateReply"] = currentDate
        hashMap["textReply"] = textInputReply.toString()

        databaseReference.setValue(hashMap).addOnCompleteListener { replyPostResult ->
            if (replyPostResult.isSuccessful) {
                Log.i(TAG, "Reply post success saved in database")
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Log.i(TAG, "Reply post not success saved in database: ${replyPostResult.exception?.localizedMessage}")
            }
        }.addOnFailureListener { replyResultFailure ->
            Log.i(TAG, "Reply result failure: ${replyResultFailure.localizedMessage}")
        }

        Log.i(TAG, "post id: $postId")

    }
}