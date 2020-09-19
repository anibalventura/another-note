package com.anibalventura.anothernote.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.anibalventura.anothernote.data.db.note.NoteDatabase
import com.anibalventura.anothernote.data.models.NoteData
import com.anibalventura.anothernote.data.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val noteDao = NoteDatabase.getDatabase(application).noteDao()
    private val repository: NoteRepository

    val getAllData: LiveData<List<NoteData>>

    val sortByTitle: LiveData<List<NoteData>>

    init {
        repository = NoteRepository(noteDao)
        getAllData = repository.getAllData

        sortByTitle = repository.sortByTitle
    }

    fun insertData(noteData: NoteData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertData(noteData)
        }
    }

    fun updateData(noteData: NoteData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateData(noteData)
        }
    }

    fun deleteItem(noteData: NoteData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteItem(noteData)
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }

    fun searchDatabase(searchQuery: String): LiveData<List<NoteData>> {
        return repository.searchDatabase(searchQuery)
    }
}