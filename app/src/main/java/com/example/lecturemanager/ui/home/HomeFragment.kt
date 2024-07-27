package com.example.lecturemanager.ui.home


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lecturemanager.R
import com.example.lecturemanager.databinding.FragmentHomeBinding
import com.example.lecturemanager.ui.home.database.DatabaseBuilder
import com.example.lecturemanager.ui.home.datapackage.HomeViewModelFactory
import com.example.lecturemanager.ui.home.datapackage.User
import com.example.lecturemanager.ui.ui.LectureAdapter
import com.example.lecturemanager.ui.ui.ui.login.DaysAdapter
import com.example.lecturemanager.ui.workers.NotificationScheduler
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.ParseException
import java.util.Date

class HomeFragment : Fragment(), LectureAdapter.OnDeleteClickListener, DaysAdapter.OnDayClickListener {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var adapter: LectureAdapter
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(DatabaseBuilder.getInstance(requireContext()).userDao())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        adapter = LectureAdapter(this)
        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter

        setupDayButtonsRecyclerView(root)

        homeViewModel.lectures.observe(viewLifecycleOwner, Observer { lectures ->
            adapter.submitList(lectures)
        })

        _binding!!.addLecture.setOnClickListener {
            showAddLectureBottomSheet()
        }

        return root
    }

    private fun setupDayButtonsRecyclerView(view: View) {
        val daysRecyclerView: RecyclerView = view.findViewById(R.id.daysRecyclerView)
        val daysAdapter = DaysAdapter(getDaysOfWeek(), this)

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        daysRecyclerView.layoutManager = layoutManager
        daysRecyclerView.adapter = daysAdapter
    }

    private fun getDaysOfWeek(): List<String> {
        return listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    }

    override fun onDayClick(day: String) {
        homeViewModel.getLecturesForDay(day).observe(viewLifecycleOwner, Observer { lectures ->
            adapter.submitList(lectures)
        })
    }

    private fun showAddLectureBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.add_lecture_bottomsheet, null)

        val lectureNameEditText = view.findViewById<EditText>(R.id.editText)
        lectureNameEditText.requestFocus()

        val lectureDateTextView = view.findViewById<TextView>(R.id.date)
        val btnDatePicker = view.findViewById<Button>(R.id.idBtnDatePicker)
        btnDatePicker.setOnClickListener {
            showDatePicker(view, lectureDateTextView)
        }

        val daySpinner = view.findViewById<Spinner>(R.id.daySpinner)
        setupDaySpinner(view, daySpinner)

        val lectureStartTimeTextView = view.findViewById<TextView>(R.id.StartTime)
        val btnStartTimePicker = view.findViewById<Button>(R.id.idBtnStartTime)
        btnStartTimePicker.setOnClickListener {
            timePicker(view, lectureStartTimeTextView)
        }

        val lectureEndTimeTextView = view.findViewById<TextView>(R.id.endtime)
        val btnEndTimePicker = view.findViewById<Button>(R.id.idBtnEndTime)
        btnEndTimePicker.setOnClickListener {
            timePicker(view, lectureEndTimeTextView)
        }
        val btnSubmit = view.findViewById<Button>(R.id.idBtnSubmit)
        btnSubmit.setOnClickListener {
            val lecture = lectureNameEditText.text.toString()
            val date = lectureDateTextView.text.toString()
            val day = daySpinner.selectedItem.toString()
            val time = lectureStartTimeTextView.text.toString()
            val endTime = lectureEndTimeTextView.text.toString()

            if (lecture.isNotEmpty() && date.isNotEmpty() && day.isNotEmpty() && time.isNotEmpty() && endTime.isNotEmpty()) {
                val user = User(
                    lectureName = lecture,
                    lectureDate = date,
                    lectureDay = day,
                    lectureStartTime = time,
                    lectureEndTime = endTime
                )

                homeViewModel.insertLecture(user).observe(viewLifecycleOwner, Observer { insertedUserId ->
                    if (insertedUserId != null) {
                        val startTimeMillis = convertToMillis(date, time)
                        NotificationScheduler.scheduleLectureNotification(
                            requireContext(),
                            insertedUserId, // Pass the correct lecture ID here
                            lecture,
                            startTimeMillis
                        )
                        NotificationScheduler.scheduleWeeklyNotification(
                            requireContext(),
                            insertedUserId, // Pass the correct lecture ID here
                            lecture,
                            startTimeMillis
                        )

                        dialog.dismiss()
                    }
                })
            }else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

    override fun onDeleteClicked(position: Int) {
        val userDao = DatabaseBuilder.getInstance(requireContext()).userDao()

        GlobalScope.launch {
            val lectures = adapter.currentList.toMutableList()
            val deletedItem = lectures.removeAt(position)

            withContext(Dispatchers.Main) {
                adapter.submitList(lectures)
            }

            userDao.delete(deletedItem)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
private fun showDatePicker(view: View, textView: TextView) {
        val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        view.context,
        { _, year, monthOfYear, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, monthOfYear, dayOfMonth)
            }
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            textView.text = dateFormat.format(selectedDate.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.show()
}
private fun timePicker(view: View, textView: TextView) {
    val calendar = Calendar.getInstance()

    val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        textView.text = timeFormat.format(calendar.time)
    }

    TimePickerDialog(
        view.context,
        timeSetListener,
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    ).show()
}

private fun setupDaySpinner(view: View, spinner: Spinner) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val adapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, days)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    spinner.adapter = adapter
}

private fun convertToMillis(date: String, time: String): Long {
    val dateTime = "$date $time"
    val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val parsedDate = format.parse(dateTime)
    return parsedDate?.time ?: 0
}

//private fun convertToMillis(date: String, time: String): Long {
//    return try {
//        val dateTime = "$date $time"
//        val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
//        val parsedDate = format.parse(dateTime)
//        parsedDate?.time ?: 0L
//    } catch (e: ParseException) {
//        Log.e("HomeFragment", "Date parsing error: ${e.message}")
//        0L
//    }
//}
