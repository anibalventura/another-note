package com.anibalventura.anothernote.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.anibalventura.anothernote.data.models.NoteData

@Dao
interface NoteDao {

    @Query("SELECT * FROM note_table ORDER BY id DESC")
    fun getAllData(): LiveData<List<NoteData>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertData(noteData: NoteData)

    @Update
    suspend fun updateData(noteData: NoteData)

    @Delete
    suspend fun deleteItem(noteData: NoteData)

    @Query("DELETE FROM note_table")
    suspend fun deleteAll()
}