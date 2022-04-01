package com.afauzi.peoemergency.screen.auth

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.databinding.ActivitySigninBinding
import com.afauzi.peoemergency.screen.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding

    // declaration for firebase authentication
    private lateinit var auth: FirebaseAuth

    private lateinit var email: EditText
    private lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onResume() {
        super.onResume()

        binding.btnLoginSignIn.setOnClickListener {
            validate()
        }

    }

    private fun validate() {
        email = binding.etEmailSignIn
        password = binding.etPasswordSignIn

        if (email.text.toString().trim().isEmpty()) errValidate(email, "email column is required!")
        else if (password.text.toString().trim().isEmpty()) errValidate(password, "password column is required!")
        else {
            if (password.text.toString().length < 8 ) errValidate(password, "password must be 8 character!")
            else {
                binding.progresSignIn.visibility = View.VISIBLE
                signInUser()
            }
        }

    }

    private fun signInUser() {
        email = binding.etEmailSignIn
        password = binding.etPasswordSignIn

        auth = FirebaseAuth.getInstance()

        auth.signInWithEmailAndPassword(email.text.toString().trim(), password.text.toString().trim()).addOnCompleteListener { authResult ->
            if (authResult.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                clearText()
            } else {
                val snackbar = Snackbar.make(binding.root, "${authResult.exception?.message}", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            }

        }

    }

    private fun errValidate(inputView: EditText, msg: String) {
        inputView.error = msg
        inputView.requestFocus()
    }

    private fun clearText() {
        email.text.clear()
        password.text.clear()
    }


}