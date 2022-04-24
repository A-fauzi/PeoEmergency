package com.afauzi.peoemergency.screen.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
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
import com.afauzi.peoemergency.screen.main.fragment.activity.accountProfile.EditProfileActivity
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.auth
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.databaseReference
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.firebaseDatabase
import com.afauzi.peoemergency.utils.Library.TAG
import com.afauzi.peoemergency.utils.Library.clearText
import com.afauzi.peoemergency.utils.Library.currentDate
import com.afauzi.peoemergency.utils.Library.dialogErrors
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseUser
import kotlin.collections.HashMap

@SuppressLint("SimpleDateFormat")
class SignUpActivity : AppCompatActivity() {

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
            signUpUsers()
        }

        linkToSignIn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        username.addTextChangedListener(GenericTextWatcher(username))
        email.addTextChangedListener(GenericTextWatcher(email))
        password.addTextChangedListener(GenericTextWatcher(password))
        passwordConfirm.addTextChangedListener(GenericTextWatcher(passwordConfirm))

    }

    private fun signUpUsers() {
        auth.createUserWithEmailAndPassword(email.text.toString().trim(), password.text.toString().trim()).addOnCompleteListener { authResult ->
            when {
                authResult.isSuccessful -> {
                    Log.i(TAG, "success signup authResult: ${authResult.exception?.message}")

                    val user: FirebaseUser? = auth.currentUser

                    val uid = user?.uid

                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap["username"] = username.text.toString().trim()
                    hashMap["email"] = email.text.toString().trim()
                    hashMap["date_join"] = currentDate

                    databaseReference = firebaseDatabase.getReference("users").child(uid!!)

                    databaseReference.setValue(hashMap)
                        .addOnCompleteListener(this) { databaseResult ->

                            if (databaseResult.isSuccessful) {

                                Log.i(
                                    TAG,
                                    "success signup databaseResult : ${databaseResult.exception?.message}"
                                )

                                Snackbar.make(
                                    binding.root,
                                    "yeeyy account created, happy to join :)",
                                    Snackbar.LENGTH_SHORT
                                ).setBackgroundTint(Color.GREEN).show()

                                val intent = Intent(this, RegisterProfileDetailStep1::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()

                                clearText(username)
                                clearText(email)
                                clearText(password)
                                clearText(passwordConfirm)

                            } else {

                                Log.e(
                                    TAG,
                                    "failed signup databaseResult : ${databaseResult.exception?.message}"
                                )

                                setFormEnable(true, R.color.white)

                                progressBar.visibility = View.INVISIBLE

                                dialogErrors(layoutInflater, this, databaseResult.exception?.localizedMessage!!)

                            }

                        }.addOnFailureListener(this) { databaseResult ->
                            Log.e(
                                TAG,
                                "failed signup databasefailure : ${databaseResult.message}"
                            )
                            setFormEnable(true, R.color.white)

                            progressBar.visibility = View.INVISIBLE

                            dialogErrors(layoutInflater, this, databaseResult.localizedMessage!!)
                        }

                }
            }
        }.addOnFailureListener(this) { authExcep ->
            Log.e(TAG, "failed signup auth message  : ${authExcep.message}")
            setFormEnable(true, R.color.white)

            progressBar.visibility = View.INVISIBLE

            dialogErrors(layoutInflater, this, getString(R.string.txt_error_network))
        }

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