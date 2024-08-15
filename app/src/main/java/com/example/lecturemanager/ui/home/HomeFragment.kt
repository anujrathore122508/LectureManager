package com.example.lecturemanager.ui.home


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
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


        val sharedPreferences = requireContext().getSharedPreferences("LectureManagerPrefs", Context.MODE_PRIVATE)
        val firstName = sharedPreferences.getString("FIRST_NAME", "User")

        binding.welcomeTextView.text = "Hello , $firstName!".capitalize()


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
        val view = layoutInflater.inflate(R.layout.add_lecture_bottomsheet,null)

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
        dialog.setOnCancelListener {
            dialog.dismiss()
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

        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun showEditLectureBottomSheet(lecture: User) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.add_lecture_bottomsheet, null)

        // Initialize views
        val lectureNameEditText = view.findViewById<EditText>(R.id.editText)
        val lectureDateTextView = view.findViewById<TextView>(R.id.date)
        val btnDatePicker = view.findViewById<Button>(R.id.idBtnDatePicker)
        val daySpinner = view.findViewById<Spinner>(R.id.daySpinner)
        val lectureStartTimeTextView = view.findViewById<TextView>(R.id.StartTime)
        val btnStartTimePicker = view.findViewById<Button>(R.id.idBtnStartTime)
        val lectureEndTimeTextView = view.findViewById<TextView>(R.id.endtime)
        val btnEndTimePicker = view.findViewById<Button>(R.id.idBtnEndTime)
        val btnSubmit = view.findViewById<Button>(R.id.idBtnSubmit)

        // Set existing lecture details
        lectureNameEditText.setText(lecture.lectureName)
        lectureDateTextView.text = lecture.lectureDate
        daySpinner.setSelection(getDaysOfWeek().indexOf(lecture.lectureDay))
        lectureStartTimeTextView.text = lecture.lectureStartTime
        lectureEndTimeTextView.text = lecture.lectureEndTime

        // Setup listeners
        btnDatePicker.setOnClickListener { showDatePicker(view, lectureDateTextView) }
        btnStartTimePicker.setOnClickListener { timePicker(view, lectureStartTimeTextView) }
        btnEndTimePicker.setOnClickListener { timePicker(view, lectureEndTimeTextView) }

        btnSubmit.setOnClickListener {
            val updatedLecture = lecture.copy(
                lectureName = lectureNameEditText.text.toString(),
                lectureDate = lectureDateTextView.text.toString(),
                lectureDay = daySpinner.selectedItem.toString(),
                lectureStartTime = lectureStartTimeTextView.text.toString(),
                lectureEndTime = lectureEndTimeTextView.text.toString()
            )
            homeViewModel.updateLecture(updatedLecture).observe(viewLifecycleOwner, Observer { success ->
                if (success) {
                    Toast.makeText(context, "Lecture updated successfully", Toast.LENGTH_SHORT).show()
                    // Refresh your lecture list or perform other actions
                } else {
                    Toast.makeText(context, "Failed to update lecture", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            })
        }

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

    override fun onEditClicked(position: Int) {
        val lecture = adapter.currentList[position]
        showEditLectureBottomSheet(lecture)
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

private fun convertToMillis(date: String?, time: String?): Long {
    if (date.isNullOrEmpty() || time.isNullOrEmpty()) {
        Log.e("HomeFragment", "Date or time is null or empty. Date: $date, Time: $time")
        return 0
    }

    val dateTime = "$date $time"
    val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return try {
        val parsedDate = format.parse(dateTime)
        parsedDate?.time ?: 0
    } catch (e: ParseException) {
        Log.e("HomeFragment", "Date parse exception: ${e.message} for dateTime: $dateTime")
        0
    }
}


