package com.example.multilingualchat.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.multilingualchat.R
import com.example.multilingualchat.databinding.FragmentForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordFragment : Fragment() {
    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private var language = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentForgotPasswordBinding.inflate(layoutInflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences =
            activity?.getSharedPreferences("languageShref", Context.MODE_PRIVATE)

        language = sharedPreferences?.getString("language", "").toString()
        when (language) {
            "telugu" -> {
                binding.tvForgotPassword.text = getString(R.string.forgot_Password_TN)
                binding.tvEmail.text = getString(R.string.email_TG)
                binding.etEmail.hint = getString(R.string.enter_your_email_TG)
                binding.note.text = getString(R.string.note_TN)
                binding.forgotPasswordBtn.text = getString(R.string.send_reset_mail_TN)
            }
            "hindi" -> {
                binding.tvForgotPassword.text = getString(R.string.forgot_Password_HN)
                binding.tvEmail.text = getString(R.string.email_HN)
                binding.etEmail.hint = getString(R.string.enter_your_email_HN)
                binding.note.text = getString(R.string.note_HN)
                binding.forgotPasswordBtn.text = getString(R.string.send_reset_mail_HN)
            }
            "kannada" -> {
                binding.tvForgotPassword.text = getString(R.string.forgot_Password_KN)
                binding.tvEmail.text = getString(R.string.email_KN)
                binding.etEmail.hint = getString(R.string.enter_your_email_KN)
                binding.note.text = getString(R.string.note_KN)
                binding.forgotPasswordBtn.text = getString(R.string.send_reset_mail_KN)
            }

        }

        binding.forgotPasswordBtn.setOnClickListener {

            val email = binding.etEmail.text.toString()
            if (email.isEmpty()) {
                binding.etEmail.error = "Email is required"
                return@setOnClickListener
            }

            sendForgotLink(email)
        }
    }

    private fun sendForgotLink(email: String) {
        binding.ForgotPasswordScreen.visibility = View.VISIBLE
        binding.fpLoader.visibility = View.VISIBLE

        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                binding.fpLoader.visibility = View.GONE
                binding.ForgotPasswordScreen.visibility = View.GONE

            } else {
                Toast.makeText(requireContext(), task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}