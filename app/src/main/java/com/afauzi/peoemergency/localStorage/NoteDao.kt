package com.afauzi.peoemergency.localStorage

import androidx.room.*

@Dao
interface NoteDao {
    @Insert
    suspend fun addNote(note: ModelNote)

    @Update
    suspend fun updateNote(note: ModelNote)

    @Delete
    suspend fun deleteNote(note: ModelNote)

    @Query("SELECT * FROM note ORDER BY id DESC") 
    suspend fun getNotes(): List<ModelNote>

    @Query("SELECT * FROM note WHERE id=:note_id")
    suspend fun getNote(note_id: Int): List<ModelNote>
}