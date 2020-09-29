package com.anibalventura.anothernote.data.db.note

import androidx.lifecycle.LiveData
import androidx.room.*
import com.anibalventura.anothernote.data.models.NoteModel

@Dao
interface NoteDao {

    @Query("SELECT * FROM note_table ORDER BY id DESC")
    fun getDatabase(): LiveData<List<NoteModel>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItem(noteModel: NoteModel)

    @Update
    suspend fun updateItem(noteModel: NoteModel)

    @Delete
    suspend fun deleteItem(noteModel: NoteModel)

    @Query("DELETE FROM note_table")
    suspend fun deleteDatabase()

    @Query("SELECT * FROM note_table WHERE title LIKE :searchQuery")
    fun searchDatabase(searchQuery: String): LiveData<List<NoteModel>>

    @Query("SELECT * FROM note_table ORDER BY title ASC")
    fun sortByTitle(): LiveData<List<NoteModel>>

    @Query("SELECT * FROM note_table ORDER BY id DESC")
    fun sortByCreation(): LiveData<List<NoteModel>>

    @Query("SELECT * FROM note_table ORDER BY color ASC")
    fun sortByColor(): LiveData<List<NoteModel>>
}