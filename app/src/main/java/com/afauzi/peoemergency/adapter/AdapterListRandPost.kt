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
import com.squareup.picasso.Picasso

class AdapterListRandPost(
    private val callClickListener: CallClickListener,
    private val listItemRandomPost: ArrayList<ModelItemRandomPost>
    ): RecyclerView.Adapter<AdapterListRandPost.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.item_name)
        val photoProfile: ImageView = itemView.findViewById(R.id.item_photo_profile)
        val postLocation: TextView = itemView.findViewById(R.id.item_location_post)
        val postDesc: TextView = itemView.findViewById(R.id.item_description_post)
        val postDate: TextView = itemView.findViewById(R.id.item_date_post)
        val postImage: ImageView = itemView.findViewById(R.id.item_image_post)
        val cardContent: CardView = itemView.findViewById(R.id.item_cardView_content_random_post)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list_random_post, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = listItemRandomPost[position]
        holder.username.text = currentItem.user.username
        Picasso.get().load(currentItem.user.photo_profile).placeholder(R.drawable.ic_baseline_person_24).into(holder.photoProfile)
        holder.postLocation.text = currentItem.post_location
        holder.postDesc.text = currentItem.post_desc
        holder.postDate.text = currentItem.post_date
        Picasso.get().load(currentItem.post_image).placeholder(R.drawable.ic_baseline_image_24).into(holder.postImage)

        holder.cardContent.setOnClickListener {
            callClickListener.onClickListener(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return listItemRandomPost.size
    }

    interface CallClickListener {
        fun onClickListener(data: ModelItemRandomPost)
        fun onLongClickListener(data: ModelItemRandomPost)
        fun onClickRemove(data: ModelItemRandomPost)
    }
}