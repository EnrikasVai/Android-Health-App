package com.example.bakis.database

import androidx.lifecycle.LiveData
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

    @Query("UPDATE UserEntity SET waterGoal = :waterGoal WHERE id = :id")
    suspend fun updateUserWaterGoal(id: Int, waterGoal: Int)

    // Insert a new water intake record
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterIntake(waterIntakeEntity: WaterIntakeEntity)

    // Query all water intake records for a specific user
    @Query("SELECT * FROM water_intake WHERE user_id = :userId")
    fun getAllWaterIntakesForUser(userId: Int): Flow<List<WaterIntakeEntity>>

    // Query water intake records for a specific user on a specific date
    @Query("SELECT * FROM water_intake WHERE user_id = :userId AND date = :date")
    fun getWaterIntakeForUserByDate(userId: Int, date: String): Flow<List<WaterIntakeEntity>>

    // Optionally, add a method to delete water intake records if needed
    @Delete
    suspend fun deleteWaterIntake(waterIntakeEntity: WaterIntakeEntity)

    // Update a specific water intake record, if necessary
    @Update
    suspend fun updateWaterIntake(waterIntakeEntity: WaterIntakeEntity)

    @Query("SELECT SUM(intake_amount) as totalIntake, date FROM water_intake GROUP BY date")
    fun getDailyWaterIntake(): LiveData<List<DailyIntake>>

    // Query to sum monthly water intake, assuming the date format is 'YYYY-MM-DD'
    @Query("SELECT SUM(intake_amount) as totalIntake, SUBSTR(date, 1, 7) as month FROM water_intake GROUP BY month")
    fun getMonthlyWaterIntake(): LiveData<List<MonthlyIntake>>

}
data class DailyIntake(val totalIntake: Int, val date: String)
data class MonthlyIntake(val totalIntake: Int, val month: String)