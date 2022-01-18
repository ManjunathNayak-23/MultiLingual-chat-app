package com.example.multilingualchat.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.multilingualchat.activity.ChatActivity
import com.example.multilingualchat.adapter.DashboardRecyclerAdapter
import com.example.multilingualchat.databinding.FragmentUsersBinding
import com.example.multilingualchat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class UsersFragment : Fragment(), DashboardRecyclerAdapter.onItemClick {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    private val list: ArrayList<User> = ArrayList()
    private lateinit var database: FirebaseDatabase
    private lateinit var uid: String
    private lateinit var adapter: DashboardRecyclerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUsersBinding.inflate(layoutInflater, container, false)
        uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        database = FirebaseDatabase.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dashboardRecycler.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )

        adapter = DashboardRecyclerAdapter(
            list,
            requireContext(),
            this@UsersFragment
        )
        binding.dashboardRecycler.adapter = adapter


        initData()

    }


    private fun initData() {
        binding.spinKit.visibility = View.VISIBLE
        GlobalScope.launch(Dispatchers.IO) {


            database.reference.child("Users").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    val children = snapshot.children

                    children.forEach {

                        val user = it.getValue(User::class.java)

                        if (user != null) {
                            if (user.uid != uid) {
                                list.add(user)

                            }


                        }
                    }
                    adapter.notifyDataSetChanged()
                    binding.spinKit.visibility = View.GONE

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "error occurred", Toast.LENGTH_SHORT)
                        .show()
                }


            })


        }

    }

    override fun onClick(uid: String, name: String, imgUrl: String, language: String) {
        val intent = Intent(requireActivity(), ChatActivity::class.java)
        intent.putExtra("uid", uid)
        intent.putExtra("name", name)
        intent.putExtra("imgUrl", imgUrl)
        intent.putExtra("language", language)
        startActivity(intent)
        this.activity?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}