package com.anibalventura.anothernote.data.db.archive

import androidx.lifecycle.LiveData
import androidx.room.*
import com.anibalventura.anothernote.data.models.ArchiveModel

@Dao
interface ArchiveDao {

    @Query("SELECT * FROM archive_table ORDER BY id DESC")
    fun getDatabase(): LiveData<List<ArchiveModel>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItem(archiveModel: ArchiveModel)

    @Update
    suspend fun updateItem(archiveModel: ArchiveModel)

    @Delete
    suspend fun deleteItem(archiveModel: ArchiveModel)

    @Query("DELETE FROM archive_table")
    suspend fun deleteDatabase()

    @Query("SELECT * FROM archive_table WHERE title LIKE :searchQuery")
    fun searchDatabase(searchQuery: String): LiveData<List<ArchiveModel>>
}