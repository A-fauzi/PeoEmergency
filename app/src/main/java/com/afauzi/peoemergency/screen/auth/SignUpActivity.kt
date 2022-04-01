package com.afauzi.peoemergency.screen.auth

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.afauzi.peoemergency.databinding.ActivitySignUpBinding
import com.afauzi.peoemergency.screen.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private lateinit var username: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var passwordConfirm: EditText

    /**
     * declaration for firebase authentication
     */
    private lateinit var auth: FirebaseAuth

    /**
     * declaration for firebase realtimedatabase
     */
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onResume() {
        super.onResume()

        binding.btnRegisterSignUp.setOnClickListener {
            validate()
        }

    }

    private fun validate() {
        username = binding.etUsernameSignUp
        email = binding.etEmailSignUp
        password = binding.etPasswordSignUp
        passwordConfirm = binding.etPasswordConfirmSignUp

        if (username.text.toString().trim().isEmpty()) errValidate(username, "username column is required!")
        else if (email.text.toString().trim().isEmpty()) errValidate(email, "email column is required!")
        else if (password.text.toString().trim().isEmpty()) errValidate(password, "password column is required!")
        else if (passwordConfirm.text.toString().trim().isEmpty()) errValidate(passwordConfirm, "password confirmation column is required!")
        else {

            if (password.text.toString().length < 8 ) errValidate(password, "password must be 8 character!")
            else if (passwordConfirm.text.toString().trim() != password.text.toString().trim()) errValidate(passwordConfirm, "password confirmation must be equals with password!")
            else {
                binding.progressInSignUp.visibility = View.VISIBLE
                signUpUsers()
           }

        }

    }

    private fun errValidate(inputView: EditText, msg: String) {
        inputView.error = msg
        inputView.requestFocus()
    }

    private fun clearText() {
        username.text.clear()
        email.text.clear()
        password.text.clear()
        passwordConfirm.text.clear()
    }

    private fun signUpUsers() {
        username = binding.etUsernameSignUp
        email = binding.etEmailSignUp
        password = binding.etPasswordSignUp

        auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email.text.toString().trim(), password.text.toString().trim()).addOnCompleteListener { authResult ->
            when {
                authResult.isSuccessful -> {
                    val user: FirebaseUser? = auth.currentUser
                    val uid = user?.uid

                    databaseReference = FirebaseDatabase.getInstance().getReference("users").child(uid!!)

                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap["user_id"] = uid
                    hashMap["username"] = username.text.toString().trim()
                    hashMap["email"] = email.text.toString().trim()

                    databaseReference.setValue(hashMap).addOnCompleteListener(this) { databaseResult ->

                        if (databaseResult.isSuccessful) {
                            Snackbar.make(binding.root, "success registry for user", Snackbar.LENGTH_SHORT).setBackgroundTint(Color.GREEN).show()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                            clearText()
                        } else {
                            binding.progressInSignUp.visibility = View.GONE
                            Snackbar.make(binding.root, "failed registry for user", Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show()
                            databaseResult.exception.toString()
                        }

                    }.addOnFailureListener(this) { databaseExcep ->
                        binding.progressInSignUp.visibility = View.GONE
                        Snackbar.make(binding.root, "${databaseExcep.message}", Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show()
                    }

                }
            }
        }.addOnFailureListener(this) { authExcep ->
            binding.progressInSignUp.visibility = View.GONE
            Snackbar.make(binding.root, "${authExcep.message}", Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED).show()
        }

    }


}