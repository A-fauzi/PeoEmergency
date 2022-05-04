package com.afauzi.peoemergency.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.dataModel.ModelItemRandomPost
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.auth
import com.squareup.picasso.Picasso


class AdapterListRandPost(
    private val callClickListener: CallClickListener,
    private val listItemRandomPost: ArrayList<ModelItemRandomPost>
) : RecyclerView.Adapter<AdapterListRandPost.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.item_name)
        val photoProfile: ImageView = itemView.findViewById(R.id.item_photo_profile)
        val postLocation: TextView = itemView.findViewById(R.id.item_location_post)
        val postDesc: TextView = itemView.findViewById(R.id.item_description_post)
        val postDate: TextView = itemView.findViewById(R.id.item_date_post)
        val postImage: ImageView = itemView.findViewById(R.id.item_image_post)
        val cardContent: CardView = itemView.findViewById(R.id.item_cardView_content_random_post)
        val btnMorePost: ImageView = itemView.findViewById(R.id.item_btn_more_post)
        val replyPost: ImageView = itemView.findViewById(R.id.item_to_comment_post)
        val likePost: ImageView = itemView.findViewById(R.id.item_to_like_post)
        val sharePost: ImageView = itemView.findViewById(R.id.item_to_share_post)
        val commentCount: TextView = itemView.findViewById(R.id.item_tv_count_comment)
        val likeCount: TextView = itemView.findViewById(R.id.item_tv_count_like)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_random_post, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = listItemRandomPost[position]
        holder.username.text = currentItem.username
        holder.postLocation.text = currentItem.postLocationCityName
        holder.postDesc.text = currentItem.postText
        holder.postDate.text = currentItem.postDate
        holder.commentCount.text = currentItem.countCommentPostUser
        holder.likeCount.text = currentItem.countLikePostUser

        Picasso.get().load(currentItem.photoProfile).placeholder(R.drawable.person_place_holder)
            .into(holder.photoProfile)

        if (currentItem.photoPost == null || currentItem.photoPost == "") {
            holder.postImage.setImageResource(0)
        } else {
            Picasso.get().load(currentItem.photoPost)
                .placeholder(R.drawable.image_post_place_holder).resize(500, 500)
                .centerCrop()
                .into(holder.postImage)
        }

        val uid = auth.currentUser!!.uid
        if (currentItem.userId != uid) {
            holder.btnMorePost.visibility = View.INVISIBLE
        }

        holder.btnMorePost.setOnClickListener {
            callClickListener.onClickListenerPostMore(currentItem)
        }

        holder.cardContent.setOnClickListener {
            callClickListener.onClickListenerCardView(currentItem)
        }

        holder.postImage.setOnClickListener {
            callClickListener.onClickListenerImageView(currentItem)
        }

        holder.replyPost.setOnClickListener {
            callClickListener.onClickListenerPostReply(currentItem)
        }
        holder.likePost.setOnClickListener {
            holder.likePost.setImageResource(R.drawable.ic_like)
            callClickListener.onClickListenerPostLike(currentItem)
        }
        holder.sharePost.setOnClickListener {
            callClickListener.onClickListenerPostShare(currentItem)
        }


    }

    override fun getItemCount(): Int {
        return listItemRandomPost.size
    }

    interface CallClickListener {
        fun onClickListenerCardView(data: ModelItemRandomPost)
        fun onClickListenerImageView(data: ModelItemRandomPost)
        fun onClickListenerPostReply(data: ModelItemRandomPost)
        fun onClickListenerPostLike(data: ModelItemRandomPost)
        fun onClickListenerPostShare(data: ModelItemRandomPost)
        fun onClickListenerPostMore(data: ModelItemRandomPost)
        fun onLongClickListener(data: ModelItemRandomPost)
        fun onClickRemove(data: ModelItemRandomPost)
    }
}