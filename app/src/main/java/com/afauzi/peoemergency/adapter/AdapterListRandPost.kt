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
        val usernameAndDate: TextView = itemView.findViewById(R.id.username_and_date_random_post)
        val photoProfile: ImageView = itemView.findViewById(R.id.profile_photo_random_post)
        val descPost: TextView = itemView.findViewById(R.id.descript_random_post)
        val media: ImageView = itemView.findViewById(R.id.media_random_post)
        val cardContent: CardView = itemView.findViewById(R.id.cardView_content_random_post)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list_random_post, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = listItemRandomPost[position]
        holder.usernameAndDate.text = "${currentItem.username} | ${currentItem.date}"
        Picasso.get().load(currentItem.profilePhoto).placeholder(R.drawable.ic_baseline_person_24).into(holder.photoProfile)
        Picasso.get().load(currentItem.media).placeholder(R.drawable.ic_baseline_image_24).into(holder.media)
        holder.descPost.text = currentItem.desc

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