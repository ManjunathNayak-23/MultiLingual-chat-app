package com.example.multilingualchat.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.multilingualchat.activity.DashboardActivity
import com.example.multilingualchat.databinding.FragmentChooseNameAndImageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class ChooseNameAndImageFragment : Fragment() {
    private var _binding: FragmentChooseNameAndImageBinding? = null

    private val binding get() = _binding!!
    private lateinit var storage: FirebaseStorage

    private lateinit var selectedImage: Uri
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChooseNameAndImageBinding.inflate(layoutInflater, container, false)
        auth = Firebase.auth
        storage= FirebaseStorage.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profilePicture.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)

        }

        binding.saveProfile.setOnClickListener {
            if (selectedImage != null) {
                binding.profileScreen.visibility=View.VISIBLE
                binding.profileLoader.visibility=View.VISIBLE
                binding.proLoaderTxt.visibility=View.VISIBLE
                val reference: StorageReference =
                    storage.reference.child("Profiles").child(auth.uid.toString())
                reference.putFile(selectedImage).addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        reference.downloadUrl.addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()
                            setImageUrl(imageUrl)

                        }
                    }
                }
            }else{
            Toast.makeText(requireContext(),"Select a image Please",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setImageUrl(imageUrl: String) {
        val database = FirebaseDatabase.getInstance()
        val map = HashMap<String, Any>()
        map["profilePic"] = imageUrl
        database.reference.child("Users").child(auth.uid.toString()).updateChildren(map)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(requireActivity(), DashboardActivity::class.java))
                    activity?.finishAffinity()
                    activity?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    binding.profileScreen.visibility=View.GONE
                    binding.profileLoader.visibility=View.GONE
                    binding.proLoaderTxt.visibility=View.GONE

                    Toast.makeText(
                        requireContext(),
                        "Unable to upload please try again in some time",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (data.data != null) {
                binding.profilePicture.setImageURI(data.data)
                selectedImage = data.data!!
            }
        }
    }

    companion object {
        const val PICK_IMAGE = 101
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}