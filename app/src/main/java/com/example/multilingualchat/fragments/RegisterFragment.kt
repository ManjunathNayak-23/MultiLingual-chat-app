package com.example.multilingualchat.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.multilingualchat.R
import com.example.multilingualchat.databinding.FragmentRegisterBinding
import com.example.multilingualchat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.regex.Matcher
import java.util.regex.Pattern


class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null

    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private var language = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences =
            activity?.getSharedPreferences("languageShref", Context.MODE_PRIVATE)

        language = sharedPreferences?.getString("language", "").toString()
        when (language) {
            "telugu" -> {
                binding.tvSignUp.text = getString(R.string.sign_up_TG)
                binding.tvFullName.text = getString(R.string.full_name_TG)
                binding.tvPhoneNumber.text = getString(R.string.phone_number_TG)
                binding.etEmailSignup.hint = getString(R.string.enter_your_name_TG)
                binding.tvEmailSignup.text = getString(R.string.email_TG)
                binding.tvPasswordSignup.text = getString(R.string.password_TG)
                binding.tvPasswordConfirm.text = getString(R.string.confirm_password_TG)
                binding.etFullName.hint = getString(R.string.enter_your_name_TG)
                binding.etPasswordConfirm.hint = getString(R.string.confirm_password_TG)
                binding.etPhoneNumber.hint = getString(R.string.enter_your_phone_number_TG)
                binding.registerBtn.text = getString(R.string.register_TG)
                binding.tvGotoSignin.text = getString(R.string.have_an_account_signin_TG)
            }
            "hindi" -> {
                binding.tvSignUp.text = getString(R.string.sign_in_HN)
                binding.tvFullName.text = getString(R.string.full_name_HN)
                binding.tvPhoneNumber.text = getString(R.string.phone_number_HN)
                binding.tvEmailSignup.text = getString(R.string.email_HN)
                binding.tvPasswordSignup.text = getString(R.string.password_HN)
                binding.etEmailSignup.hint = getString(R.string.enter_your_name_HN)
                binding.tvPasswordConfirm.text = getString(R.string.confirm_password_HN)
                binding.etFullName.hint = getString(R.string.enter_your_name_HN)
                binding.etPasswordConfirm.hint = getString(R.string.confirm_password_HN)
                binding.etPhoneNumber.hint = getString(R.string.enter_your_phone_number_HN)
                binding.registerBtn.text = getString(R.string.register_HN)
                binding.tvGotoSignin.text = getString(R.string.have_an_account_signin_HN)
            }
            "kannada" -> {
                binding.tvSignUp.text = getString(R.string.sign_in_KN)
                binding.tvFullName.text = getString(R.string.full_name_KN)
                binding.tvPhoneNumber.text = getString(R.string.phone_number_KN)
                binding.tvEmailSignup.text = getString(R.string.email_KN)
                binding.tvPasswordSignup.text = getString(R.string.password_KN)
                binding.etEmailSignup.hint = getString(R.string.enter_your_name_KN)
                binding.tvPasswordConfirm.text = getString(R.string.confirm_password_KN)
                binding.etFullName.hint = getString(R.string.enter_your_name_KN)
                binding.etPasswordConfirm.hint = getString(R.string.confirm_password_KN)
                binding.etPhoneNumber.hint = getString(R.string.enter_your_phone_number_KN)
                binding.registerBtn.text = getString(R.string.register_KN)
                binding.tvGotoSignin.text = getString(R.string.have_an_account_signin_KN)
            }
        }


        binding.tvGotoSignin.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_registerFragment_to_loginFragment)

        }

        binding.registerBtn.setOnClickListener {
            checkMandatoryFields()
        }
    }

    private fun checkMandatoryFields() {
        val email = binding.etEmailSignup.text.toString().trim()
        val phoneNumber = binding.etPhoneNumber.text.toString().trim()
        val personName = binding.etFullName.text.toString()
        val password = binding.etPasswordSignup.text.toString().trim()
        val confirmPassword = binding.etPasswordConfirm.text.toString().trim()

        if (email.isEmpty()) {
            binding.etEmailSignup.error = "Email is Required"
            return
        }
        if (phoneNumber.isEmpty()) {
            binding.etPhoneNumber.error = "Phone Number is Required"
            return
        }
        if (personName.isEmpty()) {
            binding.etFullName.error = "Name is Required"
            return
        }
        if (password.isEmpty()) {
            binding.etPasswordSignup.error = "Password is Required"
            return
        }
        if (confirmPassword.isEmpty()) {
            binding.etPasswordConfirm.error = "Confirm password is Required"
            return
        }
        if (phoneNumber.length != 10) {
            binding.etPhoneNumber.error = "Enter a valid Phone Number"
            return
        }
        if (password != confirmPassword) {
            binding.etPasswordConfirm.error = "Password does not match"
            return
        }

        if (password.length < 6) {
            binding.etPasswordSignup.error = "Password length should be greater than 6"
            return
        }
        if (isEmailValid(email)) {
            registerTheUser(email, phoneNumber, personName, password)

        } else {
            Toast.makeText(requireContext(), "Enter a valid email", Toast.LENGTH_SHORT).show()
        }


    }

    fun isEmailValid(email: String?): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun registerTheUser(
        email: String,
        phoneNumber: String,
        personName: String,
        password: String
    ) {
        binding.registerScreen.visibility = View.VISIBLE
        binding.registerLoader.visibility = View.VISIBLE
        binding.loadingTxt.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val uId: String = auth.currentUser?.uid.toString()
                    uploadUserToRealtimeDatabase(uId, email, phoneNumber, personName)

                } else {
                    binding.registerScreen.visibility = View.GONE
                    binding.registerLoader.visibility = View.GONE
                    binding.loadingTxt.visibility = View.GONE
                    Toast.makeText(requireContext(), "Authentication Failed", Toast.LENGTH_LONG)
                        .show()
                }


            }
    }

    private fun uploadUserToRealtimeDatabase(
        uid: String,
        email: String,
        phoneNumber: String,
        personName: String
    ) {
        val user = User(uid, email, personName, phoneNumber, "default", language)
        val database = Firebase.database
        val myRef = database.getReference("Users")
        myRef.child(uid).setValue(user).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                view?.let {

                    Navigation.findNavController(it)
                        .navigate(R.id.action_registerFragment_to_chooseNameAndImageFragment)
                    binding.registerScreen.visibility = View.GONE
                    binding.registerLoader.visibility = View.GONE
                    binding.loadingTxt.visibility = View.GONE
                }
            } else {
                binding.registerScreen.visibility = View.GONE
                binding.registerLoader.visibility = View.GONE
                binding.loadingTxt.visibility = View.GONE
                Toast.makeText(requireContext(), "Some Error Occurred", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}