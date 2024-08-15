package com.example.lecturemanager.ui.ui.ui.login

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lecturemanager.databinding.ListItemAttendanceBinding

import com.example.lecturemanager.ui.home.datapackage.AttendanceSummary


class AttendanceLectureAdapter(private val onViewDetailsClick: (AttendanceSummary) -> Unit) : RecyclerView.Adapter<AttendanceLectureAdapter.ViewHolder>() {

    private var attendanceSummaryList: List<AttendanceSummary> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemAttendanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = attendanceSummaryList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = attendanceSummaryList.size

    fun submitList(list: List<AttendanceSummary>) {
        attendanceSummaryList = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ListItemAttendanceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AttendanceSummary) {
            binding.LectureName.text = item.lectureName
            binding.AttendancePercentage.text = "${item.attendancePercentage}%"
            binding.TotalPresent.text = "Present: ${item.totalPresent}"
            binding.TotalAbsent.text = "Absent: ${item.totalAbsent}"
            binding.TotalLectures.text = "Total Lectures: ${item.totalLectures}"

            // Set the progress for the circular progress bar
            binding.circularProgressBar.setProgress(item.attendancePercentage)

            // Ensure the color is updated
            binding.circularProgressBar.updateColor()

            binding.viewDetailsButton.setOnClickListener {
                onViewDetailsClick(item)
            }
        }
    }
}

