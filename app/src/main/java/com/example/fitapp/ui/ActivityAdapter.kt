package com.example.fitapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitapp.R
import com.example.fitapp.model.ActivityModel

class ActivityAdapter(private var activityList: List<ActivityModel>) : RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    inner class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val typeTextView: TextView = itemView.findViewById(R.id.activity_type)
        val durationTextView: TextView = itemView.findViewById(R.id.activity_duration)
        val distanceTextView: TextView = itemView.findViewById(R.id.activity_distance)
        val caloriesTextView: TextView = itemView.findViewById(R.id.activity_calories)
        val dateTextView: TextView = itemView.findViewById(R.id.activity_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_activity, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = activityList[position]
        holder.typeTextView.text = activity.type
        holder.durationTextView.text = activity.duration.toString()
        holder.distanceTextView.text = activity.distance.toString()
        holder.caloriesTextView.text = activity.caloriesBurned.toString()
        holder.dateTextView.text = activity.date.toString()
    }

    override fun getItemCount() = activityList.size

    fun updateList(newList: List<ActivityModel>) {
        activityList = newList
        notifyDataSetChanged()
    }
}
