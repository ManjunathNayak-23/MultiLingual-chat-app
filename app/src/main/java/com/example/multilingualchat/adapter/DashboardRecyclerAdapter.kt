package com.example.multilingualchat.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.multilingualchat.R
import com.example.multilingualchat.databinding.ChatSingleRowBinding
import com.example.multilingualchat.model.User

class DashboardRecyclerAdapter(private val list:List<User>,
    private val context: Context, val listener: onItemClick
) : RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.chat_single_row, parent, false)
        return DashboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {

        holder.binding.profileName.text = list[position].name

        if (list[position].profilePic == "default") {
            Glide.with(context).load(R.drawable.ic_profile_pic).skipMemoryCache(false).diskCacheStrategy(
                DiskCacheStrategy.AUTOMATIC).placeholder(R.drawable.ic_profile_pic).into(holder.binding.profilePic)
        }else{
            Glide.with(context).load(list[position].profilePic).skipMemoryCache(false).diskCacheStrategy(
                DiskCacheStrategy.AUTOMATIC).placeholder(R.drawable.ic_profile_pic).into(holder.binding.profilePic)
        }
        holder.binding.card.animation =
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.reycler_anim)



    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class DashboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val binding = ChatSingleRowBinding.bind(itemView)

        init {
            itemView.setOnClickListener(this)

        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onClick(
                    list[position].uid.toString(),
                    list[position].name.toString(),
                    list[position].profilePic.toString(),
                    list[position].language.toString()
                )
            }

        }
    }

    interface onItemClick {
        fun onClick(uid: String, name: String, imgUrl: String,language:String)
    }

}