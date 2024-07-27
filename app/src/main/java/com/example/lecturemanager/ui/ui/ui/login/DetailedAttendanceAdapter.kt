package com.example.lecturemanager.ui.ui.ui.login



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lecturemanager.R
import com.example.lecturemanager.ui.home.datapackage.DetailedAttendance
import java.util.Date


class DetailedAttendanceAdapter(private var attendanceList: List<DetailedAttendance>) :
    RecyclerView.Adapter<DetailedAttendanceAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_detailed_attendance, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val attendance = attendanceList[position]
        holder.dateTextView.text = Date(attendance.date).toString()  // Format date if needed
        holder.statusTextView.text = attendance.status
    }

    override fun getItemCount(): Int {
        return attendanceList.size
    }

    fun updateData(newAttendanceList: List<DetailedAttendance>) {
        attendanceList = newAttendanceList
        notifyDataSetChanged()
    }
}
