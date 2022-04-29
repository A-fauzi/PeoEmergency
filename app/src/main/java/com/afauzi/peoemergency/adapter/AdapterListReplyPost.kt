package com.afauzi.peoemergency.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.dataModel.ModelItemRandomPost
import com.afauzi.peoemergency.dataModel.ModelReplyPost
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AdapterListReplyPost(
    private val callClickListener: CallClickListener,
    private val listItemReplyPost: ArrayList<ModelReplyPost>
) : RecyclerView.Adapter<AdapterListReplyPost.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoProfile = itemView.findViewById<CircleImageView>(R.id.item_photo_profile_user_reply)
        val username = itemView.findViewById<TextView>(R.id.item_name_user_reply)
        val currentDate = itemView.findViewById<TextView>(R.id.item_date_post_user_reply)
        val textReply = itemView.findViewById<TextView>(R.id.item_description_post_user_reply)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_reply_comment, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = listItemReplyPost[position]
        Picasso.get().load(currentItem.userPhoto).placeholder(R.drawable.person_place_holder)
            .into(holder.photoProfile)
        holder.username.text = currentItem.username
        holder.currentDate.text = currentItem.dateReply
        holder.textReply.text = currentItem.textReply
    }

    override fun getItemCount(): Int {
        return listItemReplyPost.size
    }

    interface CallClickListener {
        fun onLongClickListener(data: ModelItemRandomPost)
        fun onClickRemove(data: ModelItemRandomPost)
    }
}