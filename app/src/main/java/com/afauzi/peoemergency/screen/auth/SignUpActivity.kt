package com.afauzi.peoemergency.screen.auth

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.databinding.ActivitySignUpBinding
import com.afauzi.peoemergency.screen.auth.registerStep.RegisterProfileDetailStep1
import com.afauzi.peoemergency.utils.Library.currentDate
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlin.collections.HashMap

@SuppressLint("SimpleDateFormat")
class SignUpActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SignUpActivity"
    }

    /**
     * Declaration viewBinding
     */
    private lateinit var binding: ActivitySignUpBinding

    /**
     * Declaration editText username
     */
    private lateinit var username: TextInputEditText
    private lateinit var usernameInputLayout: TextInputLayout

    /**
     * Declaration editText email
     */
    private lateinit var email: TextInputEditText
    private lateinit var emailInputLayout: TextInputLayout

    /**
     * Declaration editText password
     */
    private lateinit var password: TextInputEditText
    private lateinit var passwordInputLayout: TextInputLayout

    /**
     * Declaration editText password confirmation
     */
    private lateinit var passwordConfirm: TextInputEditText
    private lateinit var passwordConfirmInputLayout: TextInputLayout

    private lateinit var textWarnUsername: TextView
    private lateinit var textWarnEmail: TextView
    private lateinit var textWarnPassword: TextView
    private lateinit var textWarnPasswordConfirm: TextView
    private lateinit var btnRegister: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var linkToSignIn: TextView

    private fun initView() {
        username = binding.etUsernameSignUp
        email = binding.etEmailSignUp
        password = binding.etPasswordSignUp
        passwordConfirm = binding.etPasswordConfirmSignUp
        textWarnUsername = binding.tvWarnUsername
        textWarnEmail = binding.tvWarnEmail
        textWarnPassword = binding.tvWarnPassword
        textWarnPasswordConfirm = binding.tvWarnPasswordConfirmation
        btnRegister = binding.btnRegisterSignUp
        progressBar = binding.progressInSignUp
        emailInputLayout = binding.outlinedTextFieldEmail
        passwordInputLayout = binding.outlinedTextFieldPass
        passwordConfirmInputLayout = binding.outlinedTextFieldPassConfirm
        linkToSignIn = binding.tvLinkToSignIn
        usernameInputLayout = binding.outlinedTextFieldUsername
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initView()
    }

    override fun onResume() {
        super.onResume()

        btnRegister.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            setFormEnable(false, R.color.input_disabled)
            passingData()
        }

        linkToSignIn.setOnClickListener {
            finish()
        }

        username.addTextChangedListener(GenericTextWatcher(username))
        email.addTextChangedListener(GenericTextWatcher(email))
        password.addTextChangedListener(GenericTextWatcher(password))
        passwordConfirm.addTextChangedListener(GenericTextWatcher(passwordConfirm))

    }

    private fun passingData() {

        val bundle = Bundle()
        bundle.putString("username", username.text.toString())
        bundle.putString("email", email.text.toString())
        bundle.putString("password", password.text.toString())
        bundle.putString("dateJoin", currentDate)

        val intent = Intent(this, RegisterProfileDetailStep1::class.java)
        intent.putExtras(bundle)
        startActivity(intent)

    }

    inner class GenericTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(editable: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (view.id) {
                R.id.et_username_sign_up -> {
                    when {
                        text.length >= 5 -> {
                            inputValidate(
                                setCompDrawIsCorrect = username,
                                textWarnGone = textWarnUsername,
                                viewEnableTrue = email,
                                setBackBoxTextFieldActive = emailInputLayout
                            )
                        }
                        text.length >= 0 -> {
                            inputValidate(
                                textWarnVisible = textWarnUsername,
                                setCompDrawNotCorrect = username,
                                viewEnableFalse = email,
                                setBackBoxTextFieldDisable = emailInputLayout,
                                requestFocus = username
                            )
                        }
                        else -> {
                            inputValidate(
                                textWarnGone = textWarnUsername,
                                setCompDrawNotCorrect = username,
                                viewEnableFalse = email,
                                setBackBoxTextFieldDisable = emailInputLayout
                            )
                        }
                    }
                }
                R.id.et_email_sign_up -> {
                    if (text.contains("@gmail.com")) {
                        inputValidate(
                            setCompDrawIsCorrect = email,
                            textWarnGone = textWarnEmail,
                            viewEnableTrue = password,
                            setBackBoxTextFieldActive = passwordInputLayout
                        )
                    } else {
                        inputValidate(
                            setCompDrawNotCorrect = email,
                            textWarnVisible = textWarnEmail,
                            viewEnableFalse = password,
                            setBackBoxTextFieldDisable = passwordInputLayout,
                            requestFocus = email
                        )
                    }
                }
                R.id.et_password_sign_up -> {
                    if (text.length < 8) {
                        inputValidate(
                            textWarnVisible = textWarnPassword,
                            setCompDrawNotCorrect = password,
                            viewEnableFalse = passwordConfirm,
                            setBackBoxTextFieldDisable = passwordConfirmInputLayout,
                            requestFocus = password
                        )
                    } else {
                        inputValidate(
                            textWarnGone = textWarnPassword,
                            setCompDrawIsCorrect = password,
                            viewEnableTrue = passwordConfirm,
                            setBackBoxTextFieldActive = passwordConfirmInputLayout
                        )
                    }
                }
                R.id.et_password_confirm_sign_up -> {
                    if (text == password.text.toString()) {
                        inputValidate(
                            textWarnGone = textWarnPasswordConfirm,
                            setCompDrawIsCorrect = passwordConfirm,
                            viewEnableTrue = btnRegister
                        )
                    } else {
                        inputValidate(
                            textWarnVisible = textWarnPasswordConfirm,
                            setCompDrawNotCorrect = passwordConfirm,
                            viewEnableFalse = btnRegister,
                            requestFocus = passwordConfirm
                        )
                    }
                }
            }
        }

        private fun inputValidate(
            setCompDrawIsCorrect: EditText? = null,
            setCompDrawNotCorrect: EditText? = null,
            textWarnVisible: View? = null,
            textWarnGone: View? = null,
            viewEnableTrue: View? = null,
            viewEnableFalse: View? = null,
            setBackBoxTextFieldActive: TextInputLayout? = null,
            setBackBoxTextFieldDisable: TextInputLayout? = null,
            requestFocus: View? = null
        ) {
            setCompDrawIsCorrect?.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_correct,
                0
            )
            setCompDrawNotCorrect?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            textWarnVisible?.visibility = View.VISIBLE
            textWarnGone?.visibility = View.INVISIBLE
            viewEnableTrue?.isEnabled = true
            viewEnableFalse?.isEnabled = false
            setBackBoxTextFieldActive?.setBoxBackgroundColorResource(R.color.white)
            setBackBoxTextFieldDisable?.setBoxBackgroundColorResource(R.color.input_disabled)
            requestFocus?.requestFocus()
        }

    }

    private fun setFormEnable(condition: Boolean, setBackBoxColor: Int){
            username.isEnabled = condition
            usernameInputLayout.setBoxBackgroundColorResource(setBackBoxColor)

            email.isEnabled = condition
            emailInputLayout.setBoxBackgroundColorResource(setBackBoxColor)

            password.isEnabled = condition
            passwordInputLayout.setBoxBackgroundColorResource(setBackBoxColor)

            passwordConfirm.isEnabled = condition
            passwordConfirmInputLayout.setBoxBackgroundColorResource(setBackBoxColor)
    }
}