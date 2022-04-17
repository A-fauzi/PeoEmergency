package com.afauzi.peoemergency.screen.main.fragment.activity.accountProfile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.Toast
import com.afauzi.peoemergency.databinding.ActivityEditProfileBinding
import com.afauzi.peoemergency.screen.main.MainActivity
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.auth
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.databaseReference
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.firebaseDatabase
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.firebaseStorage
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.storageReference
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.user
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}