package com.example.multilingualchat.activity

import android.R.attr
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.multilingualchat.R
import com.example.multilingualchat.adapter.ChatAdapter
import com.example.multilingualchat.databinding.ActivityChatBinding
import com.example.multilingualchat.model.Message

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private  var messagesList: ArrayList<Message> = ArrayList()
    private lateinit var adapter: ChatAdapter
    private lateinit var userLanguage: String
    private lateinit var otherPersonImgUrl: String
    private lateinit var otherPersonLanguage: String


    companion object {
        const val REQUEST_CODE_SPEECH_INPUT = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.parseColor("#0093bc")
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val otherPersonName = intent.getStringExtra("name").toString()
        val otherPersonUid = intent.getStringExtra("uid").toString()
        otherPersonImgUrl = intent.getStringExtra("imgUrl").toString()
        otherPersonLanguage = intent.getStringExtra("language").toString()
        val myUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        binding.personName.text = otherPersonName


        val sharedPreferences = getSharedPreferences("languageShref", Context.MODE_PRIVATE)
        userLanguage = sharedPreferences?.getString("language", "").toString()

        binding.backBtn.setOnClickListener {
            finish()
        }


        adapter = ChatAdapter(this@ChatActivity, otherPersonImgUrl,otherPersonLanguage,userLanguage)
        binding.chatRecycler.adapter = adapter


        binding.voiceBtn.setOnClickListener {
            var languagePref = "en"
            when (userLanguage) {
                "telugu" -> {
                    languagePref = "te"
                }
                "hindi" -> {
                    languagePref = "hi"

                }
                "kannada" -> {
                    languagePref = "kn"
                }
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languagePref)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languagePref)
            intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languagePref)

            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
            } catch (e: Exception) {
                Toast
                    .makeText(
                        this@ChatActivity, " " + e.message,
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }
        }



        if (otherPersonImgUrl == "default") {

            Glide.with(this).load(R.drawable.ic_profile_pic).skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(binding.profileImgChat)
        } else {

            Glide.with(this).load(otherPersonImgUrl).skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(binding.profileImgChat)
        }


        readMessages(myUid, otherPersonUid)



        binding.sendMessage.setOnClickListener {
            val msg = binding.msgEditText.text.toString()
            if (msg != "") {
                sendMessage(myUid, otherPersonUid, msg)
            } else {
                Toast.makeText(this, "you can't send empty message", Toast.LENGTH_SHORT).show()
            }
            binding.msgEditText.setText("")
        }




    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode === RESULT_OK && attr.data != null) {
                val result: ArrayList<String> = data?.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                ) as ArrayList<String>
                binding.msgEditText.setText(
                    Objects.requireNonNull(result)[0]
                )
            }
        }

    }



    private fun readMessages(myId: String, userid: String) {

        //   GlobalScope.launch(Dispatchers.IO) {

        val reference = FirebaseDatabase.getInstance().reference
        reference.child("Chats").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messagesList.clear()
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val message = dataSnapshot.getValue(Message::class.java)

                    if (message?.receiver == myId && message.sender == userid || message?.receiver == userid && message.sender == myId) {
                        messagesList.add(message)
                        adapter.updateList(messagesList)






                    }


                }
                binding.chatRecycler.scrollToPosition(messagesList.size-1)



            }

            override fun onCancelled(error: DatabaseError) {
             Toast.makeText(this@ChatActivity,error.message.toString(),Toast.LENGTH_SHORT).show()
            }

        })


    }



    private fun sendMessage(sender: String, receiver: String, message: String) {
        val reference = FirebaseDatabase.getInstance().reference
        val hashmap = HashMap<String, String>()
        hashmap["sender"] = sender
        hashmap["receiver"] = receiver
        hashmap["message"] = message
        hashmap["msgLang"] = userLanguage
        reference.child("Chats").push().setValue(hashmap)

    }

}