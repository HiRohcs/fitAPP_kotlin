package com.example.fitapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitapp.databinding.ItemGoalBinding

class GoalsAdapter : RecyclerView.Adapter<GoalsAdapter.GoalViewHolder>() {

    private var goalsList = mutableListOf<GoalModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val binding = ItemGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goalsList[position]
        holder.bind(goal)
    }

    override fun getItemCount(): Int = goalsList.size

    fun submitList(list: List<GoalModel>) {
        goalsList = list.toMutableList()
        notifyDataSetChanged()
    }

    class GoalViewHolder(private val binding: ItemGoalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(goal: GoalModel) {
            binding.goalType.text = goal.type
            binding.goalProgress.max = goal.target.toInt()
            binding.goalProgress.progress = goal.progress.toInt()
            binding.goalProgressText.text = "${goal.progress}/${goal.target}"
        }
    }
}
