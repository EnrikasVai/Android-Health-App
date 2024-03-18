package com.example.bakis.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = tableName)

data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @SerialName("id")
    val id: Int = 0,

    @SerialName("name")
    val name: String = "",

    @SerialName("age")
    val age: Int = 18,

    @SerialName("weight")
    val weight: Double = 60.0,

    @SerialName("height")
    val height: Int = 160,

    @SerialName("sex")
    val sex: Boolean = true,

    @SerialName("waterGoal")
    val waterGoal: Int = 2000,
    )

const val tableName = "UserEntity"