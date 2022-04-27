package com.afauzi.peoemergency.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.databinding.ItemListGalleryExampleBinding
import com.squareup.picasso.Picasso
import java.io.File

class AdapterListGallery(private val fileArray: Array<File>) :
    RecyclerView.Adapter<AdapterListGallery.ViewHolder>() {
    class ViewHolder(private val binding: ItemListGalleryExampleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(file: File) {
            Picasso.get().load(file).placeholder(R.drawable.ic_baseline_image_24)
                .into(binding.localImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemListGalleryExampleBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(fileArray[position])
    }

    override fun getItemCount(): Int {
        return fileArray.size
    }
}