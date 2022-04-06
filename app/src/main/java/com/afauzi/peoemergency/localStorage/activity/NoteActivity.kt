package com.afauzi.peoemergency.localStorage.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.afauzi.peoemergency.databinding.ActivityNoteBinding
import com.afauzi.peoemergency.localStorage.Constant
import com.afauzi.peoemergency.localStorage.ModelNote
import com.afauzi.peoemergency.localStorage.NoteDB
import com.afauzi.peoemergency.localStorage.adapter.AdapterNote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteBinding

    private val db by lazy { NoteDB(this) }
    private lateinit var noteAdapter: AdapterNote

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpListener()
        setUpRecyclerView()

    }

    override fun onStart() {
        super.onStart()
        loadNote()
    }

    /**
     * Mendapatkan seluruh data note
     */
    private fun loadNote(){
        CoroutineScope(Dispatchers.IO).launch {
            val notes = db.noteDao().getNotes()
            Log.d("MainActivity", "dbResponse $notes")
            withContext(Dispatchers.Main) {
                noteAdapter.setData(notes)
            }
        }
    }

    /**
     *
     */
    private fun setUpListener() {
        binding.buttonCreate.setOnClickListener {
            intentEdit(0, Constant.TYPE_CREATE)
        }
    }

    private fun intentEdit(noteId: Int, intentType: Int) {
        startActivity(Intent(applicationContext, EditNoteActivity::class.java)
            .putExtra("intent_id", noteId)
            .putExtra("intent_type", intentType))
    }

    /**
     * Setup Recyclerview
     */
    private fun setUpRecyclerView() {
        noteAdapter = AdapterNote(arrayListOf(), object : AdapterNote.OnAdapterListener {
            override fun onClick(note: ModelNote) {
                // read detail note
                intentEdit(note.id, Constant.TYPE_READ)
            }

            override fun onUpdate(note: ModelNote) {
                intentEdit(note.id, Constant.TYPE_UPDATE)
            }

            override fun onDelete(note: ModelNote) {
                deleteDialog(note)
            }

        })
        binding.listNote.apply {
            layoutManager = LinearLayoutManager(this@NoteActivity)
            adapter = noteAdapter
        }

    }

    private fun deleteDialog(note: ModelNote) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle("confirmation")
            setMessage("Are Your Sure ${note.title}")

            setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                CoroutineScope(Dispatchers.IO).launch {
                    db.noteDao().deleteNote(note)
                    loadNote()
                }
            }
        }
        alertDialog.show()
    }
}