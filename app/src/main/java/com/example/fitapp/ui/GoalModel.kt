package com.example.fitapp.ui

data class GoalModel(
    var type: String = "",
    var target: Double = 0.0, // target value (e.g., steps, distance)
    var progress: Double = 0.0 // current progress
)
