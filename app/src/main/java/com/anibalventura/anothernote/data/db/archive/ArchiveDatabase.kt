package com.anibalventura.anothernote.data.db.archive

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.anibalventura.anothernote.data.models.ArchiveData

@Database(entities = [ArchiveData::class], version = 1, exportSchema = false)
abstract class ArchiveDatabase : RoomDatabase() {

    abstract fun archiveDao(): ArchiveDao

    companion object {

        @Volatile
        private var INSTANCE: ArchiveDatabase? = null

        fun getDatabase(context: Context): ArchiveDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArchiveDatabase::class.java,
                    "archive_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}