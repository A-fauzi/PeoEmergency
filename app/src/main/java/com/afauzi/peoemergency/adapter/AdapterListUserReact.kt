package com.afauzi.peoemergency.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.data_model.ModelReactPost
import com.afauzi.peoemergency.databinding.ItemListUserReactionsBinding
import com.squareup.picasso.Picasso

class AdapterListUserReact(private val listItemUserReact: ArrayList<ModelReactPost>): RecyclerView.Adapter<AdapterListUserReact.ViewHolder>() {
    class ViewHolder(val binding: ItemListUserReactionsBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListUserReactionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(listItemUserReact[position]){
                Picasso.get().load(photoProfile).placeholder(R.drawable.person_place_holder).into(binding.itemListUserPhoto)
                binding.itemListUsername.text = username
                binding.itemListEmotionReact.text = statusReaction
            }
        }
    }

    override fun getItemCount(): Int {
        return listItemUserReact.size
    }
}