package com.afauzi.peoemergency.local_storage.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.afauzi.peoemergency.databinding.ActivityEditNoteBinding
import com.afauzi.peoemergency.local_storage.Constant
import com.afauzi.peoemergency.local_storage.ModelNote
import com.afauzi.peoemergency.local_storage.NoteDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditNoteBinding

    private val db by lazy { NoteDB(this) }
    private var noteId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpView()
        setUpListener()
    }

    private fun setUpView() {
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        when (intent.getIntExtra("intent_type", 0)) {
            Constant.TYPE_CREATE -> {
                binding.buttonUpdate.visibility = View.GONE
            }
            Constant.TYPE_READ -> {
                binding.buttonSave.visibility = View.GONE
                binding.buttonUpdate.visibility = View.GONE
                getNote()
            }
            Constant.TYPE_UPDATE -> {
                binding.buttonSave.visibility = View.GONE
                getNote()
            }
        }
    }

    private fun setUpListener() {
        binding.buttonSave.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                db.noteDao().addNote(
                    ModelNote(
                        0,
                        binding.editTitle.text.toString(),
                        binding.editNote.text.toString()
                    )
                )
                finish()
            }
        }
        binding.buttonUpdate.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                db.noteDao().updateNote(
                    ModelNote(
                        noteId,
                        binding.editTitle.text.toString(),
                        binding.editNote.text.toString()
                    )
                )
                finish()
            }
        }
    }

    private fun getNote() {
        noteId = intent.getIntExtra("intent_id", 0)
        CoroutineScope(Dispatchers.IO).launch {
            val notes = db.noteDao().getNote(noteId)[0]
            binding.editTitle.setText(notes.title)
            binding.editNote.setText(notes.note)
        }
    }

}