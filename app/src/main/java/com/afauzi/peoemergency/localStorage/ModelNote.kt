package com.afauzi.peoemergency.localStorage

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note")
data class ModelNote(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val title: String,

    val note: String,
)
