package com.example.lecturemanager.ui.Attendance

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lecturemanager.R
import com.example.lecturemanager.databinding.FragmentAttendanceBinding
import com.example.lecturemanager.ui.ui.ui.login.AttendanceLectureAdapter

//
//class AttendanceFragment : Fragment() {
//
//    private var _binding: FragmentAttendanceBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var adapter: AttendanceLectureAdapter
//    private val attendanceViewModel: AttendanceViewModel by viewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?,
//    ): View {
//        _binding = FragmentAttendanceBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//        // Initialize RecyclerView adapter
//        adapter = AttendanceLectureAdapter()
//
//        // Find RecyclerView
//        val recyclerView: RecyclerView = root.findViewById(R.id.attendanceRecyclerView)
//
//        // Set up LayoutManager
//        val layoutManager = LinearLayoutManager(activity)
//        recyclerView.layoutManager = layoutManager
//
//        // Set up Adapter
//        recyclerView.adapter = adapter
//
//        // Observe attendance summary data
//        attendanceViewModel.attendanceSummary.observe(viewLifecycleOwner) { summary ->
//            Log.d("AttendanceFragment", "Attendance summary updated: $summary")
//            adapter.submitList(summary)
//        }
//
//        // Register broadcast receiver
//        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
//            attendanceUpdateReceiver, IntentFilter("ACTION_UPDATE_ATTENDANCE")
//        )
//
//        return root
//    }
//
//    private val attendanceUpdateReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            // Refresh the attendance summary when the broadcast is received
//            attendanceViewModel.refreshAttendanceSummary()
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        // Unregister the broadcast receiver to avoid memory leaks
//        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(attendanceUpdateReceiver)
//        _binding = null
//    }
//}
//package com.example.lecturemanager.ui.Attendance
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.localbroadcastmanager.content.LocalBroadcastManager
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.lecturemanager.R
//import com.example.lecturemanager.databinding.FragmentAttendanceBinding
//import com.example.lecturemanager.ui.ui.ui.login.AttendanceLectureAdapter

class AttendanceFragment : Fragment() {

    private var _binding: FragmentAttendanceBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AttendanceLectureAdapter
    private val attendanceViewModel: AttendanceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAttendanceBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize RecyclerView adapter
        adapter = AttendanceLectureAdapter()

        // Find RecyclerView
        val recyclerView: RecyclerView = root.findViewById(R.id.attendanceRecyclerView)

        // Set up LayoutManager
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager

        // Set up Adapter
        recyclerView.adapter = adapter

        // Observe attendance summary data
        attendanceViewModel.attendanceSummary.observe(viewLifecycleOwner) { summary ->
            Log.d("AttendanceFragment", "Attendance summary updated: $summary")
            adapter.submitList(summary)
        }

        // Register broadcast receiver
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            attendanceUpdateReceiver, IntentFilter("ACTION_UPDATE_ATTENDANCE")
        )

        return root
    }

    override fun onResume() {
        super.onResume()
        // Refresh the attendance summary when the fragment resumes
        attendanceViewModel.refreshAttendanceSummary()
    }

    private val attendanceUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Refresh the attendance summary when the broadcast is received
            attendanceViewModel.refreshAttendanceSummary()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Unregister the broadcast receiver to avoid memory leaks
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(attendanceUpdateReceiver)
        _binding = null
    }
}
