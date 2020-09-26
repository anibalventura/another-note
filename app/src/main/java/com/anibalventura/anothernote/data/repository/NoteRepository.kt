package com.anibalventura.anothernote.data.repository

import androidx.lifecycle.LiveData
import com.anibalventura.anothernote.data.db.note.NoteDao
import com.anibalventura.anothernote.data.models.NoteModel

class NoteRepository(private val noteDao: NoteDao) {

    val getDatabase: LiveData<List<NoteModel>> = noteDao.getDatabase()

    val sortByTitle: LiveData<List<NoteModel>> = noteDao.sortByTitle()

    suspend fun insertItem(noteModel: NoteModel) {
        noteDao.insertItem(noteModel)
    }

    suspend fun updateItem(noteModel: NoteModel) {
        noteDao.updateItem(noteModel)
    }

    suspend fun deleteItem(noteModel: NoteModel) {
        noteDao.deleteItem(noteModel)
    }

    suspend fun deleteDatabase() {
        noteDao.deleteDatabase()
    }

    fun searchDatabase(searchQuery: String): LiveData<List<NoteModel>> {
        return noteDao.searchDatabase(searchQuery)
    }
}