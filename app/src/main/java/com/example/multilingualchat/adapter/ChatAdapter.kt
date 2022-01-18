package com.example.multilingualchat.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.multilingualchat.R
import com.example.multilingualchat.databinding.ItemRecievedBinding
import com.example.multilingualchat.databinding.ItemSentBinding
import com.example.multilingualchat.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ChatAdapter(private val context: Context,private val imageUrl:String,private val otherPersonLanguage:String,private val userLanguage:String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1
    private lateinit var englishGermanTranslator: Translator
    private var chatList:ArrayList<Message> = ArrayList()

    fun updateList( newList:ArrayList<Message>){
        this.chatList=newList
        this.notifyDataSetChanged()

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == MESSAGE_TYPE_RIGHT) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_sent, parent, false)
            SendViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_recieved, parent, false)
            ReceiverViewHolder(view)
        }

    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if(holder.javaClass == SendViewHolder::class.java){
            val viewHolder = holder as SendViewHolder
            val chat = chatList[position]
            viewHolder.binding.message.text=chat.message
            holder.binding.card.animation =
                AnimationUtils.loadAnimation(holder.itemView.context, R.anim.reycler_anim)

        }else{

            val viewHolder = holder as ReceiverViewHolder
            val chat = chatList[position]
            viewHolder.binding.message.text=chat.message
            holder.binding.card.animation =
                AnimationUtils.loadAnimation(holder.itemView.context, R.anim.reycler_anim)
            Glide.with(context).load(imageUrl).placeholder(R.drawable.ic_profile_pic).into(viewHolder.binding.profileImage)
        }


    }



    class SendViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding=ItemSentBinding.bind(view)




    }
   inner class ReceiverViewHolder(view: View) : RecyclerView.ViewHolder(view),View.OnClickListener  {
        val binding=ItemRecievedBinding.bind(view)
       init {

           itemView.setOnClickListener(this)
       }

       override fun onClick(p0: View?) {
           var isExpanded=chatList[adapterPosition].isExpanded
           if(!isExpanded){
               chatList[adapterPosition].isExpanded=true
               binding.expandableLayout.visibility=View.VISIBLE


           }else{
               chatList[adapterPosition].isExpanded=false
               binding.expandableLayout.visibility=View.GONE

           }

           val msg=chatList[adapterPosition].message
           englishGermanTranslator.translate(msg.toString())

               .addOnSuccessListener { translatedText ->
                   binding.loginLoader.visibility=View.GONE
                   Log.d("translated", translatedText.toString())

                   binding.translatedMessage.text=translatedText

                   // Translation successful.
               }
               .addOnFailureListener { exception ->
                   Log.d("trans exception second", exception.localizedMessage.toString())
                   // Error.
                   // ...
               }
       }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        loadTranslatorService()
    }
    private fun loadTranslatorService() {
        GlobalScope.launch(Dispatchers.IO) {
        if (otherPersonLanguage == "hindi" && userLanguage == "telugu") {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.HINDI)
                .setTargetLanguage(TranslateLanguage.TELUGU)
                .build()
            englishGermanTranslator = Translation.getClient(options)
        } else if (otherPersonLanguage == "hindi" && userLanguage == "kannada") {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.HINDI)
                .setTargetLanguage(TranslateLanguage.KANNADA)
                .build()
            englishGermanTranslator = Translation.getClient(options)
        } else if (otherPersonLanguage == "hindi" && userLanguage == "english") {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.HINDI)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build()
            englishGermanTranslator = Translation.getClient(options)
        } else if (otherPersonLanguage == "hindi" && userLanguage == "hindi") {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.HINDI)
                .setTargetLanguage(TranslateLanguage.HINDI)
                .build()
            englishGermanTranslator = Translation.getClient(options)
        } else if (otherPersonLanguage == "telugu" && userLanguage == "hindi") {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.TELUGU)
                .setTargetLanguage(TranslateLanguage.HINDI)
                .build()
            englishGermanTranslator = Translation.getClient(options)
        } else if (otherPersonLanguage == "telugu" && userLanguage == "kannada") {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.TELUGU)
                .setTargetLanguage(TranslateLanguage.KANNADA)
                .build()
            englishGermanTranslator = Translation.getClient(options)
        } else if (otherPersonLanguage == "telugu" && userLanguage == "english") {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.TELUGU)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build()
            englishGermanTranslator = Translation.getClient(options)
        } else if (otherPersonLanguage == "telugu" && userLanguage == "telugu") {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.TELUGU)
                .setTargetLanguage(TranslateLanguage.TELUGU)
                .build()
            englishGermanTranslator = Translation.getClient(options)
        } else if (otherPersonLanguage == "kannada" && userLanguage == "english") {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.KANNADA)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build()
            englishGermanTranslator = Translation.getClient(options)
        } else if (otherPersonLanguage == "kannada" && userLanguage == "telugu") {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.KANNADA)
                .setTargetLanguage(TranslateLanguage.TELUGU)
                .build()
            englishGermanTranslator = Translation.getClient(options)
        } else if (otherPersonLanguage == "kannada" && userLanguage == "hindi") {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.KANNADA)
                .setTargetLanguage(TranslateLanguage.HINDI)
                .build()
            englishGermanTranslator = Translation.getClient(options)
        } else if (otherPersonLanguage == "kannada" && userLanguage == "kannada") {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.KANNADA)
                .setTargetLanguage(TranslateLanguage.KANNADA)
                .build()
            englishGermanTranslator = Translation.getClient(options)
        } else if (otherPersonLanguage == "english" && userLanguage == "hindi") {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.HINDI)
                .build()
            englishGermanTranslator = Translation.getClient(options)
        } else if (otherPersonLanguage == "english" && userLanguage == "telugu") {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.TELUGU)
                .build()
            englishGermanTranslator = Translation.getClient(options)
        } else if (otherPersonLanguage == "english" && userLanguage == "kannada") {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.KANNADA)
                .build()
            englishGermanTranslator = Translation.getClient(options)
        } else if (otherPersonLanguage == "english" && userLanguage == "english") {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build()
            englishGermanTranslator = Translation.getClient(options)
        }



        englishGermanTranslator.downloadModelIfNeeded()
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    "successfully downloaded language",
                    Toast.LENGTH_SHORT
                ).show()

                // Model downloaded successfully. Okay to start translating.
                // (Set a flag, unhide the translation UI, etc.)
            }
            .addOnFailureListener { exception ->
                // Model couldn’t be downloaded or other internal error.
                // ...
                Toast.makeText(context, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                //     }
            }
        }
    }
    override fun getItemViewType(position: Int): Int {

     val   firebaseUser = FirebaseAuth.getInstance().currentUser
        return if (chatList[position].sender == firebaseUser?.uid.toString()) {
            MESSAGE_TYPE_RIGHT
        } else {
            MESSAGE_TYPE_LEFT
        }

    }
}