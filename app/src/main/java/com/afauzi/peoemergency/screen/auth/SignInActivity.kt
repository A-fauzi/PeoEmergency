package com.afauzi.peoemergency.screen.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.databinding.ActivitySigninBinding
import com.afauzi.peoemergency.screen.main.MainActivity
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.auth
import com.afauzi.peoemergency.utils.Library.dialogErrors
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.GoogleAuthProvider

class SignInActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SignInActivity"
        private const val GOOGLE_SIGNIN_REQ_CODE = 1046
    }


    private lateinit var binding: ActivitySigninBinding

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var textFieldPassword: TextInputLayout
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var textWarnEmail: TextView
    private lateinit var textWarnPassword: TextView
    private lateinit var linkToSignUp: TextView
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var btnSignInGoogle: CardView

    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private fun initView() {
        email = binding.etEmailSignIn
        password = binding.etPasswordSignIn
        btnLogin = binding.btnLoginSignIn
        progressBar = binding.progressSignIn
        textWarnEmail = binding.tvWarnEmail
        textWarnPassword = binding.tvWarnPassword
        textFieldPassword = binding.outlinedTextFieldPassword
        linkToSignUp = binding.tvLinkToSignUp
        emailInputLayout = binding.outlinedTextFieldEmail
        passwordInputLayout = binding.outlinedTextFieldPassword
        btnSignInGoogle = binding.cvBtnSignInGoogle
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

        configureGso()

    }

    override fun onResume() {
        super.onResume()

        btnLogin.setOnClickListener {
            setFormEnable(false, R.color.input_disabled)
            signInUser()
            progressBar.visibility = View.VISIBLE
        }

        linkToSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnSignInGoogle.setOnClickListener {
            signInGoogle()
        }

//        ========================================== |TextWatcher| ==================================================================
        email.addTextChangedListener(GenericTextWatcher(email))
        password.addTextChangedListener(GenericTextWatcher(password))
//        ========================================== |END| ==================================================================
    }

    private fun configureGso() {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGNIN_REQ_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGNIN_REQ_CODE) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun handleResult(completeTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completeTask.getResult(ApiException::class.java)
            if (account != null) {
                UpdateUi(account)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    object SavedPreference {

        const val EMAIL = "email"
        const val USERNAME = "username"

        private fun getSharedPreference(ctx: Context?): SharedPreferences? {
            return PreferenceManager.getDefaultSharedPreferences(ctx)
        }

        private fun editor(context: Context, const: String, string: String) {
            getSharedPreference(
                context
            )?.edit()?.putString(const, string)?.apply()
        }

        fun getEmail(context: Context) = getSharedPreference(
            context
        )?.getString(EMAIL, "")

        fun setEmail(context: Context, email: String) {
            editor(
                context,
                EMAIL,
                email
            )
        }

        fun setUsername(context: Context, username: String) {
            editor(
                context,
                USERNAME,
                username
            )
        }

        fun getUsername(context: Context) = getSharedPreference(
            context
        )?.getString(USERNAME, "")

    }

    /**
     * Google oAuth handle methode
     */
    private fun UpdateUi(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                Log.i(TAG, "sign in with credential: ${task.exception?.localizedMessage}")

                SavedPreference.setEmail(this, account.email.toString())
                Log.i(
                    TAG,
                    "SavedPreference: ${SavedPreference.setEmail(this, account.email.toString())}"
                )

                SavedPreference.setUsername(this, account.displayName.toString())
                Log.i(
                    TAG,
                    "SavedPreference: ${
                        SavedPreference.setUsername(
                            this,
                            account.displayName.toString()
                        )
                    }"
                )

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                Log.i(TAG, "sign in with credential: ${task.exception?.localizedMessage}")
            }
        }.addOnFailureListener { signInCredentialFailure ->
            Log.i(TAG, "sign in with credential: ${signInCredentialFailure.localizedMessage}")
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }


    /**
     * Methode Handle SignIn User
     */
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
                setFormEnable(true, R.color.white)
                dialogErrors(layoutInflater, this, authResult.exception?.localizedMessage!!)
                progressBar.visibility = View.GONE
            }

        }.addOnFailureListener { authFailure ->
            setFormEnable(true, R.color.white)
            dialogErrors(layoutInflater, this, authFailure.localizedMessage!!)
            progressBar.visibility = View.GONE
        }

    }

    /**
     * Metode Text in EditText to Clear
     *
     * Fungsi untuk membersihkan / menghapus text yang terdapat pada editText
     */
    private fun clearText() {
        email.text.clear()
        password.text.clear()
    }

    /**
     * TextWatcher class to handle validation of editText View
     *
     * Kelas TextWatcher ini digunakan untuk membuat validasi pada form, untuk mengatasi suatu tindakan
     */
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

        /**
         * metohde validasi form
         *
         * Methode input validasi tindakan yang akan dikirimkan kedalam kelas TextWatcher
         */
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
            setCompDrawIsCorrect?.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_correct,
                0
            )
            setCompDrawNotCorrect?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            textWarnVisible?.visibility = View.VISIBLE
            textWarnGone?.visibility = View.INVISIBLE
            inputEnableTrue?.isEnabled = true
            inputEnableFalse?.isEnabled = false
            setBackBoxTextFieldActive?.setBoxBackgroundColorResource(R.color.white)
            setBackBoxTextFieldDisable?.setBoxBackgroundColorResource(R.color.input_disabled)
            requestFocus?.requestFocus()
        }

    }

    /**
     * Methode to handle form enable true or false
     *
     * Sebuah fungsi untuk penanganan sebuah form editText apakah harus enable or disable
     *
     */
    private fun setFormEnable(condition: Boolean, setBackBoxColor: Int) {

        email.isEnabled = condition
        emailInputLayout.setBoxBackgroundColorResource(setBackBoxColor)

        password.isEnabled = condition
        passwordInputLayout.setBoxBackgroundColorResource(setBackBoxColor)
    }
}