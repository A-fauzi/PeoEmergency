package com.afauzi.peoemergency.screen.main.fragment.activity.home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.adapter.AdapterListReplyPost
import com.afauzi.peoemergency.data_model.ModelItemRandomPost
import com.afauzi.peoemergency.data_model.ModelReplyPost
import com.afauzi.peoemergency.databinding.ActivityCommentPostRandomBinding
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.auth
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.databaseReference
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.firebaseDatabase
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*

class CommentPostRandomActivity : AppCompatActivity(), AdapterListReplyPost.CallClickListener {

    companion object {
        const val TAG = "CommentPostRandomActivity"
    }

    private lateinit var binding: ActivityCommentPostRandomBinding

    private lateinit var imageProfile: CircleImageView
    private lateinit var tvUsername: TextView
    private lateinit var tvPostDate: TextView
    private lateinit var tvPostText: TextView
    private lateinit var ivPostImage: ImageView
    private lateinit var ivPhotoProfileUserReply: CircleImageView
    private lateinit var etInputCommentReply: EditText
    private lateinit var btnSendReply: Button
    private lateinit var rvCommentPostRandom: ShimmerRecyclerView
    private lateinit var listReplyPost: ArrayList<ModelReplyPost>

    // get Data CurrentUSer
    private var photoProfileCurrentUserDataBundle: String? = null
    private var usernameCurrentUserDataBundle: String? = null

    // Get data post
    private var postId: String? = null
    private var usernamePost: String? = null
    private var userIdPost: String? = null
    private var photoProfilePost: String? = null
    private var textPost: String? = null
    private var photoPost: String? = null
    private var datePost: String? = null

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

