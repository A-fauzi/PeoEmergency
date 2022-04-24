package com.afauzi.peoemergency.screen.main.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.afauzi.peoemergency.databinding.FragmentHomeBinding
import com.afauzi.peoemergency.screen.LandingActivity
import com.afauzi.peoemergency.screen.main.fragment.activity.home.CameraAction
import com.afauzi.peoemergency.utils.FirebaseServiceInstance
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.auth
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.databaseReference
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.firebaseDatabase
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.firebaseStorage
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.storageReference
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.user
import com.afauzi.peoemergency.utils.Library
import com.afauzi.peoemergency.utils.Library.currentDateTime
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class HomeFragment : Fragment() {

    companion object {
        private const val TAG = "HomeFragment"
        const val REQUEST_CODE = 200
    }

    private lateinit var layout: View
    private lateinit var binding: FragmentHomeBinding
    private lateinit var username: TextView
    private lateinit var inputContentDescPost: EditText
    private lateinit var chooseImagePost: ImageView
    private lateinit var attachFile: ImageView
    private lateinit var moreMenu: ImageView
    private lateinit var imageReceiverCapture: ImageView
    private lateinit var btnPost: Button

    private fun initView() {
        layout = binding.mainLayout
        username = binding.usernameInDialog
        inputContentDescPost = binding.etContentDescDialog
        chooseImagePost = binding.inputPhotoDialog
        attachFile = binding.attachFileDialog
        moreMenu = binding.moreMenuDialog
        btnPost = binding.buttonPostDialog
        imageReceiverCapture = binding.ivReceiveImageCapture
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initView()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // GetData USer
//        getUserData()

        if (requireActivity().intent.extras != null) {
            Picasso
                .get()
                .load(requireActivity().intent.extras?.getString("resultCapturePostRandom"))
                .resize(500, 500)
                .centerCrop()
                .into(imageReceiverCapture)
        }

        val permissionRequestLocation =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
                // Location Permission
                when {
                    permission.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                        // Precise location access granted
                        Log.i(TAG, "Access location is granted")
                    }
                    permission.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                        // Only approximate location access granted
                        Log.i(TAG, "Access location is Only approximate")
                    }
                    else -> {
                        // No Location access granted
                        Snackbar.make(
                            binding.root,
                            "to access location, please turn on location  permission",
                            Snackbar.LENGTH_SHORT
                        )
                            .setBackgroundTint(Color.RED)
                            .show()
                        Log.i(TAG, "Access location not permission and is denied")
                    }
                }
            }

        val permissionRequestCamera =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {
                    startActivity(Intent(requireActivity(), CameraAction::class.java))
                    requireActivity().finish()
                    Log.i(TAG, "Access camera is granted")
                } else {
                    Snackbar.make(
                        binding.root,
                        "to access camera, please turn on camera permission",
                        Snackbar.LENGTH_SHORT
                    )
                        .setBackgroundTint(Color.RED)
                        .show()
                    Log.i(TAG, "Access camera is denied")
                }
            }

        // Btn Choose Image on Camera
        chooseImagePost.setOnClickListener {
            // Launch the popup permission
            permissionRequestCamera.launch(
                Manifest.permission.CAMERA
            )
        }

        // Btn attachFile
        attachFile.setOnClickListener {
            Toast.makeText(activity, "Attach file clicked", Toast.LENGTH_SHORT).show()
        }

        // Btn More Menu
        moreMenu.setOnClickListener {
            Toast.makeText(activity, "More Menu clicked", Toast.LENGTH_SHORT).show()
        }

        // Btn Post
        btnPost.setOnClickListener {
            storeData()
            // Launch the popup permission
            permissionRequestLocation.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

    }

    private fun getUserData() {
        // Code get username from database
        auth.currentUser.let {
            if (it != null) {
                databaseReference = firebaseDatabase.getReference("users").child(it.uid)
                databaseReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            username.text = snapshot.child("username").value.toString()
                            Log.i(TAG, "username: ${username.text}") // data username done
                        } else {
                            username.text = null
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Snackbar.make(
                            binding.root,
                            "Sorry, Error: ${error.message}",
                            Snackbar.LENGTH_SHORT
                        ).setBackgroundTint(Color.RED).show()
                    }
                })
            } else {
                Toast.makeText(activity, "User not authentication", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireActivity(), LandingActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun getCurrentLocation(locationCoordinate: String) {
        Log.i(TAG, "location: $locationCoordinate") // get Data location coordinate done
    }

    private fun setUpDatabase(
        username: String,
        photoProfile: String,
        postLocation: String,
        postText: String,
        postDate: String,
        imagePost: Uri
    ) {
        val uid = auth.currentUser!!.uid
        val postId = UUID.randomUUID().toString()
        val referencePath = "postRandom"
        databaseReference = firebaseDatabase.getReference(referencePath).child(uid).child(postId)

        val hashMap: HashMap<String, String> = HashMap()
        hashMap["username"] = username
        hashMap["photoProfile"] = photoProfile
        hashMap["postLocation"] = postLocation
        hashMap["postText"] = postText
        hashMap["postDate"] = postDate
        hashMap["imagePost"] = imagePost.toString()

        databaseReference.setValue(hashMap).addOnCompleteListener {
            if (it.isSuccessful) {
                uploadImageServer(uid, imagePost, postId)
                Toast.makeText(activity, "Data Post Success in Saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Data Post Failed Saved", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(activity, "${it.message}", Toast.LENGTH_SHORT).show()
        }

    }

    private fun uploadImageServer(childUid: String,imageUri: Uri,  childPostId: String) {
        val imgId = UUID.randomUUID().toString()
        val referencePath = "postRandom"

        storageReference = firebaseStorage.reference
        storageReference.child("post_image/$imgId")
        storageReference.putFile(imageUri).addOnSuccessListener { taskSnap ->
            taskSnap.storage.downloadUrl.addOnSuccessListener { uri ->
                databaseReference = firebaseDatabase.getReference(referencePath).child(childUid).child(childPostId).child("imagePost")
                databaseReference.setValue(uri.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.i(TAG, "upload: Successful")
                    } else {
                        Log.i(TAG, "upload: Errors")
                    }
                }.addOnFailureListener {
                    Toast.makeText(activity, "${it.message}", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(activity, "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(activity, "${it.message}", Toast.LENGTH_SHORT).show()
        }

    }

    private fun storeData() {
        // Get Location coordinate
        val mFusedLocation = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }



        /**
         * Menerima inputan description
         */
        val receiveInputDesc = inputContentDescPost.text
        Log.i(TAG, "content desc: $receiveInputDesc") // data description done
        Log.i(TAG, "date post: $currentDateTime") // current Date Post Done
        val bundle = requireActivity().intent.extras
        Log.i(TAG, "image uri: ${bundle?.getString("resultCapturePostRandom")}") // image uri done

//        val getImage: File = Uri.fromFile(bundle?.getString("resultCapturePostRandom").toString())

        mFusedLocation.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            val latitude = location?.latitude
            val longitude = location?.longitude
            val locationCoordinate = "$latitude, $longitude"
            getCurrentLocation(locationCoordinate)

            setUpDatabase(
                username.text.toString(),
                "",
                locationCoordinate,
                receiveInputDesc.toString(),
                currentDateTime,
                bundle?.getString("resultCapturePostRandom")!!.toUri()
            )

        }

        Library.clearText(inputContentDescPost)

    }
}
