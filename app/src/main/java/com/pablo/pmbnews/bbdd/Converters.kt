package com.pablo.pmbnews.bbdd

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converters {
    @TypeConverter
    fun fromSource(source: Source?): String {
        return Gson().toJson(source)
    }

    @TypeConverter
    fun toSource(source: String): Source? {
        val type = object : TypeToken<Source?>() {}.type
        return Gson().fromJson<Source?>(source, type)
    }

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }
}
