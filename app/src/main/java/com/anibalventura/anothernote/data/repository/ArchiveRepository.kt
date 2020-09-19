package com.anibalventura.anothernote.data.repository

import androidx.lifecycle.LiveData
import com.anibalventura.anothernote.data.db.archive.ArchiveDao
import com.anibalventura.anothernote.data.models.ArchiveData

class ArchiveRepository(private val archiveDao: ArchiveDao) {

    val getAllData: LiveData<List<ArchiveData>> = archiveDao.getAllData()

    suspend fun insertData(archiveData: ArchiveData) {
        archiveDao.insertData(archiveData)
    }

    suspend fun updateData(archiveData: ArchiveData) {
        archiveDao.updateData(archiveData)
    }

    suspend fun deleteItem(archiveData: ArchiveData) {
        archiveDao.deleteItem(archiveData)
    }

    suspend fun deleteAll() {
        archiveDao.deleteAll()
    }

    fun searchDatabase(searchQuery: String): LiveData<List<ArchiveData>> {
        return archiveDao.searchDatabase(searchQuery)
    }
}