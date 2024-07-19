package com.example.lecturemanager.ui.ui.ui.login

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.lecturemanager.R


class DaysAdapter(private val daysList: List<String>, private val onDayClickListener: OnDayClickListener) : RecyclerView.Adapter<DaysAdapter.DayViewHolder>() {

    interface OnDayClickListener {
        fun onDayClick(day: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day_button, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = daysList[position]
        holder.bind(day)
    }

    override fun getItemCount(): Int {
        return daysList.size
    }

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayButton: Button = itemView.findViewById(R.id.dayButton)

        fun bind(day: String) {
            dayButton.text = day
            dayButton.setOnClickListener {
                onDayClickListener.onDayClick(day)
            }
        }
    }
}
