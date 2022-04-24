package com.afauzi.peoemergency.screen.auth.registerStep

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.databinding.ActivityRegisterProfileDetailStep1Binding
import com.afauzi.peoemergency.databinding.ActivityRegisterProfileStep2Binding

class RegisterProfileStep2 : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterProfileStep2Binding
    private lateinit var btnStep2: Button

    private fun initView() {
        btnStep2 = binding.btnStep2
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterProfileStep2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    override fun onResume() {
        super.onResume()

        btnStep2.setOnClickListener {
            startActivity(Intent(this, RegisterDetailProfileFinish::class.java))
        }
    }
}