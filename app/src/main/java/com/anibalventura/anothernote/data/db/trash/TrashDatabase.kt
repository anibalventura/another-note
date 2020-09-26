package com.anibalventura.anothernote.data.db.trash

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.anibalventura.anothernote.data.models.TrashModel

@Database(entities = [TrashModel::class], version = 1, exportSchema = false)
abstract class TrashDatabase : RoomDatabase() {

    abstract fun trashDao(): TrashDao

    companion object {
        @Volatile
        private var INSTANCE: TrashDatabase? = null

        fun getDatabase(context: Context): TrashDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrashDatabase::class.java,
                    "trash_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}