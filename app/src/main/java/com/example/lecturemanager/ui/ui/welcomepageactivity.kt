package com.example.lecturemanager.ui.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.lecturemanager.MainActivity
import com.example.lecturemanager.R

class WelcomePageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcomepage)

        val firstNameEditText = findViewById<EditText>(R.id.firstNameEditText)
        val lastNameEditText = findViewById<EditText>(R.id.lastNameEditText)
        val submitButton = findViewById<Button>(R.id.submitButton)

        submitButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()

            // Data ko HomeActivity par pass karne ke liye Intent ka use karen
            val intent = Intent(this@WelcomePageActivity, MainActivity::class.java)
            intent.putExtra("FIRST_NAME", firstName)
            intent.putExtra("LAST_NAME", lastName)
            startActivity(intent)
            finish()
        }
    }
}
