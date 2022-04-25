package com.afauzi.peoemergency.screen.auth.registerStep

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.databinding.ActivityRegisterDetailProfileFinishBinding
import com.afauzi.peoemergency.screen.main.MainActivity
import com.afauzi.peoemergency.utils.FirebaseServiceInstance
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class RegisterDetailProfileFinish : AppCompatActivity() {

    companion object {
        const val TAG = "RegisterDetailProfileFinish"
    }

    private lateinit var binding: ActivityRegisterDetailProfileFinishBinding

    private lateinit var btnStepFinish: Button

    private lateinit var tvUsername: TextView

    private fun initView() {
        btnStepFinish = binding.btnStepFinish
        tvUsername = binding.tvUsername
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterDetailProfileFinishBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initView()

        tvUsername.text = resources.getString(R.string.hi_name, intent.extras?.getString("usernameStep2"))
        val username = intent.extras?.getString("usernameStep2")
        val gender = intent.extras?.getString("genderStep2")
        val phone = intent.extras?.getString("phoneStep2")
        val email = intent.extras?.getString("emailStep2")
        val password = intent.extras?.getString("passwordStep2")
        val dateJoin = intent.extras?.getString("dateJoinStep2")
        val imgUri = intent.extras?.getString("imgUriStep2")
        val birthday = intent.extras?.getString("userBirthdayStep2")

        val arr = arrayOf(
            username,
            gender,
            phone,
            email,
            password,
            dateJoin,
            imgUri,
            birthday,
        )
        for (i in arr) {
            Log.i(TAG, i.toString())
        }

    }

    override fun onResume() {
        super.onResume()

        btnStepFinish.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}