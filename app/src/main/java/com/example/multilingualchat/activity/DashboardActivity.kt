package com.example.multilingualchat.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.multilingualchat.R
import com.example.multilingualchat.adapter.DashboardRecyclerAdapter
import com.example.multilingualchat.adapter.ViewPagerAdapter
import com.example.multilingualchat.databinding.ActivityDashboardBinding
import com.example.multilingualchat.fragments.UsersFragment
import com.example.multilingualchat.model.User
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.parseColor("#0093bc")
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

       val adapter=ViewPagerAdapter(supportFragmentManager,lifecycle)
        binding.pager.adapter=adapter

        TabLayoutMediator(binding.tabLayout,binding.pager){tab,position->
            when(position){
                0->{
                    tab.text="Chats"
                }
                1->{
                    tab.text="Profile"
                }

            }
        }.attach()





    }



}