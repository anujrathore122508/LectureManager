package com.example.lecturemanager.ui.ui


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lecturemanager.MainActivity
import com.example.lecturemanager.R

class WelcomePageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user details are already saved
        val sharedPreferences = getSharedPreferences("LectureManagerPrefs", Context.MODE_PRIVATE)
        val firstName = sharedPreferences.getString("FIRST_NAME", null)
        val lastName = sharedPreferences.getString("LAST_NAME", null)

        if (firstName != null && lastName != null) {
            // If user details are already saved, go to MainActivity
            goToMainActivity(firstName, lastName)
            return
        }

        setContentView(R.layout.welcomepage)

        val firstNameEditText = findViewById<EditText>(R.id.firstNameEditText)
        val lastNameEditText = findViewById<EditText>(R.id.lastNameEditText)
        val submitButton = findViewById<Button>(R.id.submitButton)

        submitButton.setOnClickListener {
            var firstNameInput = firstNameEditText.text.toString()
            var lastNameInput = lastNameEditText.text.toString()

            // Capitalize the first letter of each name
            firstNameInput = capitalizeFirstLetter(firstNameInput)
            lastNameInput = capitalizeFirstLetter(lastNameInput)

            // Check if both fields are filled
            if (firstNameInput.isNotBlank() && lastNameInput.isNotBlank()) {
                // Save user details in SharedPreferences
                with(sharedPreferences.edit()) {
                    putString("FIRST_NAME", firstNameInput)
                    putString("LAST_NAME", lastNameInput)
                    putBoolean("IS_FIRST_TIME", false) // Mark that the app has been used
                    apply()
                }

                goToMainActivity(firstNameInput, lastNameInput)
            } else {
                // Optionally show an error message
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun capitalizeFirstLetter(text: String): String {
        return text.split(" ").joinToString(" ") { it.capitalize() }
    }

    private fun goToMainActivity(firstName: String, lastName: String) {
        val intent = Intent(this@WelcomePageActivity, MainActivity::class.java)
        intent.putExtra("FIRST_NAME", firstName)
        intent.putExtra("LAST_NAME", lastName)
        startActivity(intent)
        finish()
    }
}
