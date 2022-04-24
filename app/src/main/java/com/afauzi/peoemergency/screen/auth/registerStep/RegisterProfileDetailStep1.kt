package com.afauzi.peoemergency.screen.auth.registerStep

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.databinding.ActivityRegisterProfileDetailStep1Binding

class RegisterProfileDetailStep1 : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterProfileDetailStep1Binding
    private lateinit var btnNextStep: Button

    private fun initView() {
        btnNextStep = binding.btnStep1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterProfileDetailStep1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    override fun onResume() {
        super.onResume()

        btnNextStep.setOnClickListener {
            startActivity(Intent(this, RegisterProfileStep2::class.java))
        }
    }
}