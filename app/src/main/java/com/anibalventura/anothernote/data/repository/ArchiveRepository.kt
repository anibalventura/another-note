package com.anibalventura.anothernote.data.repository

import androidx.lifecycle.LiveData
import com.anibalventura.anothernote.data.db.archive.ArchiveDao
import com.anibalventura.anothernote.data.models.ArchiveModel

class ArchiveRepository(private val archiveDao: ArchiveDao) {

    val getDatabase: LiveData<List<ArchiveModel>> = archiveDao.getDatabase()

    suspend fun insertData(archiveModel: ArchiveModel) {
        archiveDao.insertItem(archiveModel)
    }

    suspend fun updateItem(archiveModel: ArchiveModel) {
        archiveDao.updateItem(archiveModel)
    }

    suspend fun deleteItem(archiveModel: ArchiveModel) {
        archiveDao.deleteItem(archiveModel)
    }

    suspend fun deleteDatabase() {
        archiveDao.deleteDatabase()
    }

    fun searchDatabase(searchQuery: String): LiveData<List<ArchiveModel>> {
        return archiveDao.searchDatabase(searchQuery)
    }
}