package com.example.multilingualchat.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.multilingualchat.R
import com.example.multilingualchat.activity.DashboardActivity
import com.example.multilingualchat.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!


    private lateinit var auth: FirebaseAuth
    private var language = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvForgotPassword.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
        val sharedPreferences =
            activity?.getSharedPreferences("languageShref", Context.MODE_PRIVATE)

        language = sharedPreferences?.getString("language", "").toString()
        when (language) {
            "telugu" -> {
                binding.tvEmail.text = getString(R.string.email_TG)
                binding.tvPassword.text = getString(R.string.password_TG)
                binding.tvForgotPassword.text = getString(R.string.forgot_password_TG)
                binding.tvGoToSignUp.text = getString(R.string.dont_have_account_signup_TG)
                binding.tvSignIn.text = getString(R.string.sign_in_TG)
                binding.loginBtn.text = getString(R.string.login_TG)
                binding.etEmail.hint = getString(R.string.enter_your_email_TG)

            }
            "hindi" -> {
                binding.tvEmail.text = getString(R.string.email_HN)
                binding.tvPassword.text = getString(R.string.password_HN)
                binding.tvForgotPassword.text = getString(R.string.forgot_password_HN)
                binding.tvGoToSignUp.text = getString(R.string.forgot_password_HN)
                binding.tvSignIn.text = getString(R.string.sign_in_HN)
                binding.loginBtn.text = getString(R.string.login_HN)
                binding.etEmail.hint = getString(R.string.enter_your_email_HN)
            }
            "kannada" -> {
                binding.tvEmail.text = getString(R.string.email_KN)
                binding.tvPassword.text = getString(R.string.password_KN)
                binding.tvForgotPassword.text = getString(R.string.forgot_password_KN)
                binding.tvGoToSignUp.text = getString(R.string.dont_have_account_signup_KN)
                binding.tvSignIn.text = getString(R.string.sign_in_KN)
                binding.loginBtn.text = getString(R.string.login_KN)
                binding.etEmail.hint = getString(R.string.enter_your_email_KN)
            }
        }









        binding.tvGoToSignUp.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_loginFragment_to_registerFragment)
        }


        binding.loginBtn.setOnClickListener {

            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            checkMandatoryFields(email, password)
        }
    }

    private fun checkMandatoryFields(email: String, password: String) {
        if (email.isEmpty()) {
            binding.etEmail.error = "Email is Required"
            return
        }
        if (password.isEmpty()) {
            binding.etPassword.error = "Password is Required"
            return
        }
        if (password.length < 6) {
            binding.etPassword.error = "Password is short!"
            return
        }
        loginTheUser(email, password)

    }

    private fun loginTheUser(email: String, password: String) {
        binding.loginScreen.visibility = View.VISIBLE
        binding.loginLoader.visibility = View.VISIBLE
        binding.loginTxt.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    changeLanguageInDb(language)

                } else {
                    binding.loginScreen.visibility = View.GONE
                    binding.loginLoader.visibility = View.GONE
                    binding.loginTxt.visibility = View.GONE
                    Toast.makeText(requireContext(), task.exception.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun changeLanguageInDb(language: String) {
        val reference = FirebaseDatabase.getInstance().reference
        reference.child("Users").child(auth.currentUser?.uid.toString()).child("language")
            .setValue(language).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(requireActivity(), DashboardActivity::class.java))
                    activity?.finishAffinity()

                } else {
                    Toast.makeText(requireContext(), "Some error occurred", Toast.LENGTH_SHORT)
                        .show()
                }
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}