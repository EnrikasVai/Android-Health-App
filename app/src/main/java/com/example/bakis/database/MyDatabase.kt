package com.example.bakis.database


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.TypeConverter
import java.util.Date

@Database(
    entities = [UserEntity::class, WaterIntakeEntity::class], // Add WaterIntakeEntity to the list of entities
    version = 2// Increment the version number due to schema change
)
@TypeConverters(Converters::class) // If you are using type converters, don't forget to include this annotation
abstract class MyDatabase : RoomDatabase() {
    abstract val dao: MyDao
}

class Converters {
    @TypeConverter
    fun fromDate(value: Date?): Long? = value?.time

    @TypeConverter
    fun toDate(value: Long?): Date? = value?.let { Date(it) }
}
