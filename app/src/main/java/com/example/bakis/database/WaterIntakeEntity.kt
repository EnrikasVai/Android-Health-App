package com.example.bakis.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "water_intake")
data class WaterIntakeEntity(
    @PrimaryKey(autoGenerate = true)
    @SerialName("id")
    val id: Int = 0,

    @ColumnInfo(name = "user_id") // This line explicitly tells Room the column name
    @SerialName("user_id")
    val userId: Int,

    @ColumnInfo(name = "intake_amount")
    @SerialName("intake_amount")
    val intakeAmount: Int, // Milliliters of water consumed

    @ColumnInfo(name = "date")
    @SerialName("date")
    val date: String // Consider using a more specific type for production
)
