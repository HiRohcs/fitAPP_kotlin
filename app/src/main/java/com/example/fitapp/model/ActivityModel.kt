package com.example.fitapp.model

data class ActivityModel(
    val type: String = "",
    val duration: Double = 0.0, // in minutes
    val distance: Double = 0.0, // in kilometers
    val caloriesBurned: Double = 0.0,
    val date: Long = 0L // timestamp
)
