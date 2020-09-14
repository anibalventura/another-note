package com.anibalventura.anothernote.data.repository

import androidx.lifecycle.LiveData
import com.anibalventura.anothernote.data.db.NoteDao
import com.anibalventura.anothernote.data.models.NoteData

class NoteRepository(private val noteDao: NoteDao) {

    val getAllData: LiveData<List<NoteData>> = noteDao.getAllData()

    suspend fun insertData(noteData: NoteData) {
        noteDao.insertData(noteData)
    }
}