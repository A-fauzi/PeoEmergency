package com.afauzi.peoemergency.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.data_model.ModelItemRandomPost
import com.afauzi.peoemergency.databinding.ItemListRandomPostBinding
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.auth
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.databaseReference
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.firebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso


class AdapterListRandPost(
    private val callClickListener: CallClickListener,
    private val listItemRandomPost: ArrayList<ModelItemRandomPost>
) : RecyclerView.Adapter<AdapterListRandPost.ViewHolder>() {

    class ViewHolder(val binding: ItemListRandomPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListRandomPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(holder) {
            with(listItemRandomPost[position]) {

                binding.itemName.text = username
                binding.itemLocationPost.text = postLocationName
                binding.itemDescriptionPost.text = postText
                binding.itemDatePost.text = postDate
                binding.itemTvCountComment.text = countCommentPostUser
                binding.itemTvCountReact.text = countReactPostUser

                databaseReference = firebaseDatabase.getReference("postRandom").child("$postId").child("userReact").child("${auth.currentUser?.uid}")
                databaseReference.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val statusReact = snapshot.child("statusReaction").value.toString()
                            binding.itemTvCountReact.text = "$statusReact $countReactPostUser"
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

                Picasso.get().load(photoProfile).placeholder(R.drawable.person_place_holder).into(binding.itemPhotoProfile)

                if (photoPost == null || photoPost == "") {
                    binding.itemImagePost.setImageResource(0)
                } else {
                    Picasso.get().load(photoPost)
                        .placeholder(R.drawable.image_post_place_holder).resize(500, 500)
                        .centerCrop()
                        .into(binding.itemImagePost)
                }

                val uid = auth.currentUser!!.uid
                if (userId != uid) {
                    binding.itemBtnMorePostCardView.visibility = View.INVISIBLE
                }

                binding.itemCardViewContentRandomPost.setOnClickListener {
                    callClickListener.onClickListenerCardView(listItemRandomPost[position])
                }

                binding.itemBtnMorePostCardView.setOnClickListener {
                    callClickListener.onClickListenerPostMore(listItemRandomPost[position])
                }

                binding.itemImagePostCardView.setOnClickListener {
                    callClickListener.onClickListenerImageView(listItemRandomPost[position])
                }

                binding.itemToCommentPost.setOnClickListener {
                    callClickListener.onClickListenerPostReply(listItemRandomPost[position])
                }

                binding.itemToReactionsPost.setOnClickListener {
                    callClickListener.onClickListenerPostReact(listItemRandomPost[position])
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return listItemRandomPost.size
    }

    interface CallClickListener {
        fun onClickListenerCardView(data: ModelItemRandomPost)
        fun onClickListenerImageView(data: ModelItemRandomPost)
        fun onClickListenerPostReply(data: ModelItemRandomPost)
        fun onClickListenerPostReact(data: ModelItemRandomPost)
        fun onClickListenerPostShare(data: ModelItemRandomPost)
        fun onClickListenerPostMore(data: ModelItemRandomPost)
        fun onLongClickListener(data: ModelItemRandomPost)
        fun onClickRemove(data: ModelItemRandomPost)
    }

    companion object {
        const val TAG = "AdapterListRandomPost"
    }
}