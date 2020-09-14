package com.anibalventura.anothernote.data.db

import androidx.room.TypeConverter
import com.anibalventura.anothernote.data.models.Priority

class Converter {

    // Convert to string.
    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.name
    }

    // Convert to Priority.
    @TypeConverter
    fun toPriority(priority: String): Priority {
        return Priority.valueOf(priority)
    }
}