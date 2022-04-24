package com.afauzi.peoemergency.screen.auth.registerStep

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.databinding.ActivityRegisterProfileDetailStep1Binding
import com.afauzi.peoemergency.utils.FirebaseServiceInstance
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class RegisterProfileDetailStep1 : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterProfileDetailStep1Binding
    private lateinit var btnNextStep: Button
    private lateinit var tvUsername: TextView

    private fun initView() {
        btnNextStep = binding.btnStep1
        tvUsername = binding.username
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterProfileDetailStep1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        getData()
    }

    override fun onResume() {
        super.onResume()

        btnNextStep.setOnClickListener {
            val intent = Intent(this, RegisterProfileStep2::class.java)
            startActivity(intent)

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
                    binding.username.text = resources.getString(R.string.hi_name, usernameSnapshot)
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