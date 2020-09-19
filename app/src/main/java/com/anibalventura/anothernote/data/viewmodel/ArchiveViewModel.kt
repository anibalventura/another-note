package com.anibalventura.anothernote.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.anibalventura.anothernote.data.db.archive.ArchiveDatabase
import com.anibalventura.anothernote.data.models.ArchiveData
import com.anibalventura.anothernote.data.repository.ArchiveRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ArchiveViewModel(application: Application) : AndroidViewModel(application) {

    private val archiveDao = ArchiveDatabase.getDatabase(application).archiveDao()
    private val repository: ArchiveRepository

    val getAllData: LiveData<List<ArchiveData>>

    init {
        repository = ArchiveRepository(archiveDao)
        getAllData = repository.getAllData
    }

    fun insertData(archiveData: ArchiveData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertData(archiveData)
        }
    }

    fun updateData(archiveData: ArchiveData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateData(archiveData)
        }
    }

    fun deleteItem(archiveData: ArchiveData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteItem(archiveData)
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }

    fun searchDatabase(searchQuery: String): LiveData<List<ArchiveData>> {
        return repository.searchDatabase(searchQuery)
    }
}