package com.example.lecturemanager.ui.profile


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lecturemanager.databinding.FragmentProfileBinding
import com.example.lecturemanager.ui.ui.ui.login.AppInfoActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Get user details from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("LectureManagerPrefs", Context.MODE_PRIVATE)
        val firstName = sharedPreferences.getString("FIRST_NAME", "User")
        val lastName = sharedPreferences.getString("LAST_NAME","User")
        val fullName = "$firstName $lastName"
        val initials = "${firstName?.get(0)}${lastName?.get(0)}"

        // Set user details to views
        binding.initialsTextView.text = initials
        binding.fullNameTextView.text = fullName

        // Share App
        binding.shareAppLayout.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Check out this amazing app!")
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }

        // Rate App on Google Play
//        binding.rateAppLayout.setOnClickListener {
//            val uri = Uri.parse("market://details?id=${requireContext().packageName}")
//            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
//            startActivity(goToMarket)
//        }

        // Report Bug
        binding.reportBugLayout.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("lecturemanager805@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "Bug Report")
                putExtra(Intent.EXTRA_TEXT, "I found a bug...")
            }
            startActivity(emailIntent)
        }

        // App Info
        binding.appInfoLayout.setOnClickListener {
            val appInfoIntent = Intent(requireContext(), AppInfoActivity::class.java)
            startActivity(appInfoIntent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

