package com.example.fitapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fitapp.R
import com.example.fitapp.databinding.FragmentWorkoutListBinding
import com.google.firebase.auth.FirebaseAuth


class WorkoutListFragment : Fragment() {

    private var _binding: FragmentWorkoutListBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWorkoutListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()


        setHasOptionsMenu(true)

        binding.logActivityButton.setOnClickListener {
            findNavController().navigate(R.id.action_workoutListFragment_to_logActivityFragment)
        }

        binding.stepTrackerButton.setOnClickListener {
            findNavController().navigate(R.id.action_workoutListFragment_to_stepTrackerFragment)
        }

        binding.setGoalButton.setOnClickListener {
            findNavController().navigate(R.id.action_workoutListFragment_to_setGoalFragment)
        }

        binding.progressButton.setOnClickListener {
            findNavController().navigate(R.id.action_workoutListFragment_to_progressFragment)
        }
        binding.activityHistoryButton.setOnClickListener {
            findNavController().navigate(R.id.action_workoutListFragment_to_activityHistoryFragment)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                auth.signOut()
                findNavController().navigate(R.id.welcomeFragment)
                true
            }
            R.id.action_profile -> {
                findNavController().navigate(R.id.action_workoutListFragment_to_profileFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
