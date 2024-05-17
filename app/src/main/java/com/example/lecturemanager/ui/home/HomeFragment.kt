package com.example.lecturemanager.ui.home

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.compose.material3.Text
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import androidx.room.Room
import com.example.lecturemanager.R
import com.example.lecturemanager.databinding.FragmentHomeBinding
import com.example.lecturemanager.ui.home.database.DatabaseBuilder
import com.example.lecturemanager.ui.ui.LectureAdapter
import com.example.lecturemanager.ui.home.database.AppDatabase
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.grpc.Context
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.lecturemanager.ui.home.datapackage.User
//import com.example.lecturemanager.ui.ui.retrieveLecturesFromDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeFragment : Fragment(),LectureAdapter.OnDeleteClickListener  {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var adapter: LectureAdapter // Declare adapter as a property of the fragment

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize RecyclerView adapter
        adapter = LectureAdapter(this)

        // Find RecyclerView
        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerView)

        // Set up LayoutManager
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager

        // Set up Adapter
        recyclerView.adapter = adapter

        // Fetch lectures from the database and update RecyclerView
        fetchLecturesFromDatabase()

        // Click listener for adding a new lecture
        _binding!!.addLecture.setOnClickListener {
            // Show bottom sheet dialog to add a new lecture
            showAddLectureBottomSheet()
        }

        _binding!!.addLecture.setOnClickListener { view ->
            val dialog = BottomSheetDialog(requireContext())

            // on below line we are inflating a layout file which we have created.
            val view = layoutInflater.inflate(R.layout.add_lecture_bottomsheet, null)
            // on below line we are creating a variable for our button
            // which we are using to dismiss our dialog.

            val lectureNameeditText = view.findViewById<EditText>(R.id.editText)
            lectureNameeditText.requestFocus()


            val btnOpen1 = view.findViewById<Button>(R.id.idBtnDatePicker)
            val lectureDateTextview = view.findViewById<TextView>(R.id.date)
            btnOpen1.setOnClickListener()
            {
                showDatePicker(view, lectureDateTextview)
            }


            val daySpinner = view.findViewById<Spinner>(R.id.daySpinner)
            setupDaySpinner(view, daySpinner)


            val btnClose2 = view.findViewById<Button>(R.id.idBtnStartTime)
            var lecturestarttimetextview = view.findViewById<TextView>(R.id.StartTime)
            btnClose2.setOnClickListener()
            {
                timepicker(view, lecturestarttimetextview)
            }

            val btnClose3 = view.findViewById<Button>(R.id.idBtnEndTime)
            var lectureEndtimetextview1 = view.findViewById<TextView>(R.id.endtime)
            btnClose3.setOnClickListener()
            {
                timepicker2(view, lectureEndtimetextview1)
            }

            val btnClose = view.findViewById<Button>(R.id.idBtnSubmit)
            // on below line we are adding on click listener
            // for our dismissing the dialog button.
            btnClose.setOnClickListener {

                val userDao = DatabaseBuilder.getInstance(requireContext()).userDao()

                GlobalScope.launch {
                    // Insert a user

                    val lecture: String = lectureNameeditText.text.toString()
                    val date: String = lectureDateTextview.text.toString()
                    val day: String = daySpinner.selectedItem.toString()
                    val time: String = lecturestarttimetextview.text.toString()
                    val endTime: String = lectureEndtimetextview1.text.toString()



                    val user = com.example.lecturemanager.ui.home.datapackage.User(
                        lectureName = lecture,
                        lectureDate = date,
                        lectureDay = day,
                        lectureStartTime = time,
                        lectureEndTime = endTime

                    )
                    userDao.insert(user)
                    // Retrieve all users
                    val users = userDao.getAllUsers()
                    for (user in users) {
                        println("User: ${user.id}, ${user.lectureName},${user.lectureDate},${user.lectureDay},${user.lectureStartTime},${user.lectureEndTime}")
                    }

                    fetchLecturesFromDatabase()
                    dialog.dismiss()
                }
            }
            // below line is use to set cancelable to avoid
            // closing of dialog box when clicking on the screen.
            dialog.setCancelable(false)

            // on below line we are setting
            // content view to our view.
            dialog.setContentView(view)

            // on below line we are calling
            // a show method to display a dialog.
            dialog.show()



        }
        return root
    }

    // Function to fetch lectures from the database
    private fun fetchLecturesFromDatabase() {
        val userDao = DatabaseBuilder.getInstance(requireContext()).userDao()

        GlobalScope.launch {
            // Retrieve all lectures from the database
            val lectures = userDao.getAllUsers()

            // Update the dataset on the main thread
            withContext(Dispatchers.Main) {
                // Submit the fetched lectures to the adapter
                adapter.submitList(lectures)
            }
        }
    }


    // Function to show bottom sheet dialog for adding a new lecture
    private fun showAddLectureBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.add_lecture_bottomsheet, null)
        // Set up the bottom sheet dialog's UI and listeners here...

        // Display the bottom sheet dialog
        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }
    override fun onDeleteClicked(position: Int) {
        // Handle item deletion here
        // You can get the deleted item position and perform actions like removing it from the database
        // After deletion, you may need to update the RecyclerView data by fetching data again from the database
        val userDao = DatabaseBuilder.getInstance(requireContext()).userDao()

        GlobalScope.launch {
            // Get the list of lectures from the adapter
            val lectures = adapter.currentList.toMutableList()

            // Remove the item at the given position
            val deletedItem = lectures.removeAt(position)

            // Update the dataset on the main thread
            withContext(Dispatchers.Main) {
                // Submit the updated list to the adapter
                adapter.submitList(lectures)
            }

            // Perform delete operation in the database
            userDao.delete(deletedItem)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}




private fun showDatePicker(view: View,view1:TextView) {
    // Create a DatePickerDialog

    val calendar = Calendar.getInstance()
    view.let {

        val datePickerDialog = DatePickerDialog(
            view.context, { DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                // Create a new Calendar instance to hold the selected date
                val selectedDate = Calendar.getInstance()
                // Set the selected date using the values received from the DatePicker dialog
                selectedDate.set(year, monthOfYear, dayOfMonth)
                // Create a SimpleDateFormat to format the date as "dd/MM/yyyy"
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                // Format the selected date into a string
                val formattedDate = dateFormat.format(selectedDate.time)
                // Update the TextView to display the selected date with the "Selected Date: " prefix
                view1.text = "Date: $formattedDate"
                          },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        // Show the DatePicker dialog
        datePickerDialog.show()
    }
}

//private fun showDaySelectionDialog(view: View) {
//    // Get a reference to the Spinner
//    val daySpinner = view.findViewById<Spinner>(R.id.daySpinner)
//
//    // Create an ArrayAdapter using the string array and a default spinner layout
//    ArrayAdapter.createFromResource(
//        view.context,
//        R.array.days,
//        android.R.layout.simple_spinner_item
//    ).also { adapter ->
//        // Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        // Apply the adapter to the spinner
//        daySpinner.adapter = adapter
//    }
//}
private fun setupDaySpinner(view: View, daySpinner: Spinner) {
    ArrayAdapter.createFromResource(
        view.context,
        R.array.days,
        android.R.layout.simple_spinner_item
    ).also { adapter ->
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        daySpinner.adapter = adapter
    }
}

   private fun timepicker(view: View,view2:TextView) {


       view.let {


           val cal = Calendar.getInstance()
           val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
               cal.set(Calendar.HOUR_OF_DAY, hour)
               cal.set(Calendar.MINUTE, minute)
               val formattedtime=SimpleDateFormat("HH:mm").format(cal.time)
               view2.text = "Start Time: $formattedtime"
           }
           val timePickerDialog= TimePickerDialog(view.context ,timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true)
           timePickerDialog.show()
       }
   }

private fun timepicker2(view: View,view3:TextView) {

    view.let {


        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            val formattedtime3= SimpleDateFormat("HH:mm").format(cal.time)
            view3.text = "End Time: $formattedtime3"
        }
        val timePickerDialog= TimePickerDialog(view.context ,timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true)
        timePickerDialog.show()
    }
}