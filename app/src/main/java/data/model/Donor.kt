package com.ananyasacademics.bloodconnect.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donors")
data class Donor(
    @PrimaryKey(autoGenerate = true)
    val donorId: Int = 0,

    val name: String,
    val bloodGroup: String,
    val phone: String,
    val area: String,

    val availabilityStatus: String = "Available",

    val notes: String = "",

    val updatedTimestamp: Long = System.currentTimeMillis(),

    val outreachStatus: String = "Not Contacted",

    val lastContactTimestamp: Long = 0L
)