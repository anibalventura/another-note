package com.anibalventura.anothernote.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.anibalventura.anothernote.data.db.note.NoteDatabase
import com.anibalventura.anothernote.data.models.NoteModel
import com.anibalventura.anothernote.data.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val noteDao = NoteDatabase.getDatabase(application).noteDao()
    private val repository: NoteRepository

    val getDatabase: LiveData<List<NoteModel>>
    val sortByTitle: LiveData<List<NoteModel>>
    val sortByCreation: LiveData<List<NoteModel>>
    val sortByColor: LiveData<List<NoteModel>>

    init {
        repository = NoteRepository(noteDao)
        getDatabase = repository.getDatabase

        sortByTitle = repository.sortByTitle
        sortByCreation = repository.sortByCreation
        sortByColor = repository.sortByColor
    }

    fun insertItem(noteModel: NoteModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertItem(noteModel)
        }
    }

    fun updateItem(noteModel: NoteModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateItem(noteModel)
        }
    }

    fun deleteItem(noteModel: NoteModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteItem(noteModel)
        }
    }

    fun deleteDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteDatabase()
        }
    }

    fun searchDatabase(searchQuery: String): LiveData<List<NoteModel>> {
        return repository.searchDatabase(searchQuery)
    }
}