package com.afauzi.peoemergency.screen.auth.registerStep

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.databinding.ActivityRegisterDetailProfileFinishBinding
import com.afauzi.peoemergency.screen.main.MainActivity

class RegisterDetailProfileFinish : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterDetailProfileFinishBinding

    private lateinit var btnStepFinish: Button

    private fun initView() {
        btnStepFinish = binding.btnStepFinish
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterDetailProfileFinishBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initView()
    }

    override fun onResume() {
        super.onResume()

        btnStepFinish.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}