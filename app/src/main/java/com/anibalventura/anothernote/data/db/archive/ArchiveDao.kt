package com.anibalventura.anothernote.data.db.archive

import androidx.lifecycle.LiveData
import androidx.room.*
import com.anibalventura.anothernote.data.models.ArchiveData

@Dao
interface ArchiveDao {

    @Query("SELECT * FROM archive_table ORDER BY id DESC")
    fun getAllData(): LiveData<List<ArchiveData>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertData(archiveData: ArchiveData)

    @Update
    suspend fun updateData(archiveData: ArchiveData)

    @Delete
    suspend fun deleteItem(archiveData: ArchiveData)

    @Query("DELETE FROM archive_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM archive_table WHERE title LIKE :searchQuery")
    fun searchDatabase(searchQuery: String): LiveData<List<ArchiveData>>
}