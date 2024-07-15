package com.example.fitapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitapp.R
import com.example.fitapp.databinding.FragmentActivityHistoryBinding
import com.example.fitapp.model.ActivityModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class ActivityHistoryFragment : Fragment() {

    private var _binding: FragmentActivityHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var activityAdapter: ActivityAdapter
    private val activityList = mutableListOf<ActivityModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActivityHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://fitapp-9e7b1-default-rtdb.europe-west1.firebasedatabase.app").reference
        activityAdapter = ActivityAdapter(activityList)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = activityAdapter
        }

        val activityTypes = resources.getStringArray(R.array.activity_types)
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, activityTypes)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.activityTypeSpinner.adapter = spinnerAdapter

        binding.filterButton.setOnClickListener {
            val selectedType = binding.activityTypeSpinner.selectedItem.toString()
            val selectedDate = binding.dateEditText.text.toString()
            filterActivities(selectedType, selectedDate)
        }

        loadActivities()
    }

    private fun loadActivities() {
        val userId = auth.currentUser?.uid ?: return

        database.child("activities").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                activityList.clear()
                for (activitySnapshot in snapshot.children) {
                    val activity = activitySnapshot.getValue(ActivityModel::class.java)
                    activity?.let { activityList.add(it) }
                }
                activityAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ActivityHistoryFragment", "Failed to load activities", error.toException())
            }
        })
    }

    private fun filterActivities(type: String, date: String) {
        Log.d("ActivityHistoryFragment", "filterActivities called with type: $type and date: $date")

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateLong = try {
            if (date.isNotEmpty()) sdf.parse(date)?.time else null
        } catch (e: Exception) {
            Log.e("ActivityHistoryFragment", "Error parsing date", e)
            Toast.makeText(requireContext(), "Invalid date format", Toast.LENGTH_SHORT).show()
            return
        }

        val filteredList = activityList.filter {
            (type == "All" || it.type == type) && (dateLong == null || sdf.format(Date(it.date)) == date)
        }
        Log.d("ActivityHistoryFragment", "Filtered list size: ${filteredList.size}")
        activityAdapter.updateList(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
