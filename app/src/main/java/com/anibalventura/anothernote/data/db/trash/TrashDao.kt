package com.anibalventura.anothernote.data.db.trash

import androidx.lifecycle.LiveData
import androidx.room.*
import com.anibalventura.anothernote.data.models.TrashData

@Dao
interface TrashDao {

    @Query("SELECT * FROM trash_table ORDER BY id DESC")
    fun getAllData(): LiveData<List<TrashData>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertData(trashData: TrashData)

    @Delete
    suspend fun deleteItem(trashData: TrashData)

    @Query("DELETE FROM trash_table")
    suspend fun deleteAll()
}