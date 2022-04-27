package com.afauzi.peoemergency.screen.main.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.afauzi.peoemergency.adapter.AdapterListRandPost
import com.afauzi.peoemergency.dataModel.ModelItemRandomPost
import com.afauzi.peoemergency.databinding.FragmentHomeBinding
import com.afauzi.peoemergency.screen.LandingActivity
import com.afauzi.peoemergency.screen.main.fragment.activity.home.CameraAction
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.auth
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.databaseReference
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.firebaseDatabase
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.firebaseStorage
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.storageReference
import com.airbnb.lottie.LottieAnimationView
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.google.android.gms.location.LocationServices
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment(), AdapterListRandPost.CallClickListener {

    companion object {
        private const val TAG = "HomeFragment"
        const val REQUEST_CODE = 200
    }

    private lateinit var layout: View
    private lateinit var binding: FragmentHomeBinding
    private lateinit var username: TextView
    private lateinit var inputContentDescPost: TextInputEditText
    private lateinit var chooseImagePost: ImageView
    private lateinit var attachFile: ImageView
    private lateinit var moreMenu: ImageView
    private lateinit var imageReceiverCapture: ImageView
    private lateinit var btnPost: Button
    private lateinit var imgProfile: CircleImageView
    private lateinit var currentLocation: TextView
    private lateinit var progressLoaderPostContent: LinearProgressIndicator
    private lateinit var postId: UUID
    private lateinit var photoProfileUri: String
    private lateinit var photoPostUri: Uri
    private lateinit var rvPostRandom: ShimmerRecyclerView
    private lateinit var listRandomPost: ArrayList<ModelItemRandomPost>
    private lateinit var animationView: LottieAnimationView

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
        postId = UUID.randomUUID()
        photoProfileUri = ""
        if (requireActivity().intent.extras != null) {
            photoPostUri =
                requireActivity().intent.extras?.getString("resultCapturePostRandom")!!.toUri()
        }
        animationView = binding.animationView
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

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // GetData USer
        getUserData()

        // get current Location
        getCurrentLocation()

        // Get List Post
        recyclerViewListRandPost()


        // Received Capture Image
        if (requireActivity().intent.extras != null) {
            Picasso
                .get()
                .load(photoPostUri)
                .resize(500, 500)
                .centerCrop()
                .into(imageReceiverCapture)

            Log.i(TAG, "image capture received: $photoPostUri")
        }

        val permissionRequestLocation =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
                // Location Permission
                when {
                    permission.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                        // Precise location access granted
                        Log.i(TAG, "Access location is granted")
                        progressLoaderPostContent.visibility = View.VISIBLE

                        Log.i(
                            TAG,
                            "Coba time: ${
                                SimpleDateFormat("dd MMM yyyy | hh:mm:ss zzz").format(
                                    Date(System.currentTimeMillis())
                                )
                            }"
                        )

                        if (requireActivity().intent.extras != null) {
                            Log.d(TAG, "uploadImageServer")
                            uploadImageServer()
                        } else {
                            Log.d(TAG, "storeDataPostInDatabase")
                            storeDataPostInDatabase()
                        }

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

    private fun getCurrentLocation() {
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

            val addresses: List<Address>? = geocoder.getFromLocation(latitude!!, longitude!!, 1)
            if (addresses!!.isNotEmpty()) {
                address = addresses[0]
                val fullAddress = address.getAddressLine(0)
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

                storeDataLocation(fullAddress, city, state, country, locationCoordinate)

            }

        }

    }

    private fun storeDataLocation(
        fullAddress: String,
        city: String,
        state: String,
        country: String,
        locationCoordinate: String
    ) {
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
                            photoProfileUri = snapshot.child("photoProfile").value.toString()

                            if (gender == "Laki-Laki") {
                                Picasso
                                    .get()
                                    .load(photoProfileUri)
                                    .placeholder(com.afauzi.peoemergency.R.drawable.boy_image_place_holder)
                                    .into(imgProfile)
                            } else {
                                Picasso
                                    .get()
                                    .load(photoProfileUri)
                                    .placeholder(com.afauzi.peoemergency.R.drawable.girl_image_place_holder)
                                    .into(imgProfile)
                            }
                            Log.i(TAG, "Photo Profile Uri: $photoProfileUri") // data username done
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

    private fun uploadImageServer() {
        progressLoaderPostContent.visibility = View.VISIBLE

        val imgId = UUID.randomUUID().toString() + ".jpg"
//        val uid = auth.currentUser!!.uid

        Log.i(TAG, "Image Post : $photoPostUri")
        storageReference =
            firebaseStorage.reference.child("post_image/${auth.currentUser!!.email}/$imgId")
        storageReference.putFile(photoPostUri).addOnSuccessListener { taskSnap ->

            Log.i(TAG, "upload image post: Successful")

            taskSnap.storage.downloadUrl.addOnSuccessListener { uri ->

                Log.i(TAG, "Uri image post: $uri")
                storeDataPostInDatabase(uri.toString())

            }.addOnFailureListener { uriFailure ->
                Toast.makeText(activity, "${uriFailure.message}", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener { snapshotFailure ->
            Toast.makeText(activity, "${snapshotFailure.message}", Toast.LENGTH_SHORT).show()
        }


    }

    private fun storeDataPostInDatabase(photoPostUriToDatabase: String = "") {
        // get user location
        auth.currentUser.let {
            if (it != null) {
                databaseReference =
                    firebaseDatabase.getReference("users").child(it.uid).child("currentLocation")
                databaseReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val uid = auth.currentUser!!.uid
                            val randStr = randomString(25)

                            databaseReference =
                                firebaseDatabase.getReference("postRandom").child(randStr)

                            Log.i(TAG, "dataPostUid: $uid")
                            Log.i(TAG, "dataPostUsername: ${username.text}")
                            Log.i(TAG, "dataPostPhotoProfile: $photoProfileUri")
                            Log.i(TAG, "dataPostImage: $photoPostUriToDatabase")
                            Log.i(
                                TAG,
                                "dataPostLocCoordinate: ${snapshot.child("locationCoordinate").value.toString()}"
                            )
                            Log.i(
                                TAG,
                                "dataPostLocCityName: ${snapshot.child("fullAddress").value.toString()}"
                            )
                            Log.i(TAG, "dataPostText: ${inputContentDescPost.text}")
                            Log.i(
                                TAG,
                                "dataPostDate: ${
                                    SimpleDateFormat("dd MMM yyyy | hh:mm:ss zzz").format(
                                        Date(
                                            System.currentTimeMillis()
                                        )
                                    )
                                }"
                            )
                            Log.i(TAG, "dataPostId: $randStr")

                            val hashMap: HashMap<String, String> = HashMap()
                            hashMap["userId"] = uid
                            hashMap["username"] = username.text.toString()
                            hashMap["photoProfile"] = photoProfileUri
                            hashMap["photoPost"] = photoPostUriToDatabase
                            hashMap["postLocationCoordinate"] =
                                snapshot.child("locationCoordinate").value.toString()
                            hashMap["postLocationCityName"] =
                                snapshot.child("fullAddress").value.toString()
                            hashMap["postText"] = inputContentDescPost.text.toString()
                            hashMap["postDate"] =
                                "${SimpleDateFormat("dd MMM yyyy | hh:mm:ss zzz").format(Date(System.currentTimeMillis()))}"
                            hashMap["postId"] = postId.toString()

                            databaseReference.setValue(hashMap)
                                .addOnCompleteListener { postResult ->
                                    if (postResult.isSuccessful) {
                                        Log.i(TAG, "data post saved in database")
                                        inputContentDescPost.text?.clear()
                                        imageReceiverCapture.setImageResource(0)
                                        progressLoaderPostContent.visibility = View.INVISIBLE
                                        Toast.makeText(
                                            activity,
                                            "Post Success!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        requireActivity().intent.removeExtra("resultCapturePostRandom")

                                    } else {
                                        Log.i(
                                            TAG,
                                            "data post not saved in database: ${postResult.exception?.message}"
                                        )
                                    }
                                }.addOnFailureListener { postResultFailure ->
                                Log.i(
                                    TAG,
                                    "data post to database failure: ${postResultFailure.message}"
                                )
                            }

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

    private fun randomString(len: Int): String {
        val random = SecureRandom()
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray()
        return (1..len).map { chars[random.nextInt(chars.size)] }.joinToString("")
    }

    private fun recyclerViewListRandPost() {
        rvPostRandom = binding.rvPostRandom
        listRandomPost = arrayListOf()
        rvPostRandom.setHasFixedSize(true)
        rvPostRandom.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        getListPostRandom()
    }

    private fun getListPostRandom() {
        val uid = auth.currentUser!!.uid
        databaseReference = firebaseDatabase.getReference("postRandom")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    listRandomPost.clear()
                    for (listRandPost in snapshot.children.sortedByDescending { it.child("postDate").value.toString() }) {
                        val list = listRandPost.getValue(ModelItemRandomPost::class.java)
                        listRandomPost.add(list!!)
                        animationView.visibility = View.INVISIBLE

                    }
                    rvPostRandom.adapter = AdapterListRandPost(this@HomeFragment, listRandomPost)
                } else {
                    animationView.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    override fun onClickListener(data: ModelItemRandomPost) {
        TODO("Not yet implemented")
    }

    override fun onLongClickListener(data: ModelItemRandomPost) {
        TODO("Not yet implemented")
    }

    override fun onClickRemove(data: ModelItemRandomPost) {
        TODO("Not yet implemented")
    }

}
















