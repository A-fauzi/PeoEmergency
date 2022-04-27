package com.afauzi.peoemergency.screen.main.fragment.activity.accountProfile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.afauzi.peoemergency.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}