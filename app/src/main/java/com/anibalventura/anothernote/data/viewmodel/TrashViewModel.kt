package com.anibalventura.anothernote.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.anibalventura.anothernote.data.db.trash.TrashDatabase
import com.anibalventura.anothernote.data.models.TrashModel
import com.anibalventura.anothernote.data.repository.TrashRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrashViewModel(application: Application) : AndroidViewModel(application) {

    private val trashDao = TrashDatabase.getDatabase(application).trashDao()
    private val repository: TrashRepository

    val getDatabase: LiveData<List<TrashModel>>

    init {
        repository = TrashRepository(trashDao)
        getDatabase = repository.getDatabase
    }

    fun insertItem(trashModel: TrashModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertItem(trashModel)
        }
    }

    fun deleteItem(noteModel: TrashModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteItem(noteModel)
        }
    }

    fun deleteDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteDatabase()
        }
    }
}