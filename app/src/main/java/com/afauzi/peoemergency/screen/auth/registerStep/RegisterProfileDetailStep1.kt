package com.afauzi.peoemergency.screen.auth.registerStep

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.databinding.ActivityRegisterProfileDetailStep1Binding
import com.afauzi.peoemergency.utils.FirebaseServiceInstance
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.hbb20.CountryCodePicker

class RegisterProfileDetailStep1 : AppCompatActivity() {

    companion object {
        const val TAG = "RegisterProfileDetailStep1"
    }

    private lateinit var binding: ActivityRegisterProfileDetailStep1Binding
    private lateinit var btnNextStep: Button
    private lateinit var tvUsername: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var codeCountryCodePicker: CountryCodePicker
    private lateinit var phoneNumber: EditText

    private fun initView() {
        btnNextStep = binding.btnStep1
        tvUsername = binding.username
        radioGroup = binding.enableRadioGroup
        codeCountryCodePicker = binding.codeCountryPicker
        phoneNumber = binding.phoneNumber
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

            dataToStore()
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

    private fun dataToStore() {
        val selectOption: Int = radioGroup.checkedRadioButtonId
        val radioButton: RadioButton = findViewById(selectOption)
        Log.i(TAG, "choose gender: ${radioButton.text}") // Data Gender Done

        val phone = "${codeCountryCodePicker.textView_selectedCountry.text}${phoneNumber.text}"
        Log.i(TAG, "Phone number: $phone") // Data Phone Number Done


    }

}