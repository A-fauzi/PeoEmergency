package com.afauzi.peoemergency.screen.main.fragment.activity.account_profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.databinding.ActivityEditProfileBinding
import com.afauzi.peoemergency.screen.auth.SignInActivity
import com.afauzi.peoemergency.screen.main.fragment.ProfileAccountFragment
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.auth
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.firebaseDatabase
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.user
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    companion object {
        const val TAG = "EditProfileActivity"
    }

    private lateinit var binding: ActivityEditProfileBinding

    private lateinit var photoProfile: ImageView
    private lateinit var username: EditText
    private lateinit var userEmail: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText

    private lateinit var textJoinDate: TextView
    private lateinit var btnUpdate: Button

    private fun initView() {
        photoProfile = binding.ivPhotoProfileUpdate
        username = binding.etUsernameSignUp
        userEmail = binding.etEmailSignUp
        password = binding.etPasswordSignUp
        confirmPassword = binding.etPasswordConfirmSignUp
        textJoinDate = binding.tvDateJoin
        btnUpdate = binding.btnUpdateProfile
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()

        auth.currentUser.let { currentUser ->
            if (currentUser != null) {
                Picasso
                    .get()
                    .load(currentUser.photoUrl)
                    .placeholder(R.drawable.boy_image_place_holder)
                    .into(photoProfile)
                Log.i(TAG, "photo Uri: ${currentUser.photoUrl}") // data photo Uri done
                firebaseDatabase.getReference("users").child(user!!.uid).addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val name = snapshot.child("username").value.toString()
                            val email = snapshot.child("email").value.toString()
                            val textJoin = snapshot.child("dateJoin").value.toString()
                            username.setText(name)
                            userEmail.setText(email)
                            password.setText("12345678")
                            confirmPassword.setText("12345678")
                            textJoinDate.text = textJoin

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            } else {
                auth.signOut()
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        binding.ivArrowBack.setOnClickListener {
            onBackPressed()
            finish()
        }
    }

}