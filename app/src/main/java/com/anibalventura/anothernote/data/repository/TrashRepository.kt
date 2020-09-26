package com.anibalventura.anothernote.data.repository

import androidx.lifecycle.LiveData
import com.anibalventura.anothernote.data.db.trash.TrashDao
import com.anibalventura.anothernote.data.models.TrashModel

class TrashRepository(private val trashDao: TrashDao) {

    val getDatabase: LiveData<List<TrashModel>> = trashDao.getDatabase()

    suspend fun insertItem(trashModel: TrashModel) {
        trashDao.insertItem(trashModel)
    }

    suspend fun deleteItem(trashModel: TrashModel) {
        trashDao.deleteItem(trashModel)
    }

    suspend fun deleteDatabase() {
        trashDao.deleteDatabase()
    }
}