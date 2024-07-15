package com.example.fitapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitapp.NotificationHelper
import com.example.fitapp.databinding.FragmentLogActivityBinding
import com.example.fitapp.model.ActivityModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LogActivityFragment : Fragment() {

    private var _binding: FragmentLogActivityBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLogActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://fitapp-9e7b1-default-rtdb.europe-west1.firebasedatabase.app").reference

        binding.logActivityButton.setOnClickListener {
            logActivity()
        }
    }

    private fun logActivity() {
        val userId = auth.currentUser?.uid ?: run {
            Log.e("LogActivityFragment", "User is not authenticated")
            Toast.makeText(requireContext(), "User is not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val type = binding.activityTypeInput.text.toString()
        val duration = binding.activityDurationInput.text.toString().toDoubleOrNull() ?: 0.0
        val distance = binding.activityDistanceInput.text.toString().toDoubleOrNull() ?: 0.0
        val caloriesBurned = binding.activityCaloriesInput.text.toString().toDoubleOrNull() ?: 0.0
        val date = System.currentTimeMillis()

        if (type.isEmpty() || duration <= 0 || distance <= 0 || caloriesBurned <= 0) {
            Log.e("LogActivityFragment", "Invalid activity details")
            Toast.makeText(requireContext(), "Please enter valid activity details", Toast.LENGTH_SHORT).show()
            return
        }

        val activity = ActivityModel(type, duration, distance, caloriesBurned, date)
        val activityReference = database.child("activities").child(userId).push()

        activityReference.setValue(activity)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("LogActivityFragment", "Activity logged successfully")
                    Toast.makeText(requireContext(), "Activity logged", Toast.LENGTH_SHORT).show()
                    updateGoalProgress(activity)
                } else {
                    Log.e("LogActivityFragment", "Failed to log activity", task.exception)
                    Toast.makeText(requireContext(), "Failed to log activity: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateGoalProgress(activity: ActivityModel) {
        val userId = auth.currentUser?.uid ?: return

        database.child("goals").child(userId).get().addOnSuccessListener { dataSnapshot ->
            for (goalSnapshot in dataSnapshot.children) {
                val goal = goalSnapshot.getValue(GoalModel::class.java) ?: continue
                when (goal.type) {
                    "Running" -> goal.progress += activity.distance
                    "Cycling" -> goal.progress += activity.distance
                    "Swimming" -> goal.progress += activity.duration
                    // Add other activity types and their corresponding goal updates
                }
                goalSnapshot.ref.setValue(goal)
                checkGoalProgress(goal)
            }
        }.addOnFailureListener { exception ->
            Log.e("LogActivityFragment", "Failed to update goal progress", exception)
        }
    }

    private fun checkGoalProgress(goal: GoalModel) {
        if (goal.progress >= goal.target) {
            NotificationHelper.sendNotification(
                requireContext(),
                "Congratulations!",
                "You've reached your fitness goal: ${goal.type}!"
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
