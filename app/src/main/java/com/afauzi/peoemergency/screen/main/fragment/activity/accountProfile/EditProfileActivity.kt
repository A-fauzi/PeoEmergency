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

    private lateinit var usernameEdit: EditText

    private lateinit var fillPathProfileImage: Uri
    private lateinit var fillPathCoverImage: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()

        getDataChangeOnEdit()

        binding.imageCoverEditProfile.setOnClickListener { selectImage(PICK_IMAGE_REQUEST_COVER) }
        binding.photoProfileEditProfile.setOnClickListener { selectImage(PICK_IMAGE_REQUEST_PROFILE) }

        binding.btnSaveEditProfile.setOnClickListener {
            saveEditData()
            startActivity(Intent(this, MainActivity::class.java))
        }

    }

    private fun getDataChangeOnEdit() {
        usernameEdit = binding.etUsernameEditProfile


        user = auth.currentUser!!
        val uid = user.uid

        databaseReference = firebaseDatabase.getReference("users").child(uid)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    usernameEdit.setText(snapshot.child("username").value.toString())
                    binding.email.text = snapshot.child("email").value.toString()
                    binding.dateJoin.text = snapshot.child("date_join").value.toString()
                } else {
                    // this code
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun selectImage(requestCode: Int) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Image From Here"),
            requestCode
        )

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST_PROFILE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            fillPathProfileImage = data.data!!
            try {
                val bitmap: Bitmap =
                    MediaStore.Images.Media.getBitmap(this.contentResolver, fillPathProfileImage)
                binding.photoProfileEditProfile.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else if (requestCode == PICK_IMAGE_REQUEST_COVER && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            fillPathCoverImage = data.data!!
            try {
                val bitmap: Bitmap =
                    MediaStore.Images.Media.getBitmap(this.contentResolver, fillPathCoverImage)
                binding.imageCoverEditProfile.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST_COVER: Int = 22
        private const val PICK_IMAGE_REQUEST_PROFILE: Int = 21
    }


    private fun saveEditData() {

        user = auth.currentUser!!
        val userId = user.uid

        databaseReference = firebaseDatabase.getReference("users").child(userId)

        val hashMap: HashMap<String, String> = HashMap()
        hashMap["username"] = binding.etUsernameEditProfile.text.toString().trim()
        hashMap["email"] = binding.email.text.toString()
        hashMap["date_join"] = binding.dateJoin.text.toString()

        databaseReference.setValue(hashMap).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Data kamu diperbarui", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(
                    this,
                    "Maaf Ada Kesalahan Penyimpanan data, Ini akan segera diperbaiki",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        storageReference = firebaseStorage.reference

        val uuid = UUID.randomUUID().toString()
        val storeRef: StorageReference = storageReference.child("profile_image/$uuid")
        storeRef.putFile(fillPathProfileImage).addOnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->

                databaseReference = firebaseDatabase.getReference("users").child(userId).child("photo_profile")
                databaseReference.setValue(uri.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Photo profile berhasil deperbarui!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            "error upload!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                 }
            }
        }.addOnFailureListener { e ->
            print(e.message)
            Toast.makeText(this, "Failed ${e.message}", Toast.LENGTH_SHORT).show()
        }

    }

}