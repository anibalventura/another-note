package com.anibalventura.anothernote.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.anibalventura.anothernote.data.db.trash.TrashDatabase
import com.anibalventura.anothernote.data.models.TrashData
import com.anibalventura.anothernote.data.repository.TrashRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrashViewModel(application: Application) : AndroidViewModel(application) {

    private val trashDao = TrashDatabase.getDatabase(application).trashDao()
    private val repository: TrashRepository

    val getAllData: LiveData<List<TrashData>>

    init {
        repository = TrashRepository(trashDao)
        getAllData = repository.getAllData
    }

    fun insertData(trashData: TrashData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertData(trashData)
        }
    }

    fun deleteItem(noteData: TrashData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteItem(noteData)
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }
}