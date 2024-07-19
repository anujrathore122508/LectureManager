package com.example.lecturemanager.ui.ui

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
        private val endTimeTextView: TextView = itemView.findViewById(R.id.endTimeTextView)
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
            startTimeTextView.text = user.lectureStartTime
            endTimeTextView.text = user.lectureEndTime
            dayTextView.text = user.lectureDay
        }

        private fun showPopupMenu() {
            val popupMenu = PopupMenu(itemView.context, threeDotMenu)
            popupMenu.inflate(R.menu.popup_menu_layout)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.action_edit -> {
                        // Handle edit action
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
