package com.afauzi.peoemergency.screen.auth.registerStep

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.databinding.ActivityRegisterProfileDetailStep1Binding
import com.afauzi.peoemergency.databinding.ActivityRegisterProfileStep2Binding
import com.afauzi.peoemergency.utils.FirebaseServiceInstance
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class RegisterProfileStep2 : AppCompatActivity() {

    companion object {
        const val TAG = "RegisterProfileStep2"
    }

    private lateinit var binding: ActivityRegisterProfileStep2Binding
    private lateinit var btnStep2: Button
    private lateinit var tvUsername: TextView

    private fun initView() {
        btnStep2 = binding.btnStep2
        tvUsername = binding.username
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterProfileStep2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()

        tvUsername.text = resources.getString(R.string.hi_name, intent.extras?.getString("usernameStep1"))
    }

    override fun onResume() {
        super.onResume()

        btnStep2.setOnClickListener {
          passingData()
        }
    }

    fun passingData() {
        val username = intent.extras?.getString("usernameStep1")
        val gender = intent.extras?.getString("genderStep1")
        val phone = intent.extras?.getString("phoneStep1")
        val email = intent.extras?.getString("emailStep1")
        val password = intent.extras?.getString("passwordStep1")
        val dateJoin = intent.extras?.getString("dateJoinStep1")

    }

}