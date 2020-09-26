package com.anibalventura.anothernote.data.db.trash

import androidx.lifecycle.LiveData
import androidx.room.*
import com.anibalventura.anothernote.data.models.TrashModel

@Dao
interface TrashDao {

    @Query("SELECT * FROM trash_table ORDER BY id DESC")
    fun getDatabase(): LiveData<List<TrashModel>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItem(trashModel: TrashModel)

    @Delete
    suspend fun deleteItem(trashModel: TrashModel)

    @Query("DELETE FROM trash_table")
    suspend fun deleteDatabase()
}