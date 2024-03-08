package com.example.bakis.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userEntity: UserEntity)

    @Delete
    suspend fun delete(userEntity: UserEntity)

    @Update
    suspend fun update(userEntity: UserEntity)

    @Query("SELECT * FROM UserEntity")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("DELETE FROM UserEntity")
    suspend fun deleteAllUsers()

    @Query("UPDATE UserEntity SET name = :name WHERE id = :id")
    suspend fun updateUserName(id: Int, name: String)

    @Query("UPDATE UserEntity SET age = :age WHERE id = :id")
    suspend fun updateUserAge(id: Int, age: Int)

    @Query("UPDATE UserEntity SET weight = :weight WHERE id = :id")
    suspend fun updateUserWeight(id: Int, weight: Double)

    @Query("UPDATE UserEntity SET height = :height WHERE id = :id")
    suspend fun updateUserHeight(id: Int, height: Int)

    @Query("UPDATE UserEntity SET sex = :sex WHERE id = :id")
    suspend fun updateUserSex(id: Int, sex: Boolean)

}
