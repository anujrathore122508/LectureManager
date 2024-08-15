package com.example.lecturemanager.ui.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lecturemanager.R
import com.example.lecturemanager.ui.home.datapackage.User
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class LectureAdapter(private val onDeleteClickListener: OnDeleteClickListener) : ListAdapter<User, LectureAdapter.LectureViewHolder>(LectureDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LectureViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lecture, parent, false)
        return LectureViewHolder(view)
    }

    override fun onBindViewHolder(holder: LectureViewHolder, position: Int) {
        val lecture = getItem(position)
        holder.bind(lecture)
    }



    inner class LectureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val lectureNameTextView: TextView = itemView.findViewById(R.id.lectureNameTextview)
        private val startTimeTextView: TextView = itemView.findViewById(R.id.startTimeTextView)
//        private val endTimeTextView: TextView = itemView.findViewById(R.id.endTimeTextView)
        private val dayTextView: TextView = itemView.findViewById(R.id.dayTextView)
        private val threeDotMenu: ImageView = itemView.findViewById(R.id.threeDotMenu)

        init {
            // Set click listener for three-dot menu ImageView
            threeDotMenu.setOnClickListener {
                // Show popup menu
                showPopupMenu()

            }
        }



        fun bind(user: User) {
            lectureNameTextView.text = user.lectureName

            // Convert lectureStartTime and lectureEndTime to the required format
            val startTimeFormatted = formatTime(user.lectureStartTime)
            val endTimeFormatted = formatTime(user.lectureEndTime)

            // Set formatted time to the TextView
            startTimeTextView.text = "$startTimeFormatted - $endTimeFormatted"
            dayTextView.text = "Day: ${user.lectureDay}"
        }
        private fun formatTime(time: String): String {
            return try {
                // Assuming the time input is in "HH:mm" format (e.g., "11:00", "14:00")
                val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val date = inputFormat.parse(time)
                outputFormat.format(date)
            } catch (e: ParseException) {
                // Log error and return a default value or empty string
                Log.e("LectureAdapter", "Error parsing time: $time", e)
                "Invalid Time"
            }
        }

        private fun showPopupMenu() {
            val popupMenu = PopupMenu(itemView.context, threeDotMenu)
            popupMenu.inflate(R.menu.popup_menu_layout)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.action_edit -> {
                        // Handle edit action
                        onDeleteClickListener.onEditClicked(adapterPosition)
                        true
                    }
                    R.id.action_delete -> {
                        // Handle delete action
                        onDeleteClickListener.onDeleteClicked(adapterPosition)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    interface OnDeleteClickListener {
        fun onDeleteClicked(position: Int)
        fun onEditClicked(position: Int) // New method for edit
    }

    class LectureDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}

