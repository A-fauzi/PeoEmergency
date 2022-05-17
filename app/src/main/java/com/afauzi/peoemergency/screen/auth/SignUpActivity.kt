package com.afauzi.peoemergency.screen.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.databinding.ActivitySignUpBinding
import com.afauzi.peoemergency.screen.auth.register_step.RegisterDetailProfileFinish
import com.afauzi.peoemergency.screen.auth.register_step.RegisterProfileDetailStep1
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.auth
import com.afauzi.peoemergency.utils.Library.currentDateAndTime
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.UserProfileChangeRequest

@SuppressLint("SimpleDateFormat")
class SignUpActivity : AppCompatActivity() {

    companion object {
        /**
         * constant variable to TAG
         */
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

    /**
     * Declaration TextInputLayout username
     */
    private lateinit var usernameInputLayout: TextInputLayout

    /**
     * Declaration editText email
     */
    private lateinit var email: TextInputEditText

    /**
     * Declaration TextInputLayout email
     */
    private lateinit var emailInputLayout: TextInputLayout

    /**
     * Declaration editText password
     */
    private lateinit var password: TextInputEditText

    /**
     * Declaration TextInputLayout password
     */
    private lateinit var passwordInputLayout: TextInputLayout

    /**
     * Declaration editText password confirmation
     */
    private lateinit var passwordConfirm: TextInputEditText

    /**
     * Declaration TextInputLayout password confirmation
     */
    private lateinit var passwordConfirmInputLayout: TextInputLayout

    /**
     * Declaration Text Warning username
     */
    private lateinit var textWarnUsername: TextView

    /**
     * Declaration Text Warning email
     */
    private lateinit var textWarnEmail: TextView

    /**
     * Declaration Text Warning password
     */
    private lateinit var textWarnPassword: TextView

    /**
     * Declaration Text Warning password confirmation
     */
    private lateinit var textWarnPasswordConfirm: TextView

    /**
     * Declaration btn register
     */
    private lateinit var btnRegister: Button

    /**
     * Declaration text link to move sign in activity
     */
    private lateinit var linkToSignIn: TextView

    private lateinit var progressIndicator: CircularProgressIndicator

    /**
     * Initials view
     */
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
        emailInputLayout = binding.outlinedTextFieldEmail
        passwordInputLayout = binding.outlinedTextFieldPass
        passwordConfirmInputLayout = binding.outlinedTextFieldPassConfirm
        linkToSignIn = binding.tvLinkToSignIn
        usernameInputLayout = binding.outlinedTextFieldUsername
        progressIndicator = binding.progressInSignup
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Initials views
        initView()
    }

    override fun onResume() {
        super.onResume()

        // btn register to behavior
        btnRegister.setOnClickListener {
            setFormEnable(false, R.color.input_disabled)
            progressIndicator.visibility = View.VISIBLE
            btnRegister.visibility = View.INVISIBLE
            createUserEmailPassword()
        }

        // link to move sign in activity
        linkToSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        // =============================== Add Text Watcher On EditText ===========================================
        username.addTextChangedListener(GenericTextWatcher(username))
        email.addTextChangedListener(GenericTextWatcher(email))
        password.addTextChangedListener(GenericTextWatcher(password))
        passwordConfirm.addTextChangedListener(GenericTextWatcher(passwordConfirm))
        // =============================== End ===========================================

    }


    private fun createUserEmailPassword() {
        auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString()).addOnCompleteListener(this) {  taskSuccessfuly ->
            if (taskSuccessfuly.isSuccessful) {
                Log.d(TAG, "Success Create Email Password")

                // Sign in success, update UI with the signed-in user's information
                val user = auth.currentUser
                val profileUpdates = UserProfileChangeRequest.Builder().apply {
                    this.displayName = username.text.toString()
                }.build()
                user?.updateProfile(profileUpdates)?.addOnCompleteListener { updatesProfile ->
                    if (updatesProfile.isSuccessful) {
                        Log.d(RegisterDetailProfileFinish.TAG, "Profile Updated")
                        passingData()
                    } else {
                        Log.w(RegisterDetailProfileFinish.TAG, "Profile Not Updated")
                    }
                }
            } else {
                // Handle Jika Gagal
                progressIndicator.visibility = View.INVISIBLE
                btnRegister.visibility = View.VISIBLE
                Log.w(TAG, "Not Success Create Email Password : ${taskSuccessfuly.exception?.localizedMessage}")
            }
        }.addOnFailureListener {
            progressIndicator.visibility = View.INVISIBLE
            btnRegister.visibility = View.VISIBLE
            Log.w(TAG, "Failure Create Email Password : ${it.localizedMessage}")
        }
    }

    /**
     * Handle to passing data on second activity
     *
     * Fungsi untuk passing data
     */
    private fun passingData() {

        // ======================================= Passing data on bundle ==================================
        /**
         * Declaration variable Bundle()
         *
         * Mengirim sebuah data dengan bundles
         */
        val bundle = Bundle()

        auth.currentUser.let {
            bundle.putString("username", it?.displayName)
            Log.d(TAG, it?.displayName.toString())

            bundle.putString("email", it?.email)
            Log.d(TAG, it?.email.toString())

            bundle.putString("password", password.text.toString())
            Log.d(TAG, password.text.toString())

            bundle.putString("dateJoin", currentDateAndTime)
            Log.d(TAG, currentDateAndTime)
        }
        // ======================================= End ==================================

        // ====================================== Intent Main ===========================
        /**
         * Intent Action Target to second class activity
         *
         * Intent untuk mengarahkan aktifitas yang dituju dengan membawa data menggunakan bundle
         */
        val intent = Intent(this, RegisterProfileDetailStep1::class.java)

        // data passing on bundle to passing in activity target
        intent.putExtras(bundle)

        startActivity(intent)

        // ====================================== End ===================================

    }

    /**
     * GenericTextWatcher class to views editText in this activity
     *
     * Kelas ini adalah kelas untuk mengelola validasi yang dikirimkan melalui input form menggunakan TextWatcher
     */
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

        /**
         * Validate on ediText to use in GenericTextWatcher class
         *
         *
         * Kejadian di formulir saat formulir di validasi dengan TextWatcher
         */
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

    /**
     * Handle form enabling condition
     *
     * Mengatasi formulir aktif atau tidak
     */
    private fun setFormEnable(condition: Boolean, setBackBoxColor: Int) {
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