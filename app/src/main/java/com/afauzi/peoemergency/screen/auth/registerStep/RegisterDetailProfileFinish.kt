package com.afauzi.peoemergency.screen.auth.registerStep

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        getData()
    }

    override fun onResume() {
        super.onResume()

        btnStepFinish.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
    private fun getData() {

        // Shimmer load open animation view
        binding.veilLayout.veil()

        FirebaseServiceInstance.user = FirebaseServiceInstance.auth.currentUser!!
        val uid = FirebaseServiceInstance.user.uid

        FirebaseServiceInstance.databaseReference = FirebaseServiceInstance.firebaseDatabase.getReference("users").child(uid)
        FirebaseServiceInstance.databaseReference.addValueEventListener(object :
            ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val usernameSnapshot = snapshot.child("username").value.toString()
                    binding.tvUsername.text = resources.getString(R.string.hi_name, usernameSnapshot)
                    // Shimmer load close animation view
                    binding.veilLayout.unVeil()
                } else {
                    // Shimmer load close animation view
                    binding.veilLayout.unVeil()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Shimmer load close animation view
                binding.veilLayout.unVeil()
            }

        })
    }
}