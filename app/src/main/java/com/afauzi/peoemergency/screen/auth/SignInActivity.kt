package com.afauzi.peoemergency.screen.auth

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.databinding.ActivitySigninBinding
import com.afauzi.peoemergency.screen.main.MainActivity
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.auth
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class SignInActivity : AppCompatActivity() {


    private lateinit var binding: ActivitySigninBinding

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var textFieldPassword: TextInputLayout
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var textWarnEmail: TextView
    private lateinit var textWarnPassword: TextView
    private lateinit var linkToSignUp: TextView

    private fun initView() {
        email = binding.etEmailSignIn
        password = binding.etPasswordSignIn
        btnLogin = binding.btnLoginSignIn
        progressBar = binding.progressSignIn
        textWarnEmail = binding.tvWarnEmail
        textWarnPassword = binding.tvWarnPassword
        textFieldPassword = binding.outlinedTextFieldPassword
        linkToSignUp = binding.tvLinkToSignUp
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

    }

    override fun onResume() {
        super.onResume()

        btnLogin.setOnClickListener {
            signInUser()
            progressBar.visibility = View.VISIBLE
        }

        linkToSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        email.addTextChangedListener(GenericTextWatcher(email))
        password.addTextChangedListener(GenericTextWatcher(password))

    }

    private fun signInUser() {
        auth.signInWithEmailAndPassword(
            email.text.toString().trim(),
            password.text.toString().trim()
        ).addOnCompleteListener { authResult ->
            if (authResult.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                clearText()
            } else {
                val snackbar = Snackbar.make(
                    binding.root,
                    "${authResult.exception?.message}",
                    Snackbar.LENGTH_SHORT
                )
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
                progressBar.visibility = View.GONE
            }

        }

    }

    private fun clearText() {
        email.text.clear()
        password.text.clear()
    }

    inner class GenericTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(editable: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (view.id) {
                R.id.et_email_sign_in -> {
                    if (text.contains('@')) {
                        inputValidate(
                            setCompDrawIsCorrect = email,
                            textWarnGone = textWarnEmail,
                            inputEnableTrue = password,
                            setBackBoxTextFieldActive = textFieldPassword
                        )
                    } else {
                        inputValidate(
                            setCompDrawNotCorrect = email,
                            textWarnVisible = textWarnEmail,
                            inputEnableFalse = password,
                            setBackBoxTextFieldDisable = textFieldPassword,
                            requestFocus = email
                        )

                    }
                }
                R.id.et_password_sign_in -> {
                    if (text.length < 8) {
                        inputValidate(
                            textWarnVisible = textWarnPassword,
                            setCompDrawNotCorrect = password,
                            inputEnableFalse = btnLogin,
                            requestFocus = password
                        )
                    } else {
                        inputValidate(
                            textWarnGone = textWarnPassword,
                            setCompDrawIsCorrect = password,
                            inputEnableTrue = btnLogin
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
            inputEnableTrue: View? = null,
            inputEnableFalse: View? = null,
            setBackBoxTextFieldActive: TextInputLayout? = null,
            setBackBoxTextFieldDisable: TextInputLayout? = null,
            requestFocus: View? = null
        ) {
            setCompDrawIsCorrect?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_correct, 0)
            setCompDrawNotCorrect?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            textWarnVisible?.visibility = View.VISIBLE
            textWarnGone?.visibility = View.INVISIBLE
            inputEnableTrue?.isEnabled = true
            inputEnableFalse?.isEnabled = false
            setBackBoxTextFieldActive?.setBoxBackgroundColorResource( R.color.white)
            setBackBoxTextFieldDisable?.setBoxBackgroundColorResource( R.color.input_disabled)
            requestFocus?.requestFocus()
        }

    }


}