package com.anibalventura.anothernote.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.anibalventura.anothernote.data.db.archive.ArchiveDatabase
import com.anibalventura.anothernote.data.models.ArchiveModel
import com.anibalventura.anothernote.data.repository.ArchiveRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ArchiveViewModel(application: Application) : AndroidViewModel(application) {

    private val archiveDao = ArchiveDatabase.getDatabase(application).archiveDao()
    private val repository: ArchiveRepository

    val getDatabase: LiveData<List<ArchiveModel>>

    init {
        repository = ArchiveRepository(archiveDao)
        getDatabase = repository.getDatabase
    }

    fun insertItem(archiveModel: ArchiveModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertData(archiveModel)
        }
    }

    fun updateItem(archiveModel: ArchiveModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateItem(archiveModel)
        }
    }

    fun deleteItem(archiveModel: ArchiveModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteItem(archiveModel)
        }
    }

    fun deleteDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteDatabase()
        }
    }

    fun searchDatabase(searchQuery: String): LiveData<List<ArchiveModel>> {
        return repository.searchDatabase(searchQuery)
    }
}