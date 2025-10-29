package com.farouktouil.farouktouil.core.data.local.converters

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    // Add more type converters as needed for your app
    // For example:
    // @TypeConverter
    // fun fromStringList(value: String?): List<String>? {
    //     return value?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
    // }
    //
    // @TypeConverter
    // fun toStringList(list: List<String>?): String? {
    //     return list?.joinToString(",")
    // }
}
