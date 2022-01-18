package com.example.multilingualchat.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.multilingualchat.R
import com.example.multilingualchat.activity.AuthenticationActivity
import com.example.multilingualchat.databinding.FragmentProfileBinding
import com.example.multilingualchat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var selectedImage: Uri

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        auth = Firebase.auth
        storage = FirebaseStorage.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNameAndImage()

        binding.changeProfilePicBtn.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                ChooseNameAndImageFragment.PICK_IMAGE
            )
        }
        binding.changeNameBtn.setOnClickListener {
            showDialog()
        }

        binding.LogOutBtn.setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireActivity(), AuthenticationActivity::class.java))
            this.activity?.finishAffinity()
            activity?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    private fun showDialog() {
        val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(requireActivity())
        builder.setTitle("Update Name")

// Set up the input
        val input = EditText(requireContext())
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setHint("Enter Name")
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

// Set up the buttons
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            // Here you get get input text from the Edittext

            val name = input.text.toString()
            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                changeInDatabase(name)
            }

        })
        builder.setNegativeButton(
            "Cancel",
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }

    private fun changeInDatabase(name: String) {
        binding.profileScreen.visibility = View.VISIBLE
        binding.profileLoader.visibility = View.VISIBLE
        binding.proLoaderTxt.visibility = View.VISIBLE
        val reference = FirebaseDatabase.getInstance().reference
        reference.child("Users").child(auth.currentUser?.uid.toString()).child("name")
            .setValue(name).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    binding.profileScreen.visibility = View.GONE
                    binding.profileLoader.visibility = View.GONE
                    binding.proLoaderTxt.visibility = View.GONE
                } else {
                    Toast.makeText(requireContext(), "Some error occurred", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun setNameAndImage() {
        binding.profileScreen.visibility = View.VISIBLE
        binding.profileLoader.visibility = View.VISIBLE
        binding.proLoaderTxt.visibility = View.VISIBLE
        GlobalScope.launch(Dispatchers.IO) {


            val uid = auth.currentUser?.uid.toString()
            val reference = FirebaseDatabase.getInstance().reference
            reference.child("Users").child(uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        val users = snapshot.getValue(User::class.java)

                        Glide.with(requireContext()).load(users?.profilePic).skipMemoryCache(false)
                            .diskCacheStrategy(
                                DiskCacheStrategy.AUTOMATIC
                            ).placeholder(R.drawable.ic_profile_pic)

                            .into(binding.userProfilePic)
                        binding.userProfilename.text = users?.name.toString()

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }
        binding.profileScreen.visibility = View.GONE
        binding.profileLoader.visibility = View.GONE
        binding.proLoaderTxt.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (data.data != null) {
                binding.userProfilePic.setImageURI(data.data)
                selectedImage = data.data!!
                if (selectedImage != null) {
                    binding.profileScreen.visibility = View.VISIBLE
                    binding.profileLoader.visibility = View.VISIBLE
                    binding.proLoaderTxt.visibility = View.VISIBLE
                    val reference: StorageReference =
                        storage.reference.child("Profiles").child(auth.uid.toString())
                    reference.putFile(selectedImage)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                reference.downloadUrl.addOnSuccessListener { uri ->
                                    val imageUrl = uri.toString()
                                    setImageUrl(imageUrl)

                                }
                            }
                        }
                }
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
                    Toast.makeText(requireContext(), "uploaded successfully", Toast.LENGTH_SHORT)
                        .show()
                    binding.profileScreen.visibility = View.GONE
                    binding.profileLoader.visibility = View.GONE
                    binding.proLoaderTxt.visibility = View.GONE

                } else {
                    binding.profileScreen.visibility = View.GONE
                    binding.profileLoader.visibility = View.GONE
                    binding.proLoaderTxt.visibility = View.GONE

                    Toast.makeText(
                        requireContext(),
                        "Unable to upload please try again in some time",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}