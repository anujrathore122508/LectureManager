package com.example.lecturemanager.ui.ui.ui.login


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lecturemanager.databinding.ActivityAppInfoBinding

class AppInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the app information text
        binding.appVersionTextView.text = "Version ${7.0}"
//        binding.appAuthorTextView.text = "by YourCompanyName"
    }
}
