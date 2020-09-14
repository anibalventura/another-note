package com.anibalventura.anothernote.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anibalventura.anothernote.data.models.Priority

@Entity(tableName = "note_table")
data class NoteData(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var title: String,
    var priority: Priority,
    var description: String
)