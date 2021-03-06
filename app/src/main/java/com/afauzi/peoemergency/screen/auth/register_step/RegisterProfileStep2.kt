package com.afauzi.peoemergency.screen.auth.register_step

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.databinding.ActivityRegisterProfileStep2Binding
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException
import java.text.SimpleDateFormat
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
    private lateinit var btnDatePicker: Chip
    private lateinit var tvDateReceived: TextView
    private lateinit var ivSetImageProfile: CircleImageView
    private lateinit var fillPath: Uri

    private fun initView() {
        btnStep2 = binding.btnStep2
        tvUsername = binding.username
        btnChooseImage = binding.btnChipChoosePhoto
        ivSetImageProfile = binding.ivSetPhotoProfile
        btnDatePicker = binding.btnChipDatePicker
        tvDateReceived = binding.tvReceivedBirthday
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterProfileStep2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()

        val username = intent.extras?.getString("usernameStep1")
        Log.d(TAG, username.toString())

        val gender = intent.extras?.getString("genderStep1")
        Log.d(TAG, gender.toString())

        val phone = intent.extras?.getString("phoneStep1")
        Log.d(TAG, phone.toString())

        val email = intent.extras?.getString("emailStep1")
        Log.d(TAG, email.toString())

        val password = intent.extras?.getString("passwordStep1")
        Log.d(TAG, password.toString())

        val dateJoin = intent.extras?.getString("dateJoinStep1")
        Log.d(TAG, dateJoin.toString())

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
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            fillPath = data?.data!!
            try {
                ivSetImageProfile.setImageURI(data.data) // handle chosen image
                passingData(fillPath)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun passingData(fileUri: Uri) {
        val username = intent.extras?.getString("usernameStep1")
        Log.d(TAG, username.toString())

        val gender = intent.extras?.getString("genderStep1")
        Log.d(TAG, gender.toString())

        val phone = intent.extras?.getString("phoneStep1")
        Log.d(TAG, phone.toString())

        val email = intent.extras?.getString("emailStep1")
        Log.d(TAG, email.toString())

        val password = intent.extras?.getString("passwordStep1")
        Log.d(TAG, password.toString())

        val dateJoin = intent.extras?.getString("dateJoinStep1")
        Log.d(TAG, dateJoin.toString())

        val imgProfileUri = fileUri.toString()

        btnDatePicker.setOnClickListener {
            datePickerDialog()
        }

        btnStep2.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("usernameStep2", username)
            bundle.putString("genderStep2", gender)
            bundle.putString("phoneStep2", phone)
            bundle.putString("emailStep2", email)
            bundle.putString("passwordStep2", password)
            bundle.putString("dateJoinStep2", dateJoin)
            bundle.putString("imgUriStep2", imgProfileUri)
            bundle.putString("userBirthdayStep2", "${tvDateReceived.text}")

            val intent = Intent(this, RegisterDetailProfileFinish::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun datePickerDialog() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener {
            val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            utc.timeInMillis = it
            val format = SimpleDateFormat("dd MMM yyyy")
            val formatted: String = format.format(utc.time)
            tvDateReceived.text = formatted

        }
        datePicker.show(supportFragmentManager, TAG)
    }

}