package com.example.lecturemanager.ui.ui.ui.login

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lecturemanager.databinding.ListItemAttendanceBinding

import com.example.lecturemanager.ui.home.datapackage.AttendanceSummary

class AttendanceLectureAdapter :
    ListAdapter<AttendanceSummary, AttendanceLectureAdapter.AttendanceViewHolder>(AttendanceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val binding = ListItemAttendanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AttendanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class AttendanceViewHolder(private val binding: ListItemAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AttendanceSummary) {
            binding.LectureName.text = item.lectureName
            binding.TotalLectures.text = "Total Lectures: ${item.totalLectures}"
            binding.TotalPresent.text = "Total Present: ${item.totalPresent}"
            binding.TotalAbsent.text = "Total Absent: ${item.totalAbsent}"
            binding.AttendancePercentage.text = "Attendance: ${item.attendancePercentage}%"
        }
    }

    class AttendanceDiffCallback : DiffUtil.ItemCallback<AttendanceSummary>() {
        override fun areItemsTheSame(oldItem: AttendanceSummary, newItem: AttendanceSummary): Boolean {
            return oldItem.lectureName == newItem.lectureName
        }

        override fun areContentsTheSame(oldItem: AttendanceSummary, newItem: AttendanceSummary): Boolean {
            return oldItem == newItem
        }
    }
}
