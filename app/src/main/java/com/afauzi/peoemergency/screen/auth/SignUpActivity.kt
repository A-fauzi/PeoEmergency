package com.afauzi.peoemergency.screen.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import com.afauzi.peoemergency.databinding.ActivitySignUpBinding
import com.afauzi.peoemergency.screen.main.MainActivity
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.auth
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.databaseReference
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.firebaseDatabase
import com.afauzi.peoemergency.utils.Library.TAG
import com.afauzi.peoemergency.utils.Library.currentDate
import com.google.android.material.snackbar.Snackbar
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
    private lateinit var username: EditText

    /**
     * Declaration editText email
     */
    private lateinit var email: EditText

    /**
     * Declaration editText password
     */
    private lateinit var password: EditText

    /**
     * Declaration editText password confirmation
     */
    private lateinit var passwordConfirm: EditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Todo: Initials viewBinding to signup_activity.xml
        binding = ActivitySignUpBinding.inflate(layoutInflater)

        // Todo: setContentView untuk layout root
        setContentView(binding.root)

    }

    override fun onResume() {
        super.onResume()

             // Todo: Behavior if button onClick
            binding.btnRegisterSignUp.setOnClickListener {
            Log.i(TAG, "button register clicked")
            // Todo: Handle SignUp user
            signUpUser()

        }

    }

    /**
     * Function root handle user to register, have scope function in this function
     */
    private fun signUpUser() {

        /**
         * Result if validation error
         */
        fun errValidate(inputView: EditText, msg: String) {
            inputView.error = msg
            inputView.requestFocus()
        }

        /**
         * Methode to text remove or text clear in form
         */
        fun clearText() {
            Log.i(TAG, "input clear text")
            username.text.clear()
            email.text.clear()
            password.text.clear()
            passwordConfirm.text.clear()
        }

        /**
         * Handle user to signup account
         */
        fun signUpUsers() {

            //Todo: Initials editText username
            username = binding.etUsernameSignUp

            //Todo: Initials editText email
            email = binding.etEmailSignUp

            //Todo: Initials editText password
            password = binding.etPasswordSignUp

            // Todo: Mendaftarkan user dengan methode yang sudah disediakan firebase auth, yaitu createUserWithEmailPassword()
            auth.createUserWithEmailAndPassword(

                // Todo: Dalam tindakan ini email dan password yang akan di gunakan dan disimpan kedalam kredentials firebaseAuth
                email.text.toString().trim(),
                password.text.toString().trim()

            ).addOnCompleteListener { authResult ->

                //Todo: Tindakan didalam lambda yang akan menghandle penddaftaran akun
                // saat memanggil methode addOnCompleteListener() maka akan mengembalikan authResult

                // Todo: memberikan kondisi untuk menangani handle authResult
                when {

                    //Todo: handle tindakan jika authResult sukses didalam lambda
                    authResult.isSuccessful -> {

                        // Todo: Informasikan hasil kedalam log
                        Log.i(TAG, "success signup authResult: ${authResult.exception?.message}")

                        /**
                         * User dari hasil auth.currentUser, adalah data user yang telah login
                         */
                        val user: FirebaseUser? = auth.currentUser

                        /**
                         * Mendapatkan id user berdasarkan user dari auth.currentUser
                         */
                        val uid = user?.uid

                        // Todo: initials databaseReference untuk mendapatkan reference dari firebaseDatabase / RealtimeDatabase
                        databaseReference = firebaseDatabase.getReference("users").child(uid!!)

                        /**
                         * Deklarasi hashMap untuk mengirimkan data key,value yang akan dijadikan struktur data pada realtime database
                         *
                         * key : Sebagai reference child key
                         *
                         * value: Sebagai value atau nilai data pada database
                         */
                        val hashMap: HashMap<String, String> = HashMap()
                        hashMap["username"] = username.text.toString().trim()
                        hashMap["email"] = email.text.toString().trim()
                        hashMap["date_join"] = currentDate

                        // Todo: methode setValue() dari databaseReference untuk mengirim data untuk dijadikan object json didalam struktur database
                        databaseReference.setValue(hashMap)
                            // Todo: dalam methode addOnCompleteListener() akan menghandle beberapa tindakan yang mengembalikan databaseResult
                            .addOnCompleteListener(this) { databaseResult ->

                                // Todo: Memberikan kondisi dan beberapa behaviour jika databaseResult Sukses
                                if (databaseResult.isSuccessful) {

                                    // Todo: Informasikan hasil kedalam log
                                    Log.i(TAG, "success signup databaseResult : ${databaseResult.exception?.message}")

                                    // Todo: Informasikan hasil kedalam snackbar
                                    Snackbar.make(
                                        binding.root,
                                        "yeeyy account created, happy to join :)",
                                        Snackbar.LENGTH_SHORT
                                    ).setBackgroundTint(Color.GREEN).show()

                                    /**
                                     * Declaration Intent
                                     */
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()

                                    //Todo: Call clearTextMethode
                                    clearText()

                                }
                                // Todo: Memberikan kondisi dan beberapa behaviour jika databaseResult Not Success
                                else {

                                    // Todo: Informasikan hasil kedalam log
                                    Log.e(TAG, "failed signup databaseResult : ${databaseResult.exception?.message}")

                                    /**
                                     * Deklarasi progress loading
                                     */
                                    val progressLoader = binding.progressInSignUp
                                    // Todo: Sembunyikan progress loader
                                    progressLoader.visibility = View.GONE

                                    // Todo: Menampilkan info pada snackbar
                                    Snackbar.make(
                                        binding.root,
                                        "${databaseResult.exception?.message}",
                                        Snackbar.LENGTH_SHORT
                                    ).setBackgroundTint(Color.RED).show()

                                }

                            }.addOnFailureListener(this) { databaseResult ->
                                // Todo: Informasikan hasil kedalam log
                                Log.e(TAG, "failed signup databasefailure : ${databaseResult.message}")

                                val progressLoader = binding.progressInSignUp
                                progressLoader.visibility = View.GONE

                                Snackbar.make(
                                    binding.root,
                                    "${databaseResult.message}",
                                    Snackbar.LENGTH_SHORT
                                ).setBackgroundTint(Color.RED).show()
                            }

                    }
                }
            }.addOnFailureListener(this) { authExcep ->
                // Todo: Informasikan hasil kedalam log
                Log.e(TAG, "failed signup auth failure  : ${authExcep.message}")

                val progressLoader = binding.progressInSignUp
                progressLoader.visibility = View.GONE

                Snackbar.make(binding.root, "${authExcep.message}", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.RED).show()
            }

        }

        fun validate() {
            username = binding.etUsernameSignUp
            email = binding.etEmailSignUp
            password = binding.etPasswordSignUp
            passwordConfirm = binding.etPasswordConfirmSignUp

            when {
                username.text.toString().trim().isEmpty() -> {
                    Log.i(TAG, "username validation signup is empty")
                    errValidate(username, "username column is required!")
                }
                email.text.toString().trim().isEmpty() -> {
                    Log.i(TAG, "email validation signup is empty")
                    errValidate(email, "email column is required!")
                }
                password.text.toString().trim().isEmpty() -> {
                    Log.i(TAG, "password validation signup is empty")
                    errValidate(password, "password column is required!")
                }
                passwordConfirm.text.toString().trim().isEmpty() -> {
                    Log.i(TAG, "password confirmation validation signup is empty")
                    errValidate(passwordConfirm, "password confirmation column is required!")
                }
                else -> {

                    when {
                        password.text.toString().length < 8 -> {
                            Log.i(TAG, "password signup tidak kurang dari 9 character")
                            errValidate(password, "password must be 8 character!")
                        }
                        passwordConfirm.text.toString().trim() != password.text.toString().trim() -> {
                            Log.i(TAG, "password dan password confirmation signup tidak sama")
                            errValidate(passwordConfirm, "password confirmation must be equals with password!")
                        }
                        else -> {
                            Log.i(TAG, "success validation form signup user")
                            binding.progressInSignUp.visibility = View.VISIBLE
                            signUpUsers()
                        }
                    }

                }
            }

        }
        validate()
    }
}