package com.example.lecturemanager.ui.Attendance


import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lecturemanager.databinding.ActivityAttendanceDetailsBinding
import com.example.lecturemanager.ui.ui.ui.login.DetailedAttendanceAdapter

class AttendanceDetailsActivity : AppCompatActivity() {

    private val attendanceViewModel: AttendanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAttendanceDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the lectureId and lectureName passed through the intent
        val lectureId = intent.getLongExtra("LECTURE_ID", -1)
        val lectureName = intent.getStringExtra("LECTURE_NAME") ?: "Lecture Name"

        // Set the lecture name to the TextView using the binding object
        binding.lectureNameTextView.text = lectureName

        if (lectureId != -1L) {
            // Fetch and display data
            attendanceViewModel.getDetailedAttendanceForLecture(lectureId)
            attendanceViewModel.detailedAttendance.observe(this) { detailedList ->
                // Set up RecyclerView and Adapter using the binding object
                binding.detailsRecyclerView.layoutManager = LinearLayoutManager(this)
                val adapter = DetailedAttendanceAdapter(detailedList)
                binding.detailsRecyclerView.adapter = adapter
            }
        }
    }
}
