package com.afauzi.peoemergency.screen.auth.registerStep

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.databinding.ActivityRegisterProfileDetailStep1Binding
import com.afauzi.peoemergency.databinding.ActivityRegisterProfileStep2Binding
import com.afauzi.peoemergency.screen.main.fragment.HomeFragment
import com.afauzi.peoemergency.screen.main.fragment.activity.home.CameraAction
import com.afauzi.peoemergency.utils.FirebaseServiceInstance
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.storageReference
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException
import java.util.*

class RegisterProfileStep2 : AppCompatActivity() {

    companion object {
        const val TAG = "RegisterProfileStep2"
        const val REQUEST_CODE = 100
    }

    private lateinit var binding: ActivityRegisterProfileStep2Binding
    private lateinit var btnStep2: Button
    private lateinit var tvUsername: TextView
    private lateinit var btnChooseImage: Chip
    private lateinit var ivSetImageProfile: CircleImageView
    private lateinit var fillPath: Uri

    private fun initView() {
        btnStep2 = binding.btnStep2
        tvUsername = binding.username
        btnChooseImage = binding.btnChipChoosePhoto
        ivSetImageProfile = binding.ivSetPhotoProfile

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterProfileStep2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()

        tvUsername.text =
            resources.getString(R.string.hi_name, intent.extras?.getString("usernameStep1"))
    }

    override fun onResume() {
        super.onResume()

        btnChooseImage.setOnClickListener {
            openGalleryForImage()
        }

    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
            fillPath = data?.data!!
            try {
                ivSetImageProfile.setImageURI(data.data) // handle chosen image
                passingData(fillPath)
            }catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun passingData(fileUri: Uri) {
        val username = intent.extras?.getString("usernameStep1")
        val gender = intent.extras?.getString("genderStep1")
        val phone = intent.extras?.getString("phoneStep1")
        val email = intent.extras?.getString("emailStep1")
        val password = intent.extras?.getString("passwordStep1")
        val dateJoin = intent.extras?.getString("dateJoinStep1")
        val imgProfileUri = fileUri.toString()

        btnStep2.setOnClickListener {
           val bundle = Bundle()
            bundle.putString("usernameStep2", username)
            bundle.putString("genderStep2", gender)
            bundle.putString("phoneStep2", phone)
            bundle.putString("emailStep2", email)
            bundle.putString("passwordStep2", password)
            bundle.putString("dateJoinStep2", dateJoin)
            bundle.putString("imgUriStep2", imgProfileUri)

            val intent = Intent(this, RegisterDetailProfileFinish::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }

    }

}