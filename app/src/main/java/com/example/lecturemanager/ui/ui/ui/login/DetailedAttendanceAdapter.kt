package com.example.lecturemanager.ui.ui.ui.login


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lecturemanager.R
import com.example.lecturemanager.ui.home.datapackage.DetailedAttendance
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class DetailedAttendanceAdapter(private var attendanceList: List<DetailedAttendance>) :
    RecyclerView.Adapter<DetailedAttendanceAdapter.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_detailed_attendance, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = attendanceList[position]
        holder.dateTextView.text = dateFormat.format(Date(item.date))
        holder.statusTextView.text = item.status.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }

        // Set color based on the status
        val statusText = item.status.lowercase(Locale.getDefault())
        holder.statusTextView.setTextColor(
            when (statusText) {
                "present" -> holder.itemView.context.getColor(R.color.green) // Adjust color resource
                "absent" -> holder.itemView.context.getColor(R.color.red) // Adjust color resource
                else -> holder.itemView.context.getColor(R.color.default_color) // Default color if needed
            }
        )
    }


    override fun getItemCount(): Int {
        return attendanceList.size
    }

    fun updateData(newAttendanceList: List<DetailedAttendance>) {
        attendanceList = newAttendanceList
        notifyDataSetChanged()
    }
}
