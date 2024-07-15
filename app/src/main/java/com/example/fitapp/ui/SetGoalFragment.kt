package com.example.fitapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fitapp.NotificationHelper
import com.example.fitapp.databinding.FragmentSetGoalBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SetGoalFragment : Fragment() {

    private var _binding: FragmentSetGoalBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("SetGoalFragment", "onCreateView called")
        _binding = FragmentSetGoalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("SetGoalFragment", "onViewCreated called")

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        binding.setGoalButton.setOnClickListener {
            Log.d("SetGoalFragment", "Set Goal button clicked")
            setGoal()
        }
    }

    private fun setGoal() {
        Log.d("SetGoalFragment", "setGoal called")

        val userId = auth.currentUser?.uid ?: run {
            Log.e("SetGoalFragment", "User is not authenticated")
            Toast.makeText(requireContext(), "User is not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val type = binding.goalTypeInput.text.toString()
        val target = binding.goalTargetInput.text.toString().toDoubleOrNull() ?: run {
            Log.e("SetGoalFragment", "Invalid target value")
            Toast.makeText(requireContext(), "Please enter a valid target value", Toast.LENGTH_SHORT).show()
            return
        }

        if (type.isEmpty() || target <= 0) {
            Log.d("SetGoalFragment", "Invalid goal details: type='$type', target=$target")
            Toast.makeText(requireContext(), "Please enter valid goal details", Toast.LENGTH_SHORT).show()
            return
        }

        val goal = GoalModel(type, target, 0.0)
        Log.d("SetGoalFragment", "Goal details: $goal")

        val goalReference = database.child("goals").child(userId).push()
        Log.d("SetGoalFragment", "Goal reference: ${goalReference.key}")

        Log.d("SetGoalFragment", "Attempting to set value in Firebase")
        goalReference.setValue(goal)
            .addOnCompleteListener { task ->
                Log.d("SetGoalFragment", "addOnCompleteListener called")
                if (task.isSuccessful) {
                    Log.d("SetGoalFragment", "Goal set successfully")
                    Toast.makeText(requireContext(), "Goal set", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("SetGoalFragment", "Failed to set goal", task.exception)
                    Toast.makeText(requireContext(), "Failed to set goal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("SetGoalFragment", "Failed to set goal", exception)
                Toast.makeText(requireContext(), "Failed to set goal: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }



    fun checkGoalProgress(goal: GoalModel) {
        val progress = calculateProgress(goal)
        if (progress >= goal.target) {
            NotificationHelper.sendNotification(
                requireContext(),
                "Congratulations!",
                "You've reached your fitness goal: ${goal.type}!"
            )
        }
    }

    private fun calculateProgress(goal: GoalModel): Double {
        // Implement logic to calculate progress towards the goal
        return goal.progress
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
