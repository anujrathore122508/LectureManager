package com.example.lecturemanager.ui.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lecturemanager.MainActivity
import com.example.lecturemanager.R


class UsesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_uses)

        // Set the status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.purple_700)
        }
        // Find the LinearLayout for instructions and dividers
        val instructionsContainer = findViewById<LinearLayout>(R.id.instructionsContainer)

        // Define steps
        val steps = listOf(
            " 1: Tap \"Add Lecture\" on the home screen at the Bottom to add a lecture.",
            " 2: Enter the lecture details: Name, Date, Day, Start Time, and End Time.",
            " 3: After adding lecture details, this app will notify you 5 minutes before the lecture each week on the scheduled day.",
            " 4: The notification will have \"Accept\" and \"Decline\" buttons to mark attendance.",
            " 5: Clicking \"Accept\" will mark your attendance as Present, and \"Decline\" will mark it as absent.",
            " 6: Attendance will be updated based on your response. View details on the Attendance screen.",
            " 7: Tap the \"Day\" button on the home screen to view lectures for each day.",
            " 8: Use the three dots next to a lecture to edit or delete it."
        )

        // Add steps and dividers
        for (step in steps) {
            val stepTextView = TextView(this).apply {
                text = step
                textSize = 18f
                setTextColor(Color.BLACK)
                setPadding(0, 0, 0, 16) // Padding at the bottom
            }

            val divider = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1 // Divider height
                ).apply {
                    setMargins(0, 16, 0, 16) // Margin around the divider
                }
                setBackgroundColor(Color.BLACK) // Divider color
            }

            // Add views to container
            instructionsContainer.addView(stepTextView)
            instructionsContainer.addView(divider)
        }

        // Find the TextView for "How To Use" and set its text
        val howToUseTextView = findViewById<TextView>(R.id.howToUseTextView)
        howToUseTextView.text = "How To Use"

        // Set padding to account for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Mark that UsesActivity has been shown
        val sharedPreferences = getSharedPreferences("LectureManagerPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("IS_USES_SHOWN", true)
            apply()
        }

        // Handle "Continue" button click
        val continueButton = findViewById<Button>(R.id.continueButton)
        continueButton.setOnClickListener {
            // Start MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Close UsesActivity
        }
    }
}