        recyclerViewListReplyPost()
    }

    override fun onResume() {
        super.onResume()

        btnSendReply.setOnClickListener {
            storeCommentReplyPostRandom()
            etInputCommentReply.text.clear()
        }

    }

    private fun getDataPost() {
        if (intent.extras != null) {

            // Data Reply Current User
            photoProfileCurrentUserDataBundle = intent.extras?.getString("photoProfileCurrentUser")
            usernameCurrentUserDataBundle = intent.extras?.getString("usernameCurrentUser")

            usernamePost = intent.extras?.getString("username")
            Log.i(TAG, usernamePost.toString())

            userIdPost = intent.extras?.getString("userId")
            Log.i(TAG, userIdPost.toString())

            photoProfilePost = intent.extras?.getString("photoProfile")
            Log.i(TAG, photoProfilePost.toString())

            textPost = intent.extras?.getString("textPost")
            Log.i(TAG, textPost.toString())

            postId = intent.extras?.getString("postId")
            Log.i(TAG, postId.toString())

            photoPost = intent.extras?.getString("photoPosting")
            datePost = intent.extras?.getString("datePosting")


            Picasso
                .get()
                .load(photoProfileCurrentUserDataBundle)
                .placeholder(R.drawable.person_place_holder)
                .into(ivPhotoProfileUserReply)

            Picasso
                .get()
                .load(photoProfilePost)
                .placeholder(R.drawable.person_place_holder)
                .into(imageProfile)
            tvUsername.text = usernamePost
            tvPostDate.text = datePost
            tvPostText.text = textPost

            if (photoPost == null || photoPost == "") {
                ivPostImage.visibility = View.GONE
            } else {
                Picasso
                    .get()
                    .load(photoPost)
                    .resize(500, 500)
                    .centerCrop()
                    .placeholder(R.drawable.image_post_place_holder)
                    .into(ivPostImage)
            }

        }
    }

    private fun randomString(len: Int): String {
        val random = SecureRandom()
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray()
        return (1..len).map { chars[random.nextInt(chars.size)] }.joinToString("")
    }

    private fun storeCommentReplyPostRandom() {
        // get data photoprofile currentUser
        Log.i(TAG, "Photo profile currentUser: $photoProfileCurrentUserDataBundle")

        // get data username currentUser
        Log.i(TAG, "Username currentUser: $usernameCurrentUserDataBundle")

        // create data current date reply currentUser
        val currentDate =
            SimpleDateFormat("dd MMM yyyy | HH:mm:ss zzz").format(Date(System.currentTimeMillis()))
        Log.i(TAG, "current date reply: $currentDate")

        // Create Text Input Reply
        val textInputReply = etInputCommentReply.text
        Log.i(TAG, "Text input reply: $textInputReply")

        val uid = auth.currentUser!!.uid
        databaseReference =
            firebaseDatabase.getReference("postRandom")
                .child(postId.toString())
                .child("userReplyPost")
                .child(randomString(25))
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["userPhoto"] = photoProfileCurrentUserDataBundle.toString()
        hashMap["username"] = usernameCurrentUserDataBundle.toString()
        hashMap["userId"] = uid
        hashMap["dateReply"] = currentDate
        hashMap["textReply"] = textInputReply.toString()

        databaseReference.setValue(hashMap).addOnCompleteListener { replyPostResult ->
            if (replyPostResult.isSuccessful) {
                Log.i(TAG, "Reply post success saved in database")
                showNotification("New message", "$usernameCurrentUserDataBundle Komentar disebuah postingan milik $usernamePost  ")
            } else {
                Log.i(
                    TAG,
                    "Reply post not success saved in database: ${replyPostResult.exception?.localizedMessage}"
                )
            }
        }.addOnFailureListener { replyResultFailure ->
            Log.i(TAG, "Reply result failure: ${replyResultFailure.localizedMessage}")
        }

        Log.i(TAG, "post id: $postId")

    }

    private fun recyclerViewListReplyPost() {
        rvCommentPostRandom = binding.rvReplyComment
        listReplyPost = arrayListOf()
        rvCommentPostRandom.setHasFixedSize(true)
        rvCommentPostRandom.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        getListReplyPost()
    }

    private fun getListReplyPost() {
        databaseReference = firebaseDatabase.getReference("postRandom").child(postId.toString())
            .child("userReplyPost")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    listReplyPost.clear()
                    for (listRepPost in snapshot.children.sortedByDescending { it.child("dateReply").value.toString() }) {
                        val list = listRepPost.getValue(ModelReplyPost::class.java)
                        listReplyPost.add(list!!)
                        rvCommentPostRandom.visibility = View.VISIBLE

                    }
                    rvCommentPostRandom.adapter =
                        AdapterListReplyPost(this@CommentPostRandomActivity, listReplyPost)
                } else {
                    rvCommentPostRandom.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onLongClickListener(data: ModelItemRandomPost) {
        TODO("Not yet implemented")
    }

    override fun onClickRemove(data: ModelItemRandomPost) {
        TODO("Not yet implemented")
    }

    private fun showNotification(contentTitle: String, contentText: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("n", "n", NotificationManager.IMPORTANCE_DEFAULT)
            val manager =
                ContextCompat.getSystemService(this, NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
            val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "n")
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.sample_logo)
                .setAutoCancel(true)
            val managerCompat = NotificationManagerCompat.from(this)
            val mediaPlayer: MediaPlayer = MediaPlayer.create(this, R.raw.notify_music)
            mediaPlayer.start()

            // ============= this is click event for notification to open application =================//

            val bundle = Bundle()
            // Data CurrentUser
            bundle.putString("photoProfileCurrentUser", photoProfileCurrentUserDataBundle)
            bundle.putString("usernameCurrentUser", usernameCurrentUserDataBundle)

            bundle.putString("username", "$usernamePost")
            bundle.putString("userId", "$userIdPost")
            bundle.putString("photoProfile", "$photoProfilePost")
            bundle.putString("textPost", "$textPost")
            bundle.putString("postId", "$postId")
            bundle.putString("photoPosting", "$photoPost")
            bundle.putString("datePosting", "$datePost")

            val intenNotification = Intent(this, CommentPostRandomActivity::class.java)
            intenNotification.putExtras(bundle)

            val contentIntent = PendingIntent.getActivity(
                this,
                0,
                intenNotification,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            builder.setContentIntent(contentIntent)

            //======================================================================================
            managerCompat.notify(999, builder.build())
        }
    }


}