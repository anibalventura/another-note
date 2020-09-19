package com.anibalventura.anothernote.data.repository

import androidx.lifecycle.LiveData
import com.anibalventura.anothernote.data.db.trash.TrashDao
import com.anibalventura.anothernote.data.models.TrashData

class TrashRepository(private val trashDao: TrashDao) {

    val getAllData: LiveData<List<TrashData>> = trashDao.getAllData()

    suspend fun insertData(trashData: TrashData) {
        trashDao.insertData(trashData)
    }

    suspend fun deleteItem(trashData: TrashData) {
        trashDao.deleteItem(trashData)
    }

    suspend fun deleteAll() {
        trashDao.deleteAll()
    }
}