package com.afauzi.peoemergency.localStorage.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.localStorage.ModelNote
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class AdapterNote(
    private val notes: ArrayList<ModelNote>,
    private val listener: OnAdapterListener
    ):RecyclerView.Adapter<AdapterNote.NoteViewHolder>() {

    interface OnAdapterListener {
        fun onClick(note: ModelNote)
        fun onUpdate(note: ModelNote)
        fun onDelete(note: ModelNote)
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.findViewById(R.id.item_title)
        val description: TextView = itemView.findViewById(R.id.item_description)
        val buttonDelete: ExtendedFloatingActionButton = itemView.findViewById(R.id.item_icon_delete)
        val mainLayout: RelativeLayout = itemView.findViewById(R.id.item_main_layout)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<ModelNote>) {
        notes.clear()
        notes.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.title.text = note.title
        holder.description.text = note.note

        holder.title.setOnClickListener { listener.onClick(note) }
        holder.buttonDelete.setOnClickListener { listener.onDelete(note) }
        holder.mainLayout.setOnClickListener { listener.onUpdate(note) }
    }

    override fun getItemCount(): Int {
        return notes.size
    }
}