// WorkoutViewModel.kt
package com.example.fitapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitapp.model.Workout
import com.example.fitapp.repository.WorkoutRepository

class WorkoutViewModel : ViewModel() {
    private val repository = WorkoutRepository()
    private val _workouts = MutableLiveData<List<Workout>>()
    val workouts: LiveData<List<Workout>> get() = _workouts

    init {
        loadWorkouts()
    }

    private fun loadWorkouts() {
        _workouts.value = repository.getWorkouts()
    }
}
