package com.afauzi.peoemergency.screen.auth.register_step

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.databinding.ActivityRegisterDetailProfileFinishBinding
import com.afauzi.peoemergency.screen.main.MainActivity
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.auth
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.databaseReference
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.firebaseDatabase
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.firebaseStorage
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.UserProfileChangeRequest
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class RegisterDetailProfileFinish : AppCompatActivity() {

    companion object {
        const val TAG = "RegisterDetailProfileFinish"
    }

    private lateinit var binding: ActivityRegisterDetailProfileFinishBinding

    private lateinit var btnStepFinish: Button

    private lateinit var tvUsername: TextView
    private lateinit var photoProfileDetail: CircleImageView
    private lateinit var nameProfileDetail: TextView
    private lateinit var emailProfileDetail: TextView
    private lateinit var phoneProfileDetail: TextView
    private lateinit var genderProfileDetail: TextView
    private lateinit var birthdayProfileDetail: TextView
    private lateinit var progressLoader: CircularProgressIndicator

    private fun initView() {
        btnStepFinish = binding.btnStepFinish
        tvUsername = binding.tvUsername
        photoProfileDetail = binding.ivProfileDetail
        nameProfileDetail = binding.nameProfileDetail
        emailProfileDetail = binding.emailDetailProfile
        phoneProfileDetail = binding.phoneDetailProfile
        genderProfileDetail = binding.genderDetailProfile
        birthdayProfileDetail = binding.birthdayDetailProfile
        progressLoader = binding.progressInRegisterFinish
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterDetailProfileFinishBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initView()


    }

    override fun onResume() {
        super.onResume()

        val username = intent.extras?.getString("usernameStep2")
        Log.d(TAG, username.toString())
        tvUsername.text = resources.getString(R.string.hi_name, username)
        nameProfileDetail.text = username

        val imgUri = intent.extras?.getString("imgUriStep2")
        Log.d(TAG, imgUri.toString())
        photoProfileDetail.setImageURI(imgUri?.toUri())

        val email = intent.extras?.getString("emailStep2")
        Log.d(TAG, email.toString())
        emailProfileDetail.text = resources.getString(R.string.email_detail_register, email)

        val phone = intent.extras?.getString("phoneStep2")
        Log.d(TAG, phone.toString())
        phoneProfileDetail.text = resources.getString(R.string.phone_detail_register, phone)

        val gender = intent.extras?.getString("genderStep2")
        Log.d(TAG, gender.toString())
        genderProfileDetail.text = resources.getString(R.string.gender_detail_register, gender)

        val birthday = intent.extras?.getString("userBirthdayStep2")
        Log.d(TAG, birthday.toString())
        birthdayProfileDetail.text = resources.getString(R.string.birthday_detail_register, birthday)

        val password = intent.extras?.getString("passwordStep2")
        Log.d(TAG, password.toString())

        val dateJoin = intent.extras?.getString("dateJoinStep2")
        Log.d(TAG, dateJoin.toString())

        btnStepFinish.setOnClickListener {
            if (email != null && password != null) {
                progressLoader.visibility = View.VISIBLE
                btnStepFinish.visibility = View.INVISIBLE

                uploadImageToFirebase(
                    imgUri!!.toUri(),
                    auth.currentUser!!.uid,
                    username.toString(),
                    email,
                    phone.toString(),
                    gender.toString(),
                    birthday.toString(),
                    dateJoin.toString()
                )
            }
        }

    }

    private fun uploadImageToFirebase(
        fileUri: Uri,
        userId: String,
        username: String,
        email: String,
        phone: String,
        gender: String,
        birthday: String,
        dateJoin: String,
    ) {
        if (fileUri != null) {
            val fileName = UUID.randomUUID().toString() + ".jpg"
            val refStorage =
                firebaseStorage.reference.child("imagesProfile/${auth.currentUser!!.email}/$fileName")
            refStorage.putFile(fileUri).addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    databaseReference = firebaseDatabase.getReference("users").child(userId)

                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap["userId"] = userId
                    hashMap["username"] = username
                    hashMap["email"] = email
                    hashMap["phone"] = phone
                    hashMap["photoProfile"] = uri.toString()
                    hashMap["gender"] = gender
                    hashMap["birthDay"] = birthday
                    hashMap["dateJoin"] = dateJoin

                    databaseReference.setValue(hashMap).addOnCompleteListener { databaseResult ->
                        if (databaseResult.isSuccessful) {
                            Log.i(TAG, "Succesfully save data in database")
                            // Sign in success, update UI with the signed-in user's information
                            val user = auth.currentUser
                            val profileUpdates = UserProfileChangeRequest.Builder().apply {
                                this.photoUri = uri
                            }.build()
                            user?.updateProfile(profileUpdates)?.addOnCompleteListener { updatesProfile ->
                                if (updatesProfile.isSuccessful) {
                                    Log.d(TAG, "Profile Updated")
                                } else {
                                    btnStepFinish.visibility = View.VISIBLE
                                    progressLoader.visibility = View.INVISIBLE
                                    Log.w(TAG, "Profile Not Updated")
                                }
                            }

                            startActivity(Intent(this, MainActivity::class.java))
                        } else {
                            btnStepFinish.visibility = View.VISIBLE
                            progressLoader.visibility = View.INVISIBLE
                            Log.e(TAG, databaseResult.exception?.message.toString())
                        }
                    }.addOnFailureListener { databaseFailure ->
                        btnStepFinish.visibility = View.VISIBLE
                        progressLoader.visibility = View.INVISIBLE
                        Log.e(TAG, databaseFailure.message.toString())
                    }
                }.addOnFailureListener { uriException ->
                    btnStepFinish.visibility = View.VISIBLE
                    progressLoader.visibility = View.INVISIBLE
                    Log.e(TAG, uriException.message.toString())
                }
            }.addOnFailureListener { taskSnapFailure ->
                btnStepFinish.visibility = View.VISIBLE
                progressLoader.visibility = View.INVISIBLE
                Log.e(TAG, taskSnapFailure.message.toString())
            }
        }
    }

}