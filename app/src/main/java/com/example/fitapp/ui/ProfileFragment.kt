package com.example.fitapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fitapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        binding.saveButton.setOnClickListener {
            Log.d("ProfileFragment", "Save button clicked")
            saveUserProfile()
        }

        loadUserProfile()
    }

    private fun saveUserProfile() {
        val userId = auth.currentUser?.uid ?: return

        val age = binding.ageInput.text.toString().toIntOrNull() ?: 0
        val weight = binding.weightInput.text.toString().toDoubleOrNull() ?: 0.0
        val height = binding.heightInput.text.toString().toDoubleOrNull() ?: 0.0
        val fitnessGoals = binding.fitnessGoalsInput.text.toString()

        val userProfile = UserProfile(age, weight, height, fitnessGoals)
        Log.d("ProfileFragment", "Saving user profile: $userProfile")
        database.child("users").child(userId).setValue(userProfile)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("ProfileFragment", "Profile updated successfully")
                    Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("ProfileFragment", "Failed to update profile", task.exception)
                    Toast.makeText(requireContext(), "Failed to update profile: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid ?: return

        database.child("users").child(userId).get().addOnSuccessListener { dataSnapshot ->
            val userProfile = dataSnapshot.getValue(UserProfile::class.java)
            userProfile?.let {
                Log.d("ProfileFragment", "User profile loaded: $it")
                binding.ageInput.setText(it.age.toString())
                binding.weightInput.setText(it.weight.toString())
                binding.heightInput.setText(it.height.toString())
                binding.fitnessGoalsInput.setText(it.fitnessGoals)
            }
        }.addOnFailureListener {
            Log.e("ProfileFragment", "Failed to load profile", it)
            Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
