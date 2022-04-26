package com.afauzi.peoemergency.screen.main.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
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
import androidx.core.R
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
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    private lateinit var imgProfile: CircleImageView
    private lateinit var currentLocation: TextView
    private lateinit var progressLoaderPostContent: LinearProgressIndicator

    private fun initView() {
        layout = binding.mainLayout
        username = binding.username
        inputContentDescPost = binding.etContentDescDialog
        chooseImagePost = binding.inputPhotoDialog
        attachFile = binding.attachFileDialog
        moreMenu = binding.moreMenuDialog
        btnPost = binding.buttonPostDialog
        imageReceiverCapture = binding.ivReceiveImageCapture
        imgProfile = binding.imageProfile
        currentLocation = binding.currentLocation
        progressLoaderPostContent = binding.progressPostContent
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

        // Get current location
        val mFusedLocation = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mFusedLocation.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            val latitude = location?.latitude
            val longitude = location?.longitude
            val locationCoordinate = "$latitude, $longitude"

            val geocoder = Geocoder(requireActivity(), Locale.getDefault())
            val address: Address?
            var fullAddress = ""

            val addresses: List<Address>? = geocoder.getFromLocation(latitude!!, longitude!!, 1)
            if (addresses!!.isNotEmpty()) {
                address = addresses[0]
                fullAddress = address.getAddressLine(0)
                val city = address.locality
                val state = address.adminArea
                val country = address.countryName
                val postalCode = address.postalCode
                val knownName = address.featureName

                currentLocation.text = city

                Log.i(TAG, "FullAddress: $fullAddress")
                Log.i(TAG, "City: $city")
                Log.i(TAG, "State: $state")
                Log.i(TAG, "Country: $country")
                Log.i(TAG, "postal code: $postalCode")
                Log.i(TAG, "knowName: $knownName")


                val uid = auth.currentUser!!.uid
                databaseReference =
                    firebaseDatabase.getReference("users").child(uid).child("currentLocation")
                val hashMap: HashMap<String, String> = HashMap()
                hashMap["fullAddress"] = fullAddress
                hashMap["city"] = city
                hashMap["state"] = state
                hashMap["country"] = country
                hashMap["locationCoordinate"] = locationCoordinate

                databaseReference.setValue(hashMap).addOnCompleteListener { locationResult ->
                    if (locationResult.isSuccessful) {
                        Log.i(TAG, "locationResult saved in database")
                    } else {
                        Log.i(TAG, "locationResult failed ${locationResult.exception?.message}")
                    }
                }.addOnFailureListener { e ->
                    Log.i(TAG, "location not saved in database -> ${e.message}")
                }

            } else {
                Toast.makeText(activity, "location not found", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "location not found")
                currentLocation.text =
                    getString(com.afauzi.peoemergency.R.string.not_detect_current_location)
            }

        }


        // GetData USer
        getUserData()

        // Received Capture Image
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
//                        uploadImageServer()
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
                            val gender = snapshot.child("gender").value.toString()

                            username.text = snapshot.child("username").value.toString()
                            Log.i(TAG, "username: ${username.text}") // data username done
                            val photoUri = snapshot.child("photoProfile").value.toString()

                            if (gender == "Laki-Laki") {
                                Picasso
                                    .get()
                                    .load(photoUri)
                                    .placeholder(com.afauzi.peoemergency.R.drawable.boy_image_place_holder)
                                    .into(imgProfile)
                            } else {
                                Picasso
                                    .get()
                                    .load(photoUri)
                                    .placeholder(com.afauzi.peoemergency.R.drawable.girl_image_place_holder)
                                    .into(imgProfile)
                            }
                            Log.i(TAG, "Photo Uri: $photoUri") // data username done
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

//    val hashMap: HashMap<String, String> = HashMap()
//    hashMap["username"] = username
//    hashMap["photoProfile"] = photoProfile
//    hashMap["postLocation"] = postLocation
//    hashMap["postText"] = postText
//    hashMap["postDate"] = postDate
//    hashMap["imagePost"] = imagePost.toString()

    private fun uploadImageServer() {
        progressLoaderPostContent.visibility = View.VISIBLE

        val imgId = UUID.randomUUID().toString() + ".jpg"
        val uid = auth.currentUser!!.uid
        val imageUri: Uri = requireActivity().intent.extras?.getString("resultCapturePostRandom")!!.toUri()
        Log.i(TAG, "Image Post : $imageUri")
        storageReference = firebaseStorage.reference.child("post_image/${auth.currentUser!!.email}/$imgId")
        storageReference.putFile(imageUri).addOnSuccessListener { taskSnap ->

            var currentProgress = 0
            currentProgress += 10

            progressLoaderPostContent.progress = currentProgress
            progressLoaderPostContent.max = 100

            imageReceiverCapture.setImageResource(0)
            progressLoaderPostContent.visibility = View.INVISIBLE
            Toast.makeText(activity, "Post Success!", Toast.LENGTH_SHORT).show()

            Log.i(TAG, "upload image post: Successful")

            taskSnap.storage.downloadUrl.addOnSuccessListener { uri ->
                Log.i(TAG, "Uri: $uri")
            }.addOnFailureListener { uriFailure ->
                Toast.makeText(activity, "${uriFailure.message}", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener { snapshotFailure ->
            Toast.makeText(activity, "${snapshotFailure.message}", Toast.LENGTH_SHORT).show()
        }


    }
}
