package com.afauzi.peoemergency.screen.auth.registerStep

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.databinding.ActivityRegisterProfileDetailStep1Binding
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

        tvUsername.text = resources.getString(R.string.hi_name, intent.extras?.getString("username"))

    }

    override fun onResume() {
        super.onResume()

        btnNextStep.setOnClickListener {
            passingData()
        }

    }

    private fun passingData() {
        val selectOption: Int = radioGroup.checkedRadioButtonId
        val radioButton: RadioButton = findViewById(selectOption)
        val dataGender = radioButton.text
        val phone = "${codeCountryCodePicker.textView_selectedCountry.text}${phoneNumber.text}"
        val userName = intent.extras?.getString("username")
        val email = intent.extras?.getString("email")
        val password = intent.extras?.getString("password")
        val dateJoin = intent.extras?.getString("dateJoin")

        val bundle = Bundle()
        bundle.putString("genderStep1", "$dataGender")
        bundle.putString("phoneStep1", phone)
        bundle.putString("usernameStep1", userName)
        bundle.putString("emailStep1", email)
        bundle.putString("passwordStep1", password)
        bundle.putString("dateJoinStep1", dateJoin)

        val intent = Intent(this, RegisterProfileStep2::class.java)
        intent.putExtras(bundle)
        startActivity(intent)

    }

}