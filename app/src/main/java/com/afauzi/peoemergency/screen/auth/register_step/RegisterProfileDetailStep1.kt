package com.afauzi.peoemergency.screen.auth.register_step

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var tvWarnPhoneInput: TextView

    private fun initView() {
        btnNextStep = binding.btnStep1
        tvUsername = binding.username
        radioGroup = binding.enableRadioGroup
        codeCountryCodePicker = binding.codeCountryPicker
        phoneNumber = binding.phoneNumber
        tvWarnPhoneInput = binding.tvWarnEtPhoneInput
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterProfileDetailStep1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()

        Log.i(TAG, intent.extras?.getString("username").toString())
        Log.i(TAG, intent.extras?.getString("email").toString())
        Log.i(TAG, intent.extras?.getString("password").toString())
        Log.i(TAG, intent.extras?.getString("dateJoin").toString())

        tvUsername.text = resources.getString(R.string.hi_name, intent.extras?.getString("username"))

    }

    override fun onResume() {
        super.onResume()

        btnNextStep.setOnClickListener {
            passingData()
        }

        phoneNumber.addTextChangedListener(GenericTextWatcher(phoneNumber))

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

    inner class GenericTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun afterTextChanged(editable: Editable?) {
            val text = editable.toString()
            when (view.id) {
                R.id.phoneNumber -> {
                    when {
                        text.startsWith("0") -> {
                          inputValidateErr(
                              getString(R.string.tv_warning_number_input_not_empty),
                              View.VISIBLE,
                              0,
                              false
                          )
                        }
                        text.isEmpty() -> {
                           inputValidateErr(
                               getString(R.string.tv_warning_number_input_not_empty),
                               View.VISIBLE,
                               0,
                               false
                           )
                        }
                        else -> {
                            inputValidateErr(
                                viewVisibilityTextErr = View.INVISIBLE,
                                setCompDrawRightPosition = R.drawable.ic_correct,
                                btnEnable = true
                            )
                        }
                    }
                }
            }
        }
        private fun inputValidateErr(textErr: String = "", viewVisibilityTextErr: Int, setCompDrawRightPosition: Int, btnEnable: Boolean) {
            tvWarnPhoneInput.visibility = viewVisibilityTextErr
            tvWarnPhoneInput.text = textErr
            phoneNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, setCompDrawRightPosition, 0)
            btnNextStep.isEnabled = btnEnable
        }
    }

}