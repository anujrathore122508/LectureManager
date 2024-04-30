package com.example.lecturemanager.ui.home

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lecturemanager.R
import com.example.lecturemanager.databinding.FragmentHomeBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale



class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it

        }
        _binding!!.addLecture.setOnClickListener { view ->
            val dialog = BottomSheetDialog(requireContext())


            // on below line we are inflating a layout file which we have created.
            val view = layoutInflater.inflate(R.layout.add_lecture_bottomsheet, null)
            // on below line we are creating a variable for our button
            // which we are using to dismiss our dialog.
            val btnOpen1= view.findViewById<Button>(R.id.idBtnDatePicker)
            val DateTextview=view.findViewById<TextView>(R.id.date)
            btnOpen1.setOnClickListener()
            {
             showDatePicker(view,DateTextview)
            }

            val btnClose2= view.findViewById<Button>(R.id.idBtnStartTime)
            var timetextview=view.findViewById<Button>(R.id.StartTime)
            btnClose2.setOnClickListener()
            {
                timepicker(view,timetextview)
            }

            val btnClose3= view.findViewById<Button>(R.id.idBtnEndTime)
            var timetextview1=view.findViewById<Button>(R.id.endtime)
            btnClose3.setOnClickListener()
            {
                timepicker2(view,timetextview1)
            }

            val btnClose = view.findViewById<Button>(R.id.idBtnSubmit)
            // on below line we are adding on click listener
            // for our dismissing the dialog button.
            btnClose.setOnClickListener {
                // on below line we are calling a dismiss
                // method to close our dialog.
               dialog.dismiss()
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



