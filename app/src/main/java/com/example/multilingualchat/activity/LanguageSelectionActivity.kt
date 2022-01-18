package com.example.multilingualchat.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.multilingualchat.databinding.ActivityLanguageSelectionBinding

class LanguageSelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLanguageSelectionBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLanguageSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.teluguCard.setOnClickListener {
            setPreferredLanguage("telugu")
        }
        binding.englishCard.setOnClickListener {
            setPreferredLanguage("english")
        }
        binding.kannadaCard.setOnClickListener {
            setPreferredLanguage("kannada")
        }
        binding.hindiCard.setOnClickListener {
            setPreferredLanguage("hindi")
        }
    }

    private fun setPreferredLanguage(language: String) {

        val sharedPreferences =
            getSharedPreferences("languageShref", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.putString("language", language)
        editor.apply()

        val intent = Intent(this, AuthenticationActivity::class.java)

        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }


}