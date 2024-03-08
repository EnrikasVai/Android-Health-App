package com.example.bakis.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UserEntity::class],
    version = 1
)
abstract class MyDatabase : RoomDatabase(){
    abstract val dao: MyDao
}