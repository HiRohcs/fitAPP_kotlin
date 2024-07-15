package com.example.fitapp.repository

import com.example.fitapp.model.Workout

class WorkoutRepository {
    // Simulating data fetching
    fun getWorkouts(): List<Workout> {
        return listOf(
            Workout(1, "Push Ups", 10),
            Workout(2, "Squats", 15),
            Workout(3, "Planks", 5)
        )
    }
}