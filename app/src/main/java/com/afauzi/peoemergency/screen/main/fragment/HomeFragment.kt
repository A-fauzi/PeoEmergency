package com.afauzi.peoemergency.screen.main.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
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
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.adapter.AdapterListRandPost
import com.afauzi.peoemergency.data_model.ModelItemRandomPost
import com.afauzi.peoemergency.databinding.FragmentHomeBinding
import com.afauzi.peoemergency.screen.LandingActivity
import com.afauzi.peoemergency.screen.auth.register_step.RegisterProfileStep2
import com.afauzi.peoemergency.screen.main.fragment.activity.home.CameraAction
import com.afauzi.peoemergency.screen.main.fragment.activity.home.CommentPostRandomActivity
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.auth
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.databaseReference
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.firebaseDatabase
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.firebaseStorage
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.storageReference
import com.afauzi.peoemergency.utils.Library.currentDateAndTime
import com.afauzi.peoemergency.utils.Library.dialogReactions
import com.airbnb.lottie.LottieAnimationView
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment(), AdapterListRandPost.CallClickListener {

    companion object {
        private const val TAG = "HomeFragment"
        private const val REQUEST_CHECK_SETTINGS = 12345
    }

    private lateinit var layout: View
    private lateinit var binding: FragmentHomeBinding
    private lateinit var username: TextView
    private lateinit var inputContentDescPost: TextInputEditText
    private lateinit var chooseImagePost: CardView
    private lateinit var attachFile: CardView
    private lateinit var moreMenu: CardView
    private lateinit var imageReceiverCapture: ImageView
    private lateinit var btnPost: Button
    private lateinit var imgProfile: CircleImageView
    private lateinit var currentLocation: TextView
    private lateinit var progressLoaderPostContent: LinearProgressIndicator
    private lateinit var photoProfileUri: String
    private lateinit var photoPostUri: Uri

    private lateinit var locationManager: LocationManager
    private var gpsStatus = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var rvPostRandom: ShimmerRecyclerView
    private lateinit var listRandomPost: ArrayList<ModelItemRandomPost>
    private lateinit var animationView: LottieAnimationView
    private var fillPath: Uri? = null
    private var postId: String? = null

    private fun initView() {
        layout = binding.mainLayout
        username = binding.username
        inputContentDescPost = binding.etContentDescDialog
        chooseImagePost = binding.inputPhotoDialogCardView
        attachFile = binding.attachFileDialogCardView
        moreMenu = binding.moreMenuDialogCardView
        btnPost = binding.buttonPostDialog
        imageReceiverCapture = binding.ivReceiveImageCapture
        imgProfile = binding.imageProfile
        currentLocation = binding.currentLocation
        progressLoaderPostContent = binding.progressPostContent
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

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initView()

        // Check User
        auth.currentUser.let {
            Log.d(TAG, "Email User: ${it?.email}")
            Log.d(TAG, "Name User: ${it?.displayName}")
            Log.d(TAG, "PhotoUrl: ${it?.photoUrl}")
        }

        // GetData USer
        getUserData()

        // Chcek GPS Status
        checkGpsStatus()

        // Current Location
        getCurrentLocation()

        // Get List Post
        recyclerViewListRandPost()

        return binding.root

    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Received Capture Image
        receiveImagePicture()

        val permissionRequestLocation =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
                // Location Permission
                when {
                    permission.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                        // Precise location access granted
                        Log.i(TAG, "Access location is granted")
                        progressLoaderPostContent.visibility = View.VISIBLE

                        Log.i(TAG, "fill path value $fillPath")

                        when {
                            requireActivity().intent.extras != null -> {
                                Log.d(TAG, "uploadImageServer")
                                uploadImageCaptureServer()
                            }
                            fillPath != null -> {
                                uploadImageGaleryServer()
                            }
                            else -> {
                                storeDataPostInDatabase()
                            }
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
            openGalleryForImage()
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


    private fun checkGpsStatus() {
        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (gpsStatus) {
            Log.i(TAG, "GPS Is enabled")
            Toast.makeText(activity, "Location GPS Is Active", Toast.LENGTH_SHORT).show()
        } else {
            Log.w(TAG, "GPS Is Not enabled")
            turnOnGPS()
        }
    }

    /**
     * Enable gps system
     */
    private fun turnOnGPS() {
        val locationRequest = LocationRequest.create().apply {
            interval = 2000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> =
            settingsClient.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            Log.d(TAG, "Location Settings State : ${it.locationSettingsStates}")
        }
        task.addOnFailureListener {
            if (it is ResolvableApiException) {
                try {
                    it.startResolutionForResult(requireActivity(), REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        Log.d("onResume", gpsStatus.toString())
    }


    private fun getCurrentLocation() {
        // Get current location
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
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
        fusedLocationProviderClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            val latitude = location?.latitude
            val longitude = location?.longitude
            val locationCoordinate = "$latitude, $longitude"
            Log.d(TAG, "Location Coordinate $locationCoordinate")

            if (latitude != null && longitude != null) {
                Log.d(TAG, "Location Coordinate $locationCoordinate")

                val geocoder = Geocoder(requireActivity(), Locale.getDefault())
                val address: Address
                val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)

                address = addresses[0]
                val fullAddress = address.getAddressLine(0)
                val subDistrict = address.locality
                val province = address.adminArea
                val country = address.countryName
                val postalCode = address.postalCode
                val knownName = address.featureName
                val urbanVillage = address.subLocality
                val countryCode = address.countryCode
                val districtOrRegency = address.subAdminArea
                val streetName = address.thoroughfare

                Log.i(TAG, "Urban Village(Kelurahan): $urbanVillage")
                Log.i(TAG, "Country Code(Code Negara): $countryCode")
                Log.i(TAG, "Phone: ${address.phone}")
                Log.i(TAG, "Premises: ${address.premises}")
                Log.i(TAG, "District/Regency(Kota/Kabupaten): $districtOrRegency")
                Log.i(TAG, "subThoroughfare: ${address.subThoroughfare}")
                Log.i(TAG, "Street Name(Nama Jalan): $streetName")
                Log.i(TAG, "Url: ${address.url}")
                Log.i(TAG, "Max Address: ${address.maxAddressLineIndex}")
                Log.i(TAG, "Address: $address")
                Log.i(TAG, "Address: $addresses")
                Log.i(
                    TAG,
                    "======================================================================="
                )

                Log.i(TAG, "FullAddress(Alamat Lengkap): $fullAddress")
                Log.i(TAG, "Sub-District(Kecamatan): $subDistrict")
                Log.i(TAG, "Province(Provinsi): $province")
                Log.i(TAG, "Country(Negara): $country")
                Log.i(TAG, "postal code(Kode Wilayah): $postalCode")
                Log.i(TAG, "knowName: $knownName")

                storeDataLocation(
                    fullAddress,
                    subDistrict,
                    province,
                    country,
                    locationCoordinate = locationCoordinate,
                    urbanVillage,
                    countryCode,
                    districtOrRegency,
                    streetName
                )

            } else {
                Log.d(TAG, "Location Coordinate $locationCoordinate")
            }


        }

    }

    private fun storeDataLocation(
        fullAddress: String ,
        subDistrict: String ,
        province: String ,
        country: String ,
        locationCoordinate: String,
        urbanVillage: String ,
        countryCode: String ,
        districtOrRegency: String ,
        streetName: String ,
    ) {
        val uid = auth.currentUser!!.uid
        databaseReference =
            firebaseDatabase.getReference("users").child(uid).child("currentLocation")
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["fullAddress"] = fullAddress
        hashMap["streetName"] = streetName
        hashMap["urbanVillage"] = urbanVillage
        hashMap["districtOrRegency"] = districtOrRegency
        hashMap["subDistrict"] = subDistrict
        hashMap["province"] = province
        hashMap["country"] = country
        hashMap["countryCode"] = countryCode
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

                username.text = it.displayName
                Log.i(TAG, "username: ${username.text}") // data username done

                Picasso
                    .get()
                    .load(it.photoUrl)
                    .placeholder(R.drawable.boy_image_place_holder)
                    .into(imgProfile)
                Log.i(TAG, "photo Uri: ${it.photoUrl}") // data photoUri done

                databaseReference = firebaseDatabase.getReference("users").child(it.uid)
                databaseReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val streetName = snapshot.child("currentLocation")
                                .child("streetName").value.toString()
                            val urbanVillage = snapshot.child("currentLocation")
                                .child("urbanVillage").value.toString()
                            currentLocation.text = "$streetName, $urbanVillage..."
                            Log.i(
                                TAG,
                                "current location: ${currentLocation.text}"
                            ) // data location done

                        } else {
                            // Code not exist data
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

    private fun uploadImageCaptureServer() {
        progressLoaderPostContent.visibility = View.VISIBLE

        val imgId = UUID.randomUUID().toString() + ".jpg"

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

    private fun receiveImagePicture() {
        if (requireActivity().intent.extras != null) {
            Picasso
                .get()
                .load(photoPostUri)
                .resize(500, 500)
                .centerCrop()
                .into(imageReceiverCapture)

            Log.i(TAG, "image capture received: $photoPostUri")
        }
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, RegisterProfileStep2.REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == RegisterProfileStep2.REQUEST_CODE) {
            fillPath = data?.data!!
            try {
                imageReceiverCapture.setImageURI(data.data) // handle chosen image
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImageGaleryServer() {
        progressLoaderPostContent.visibility = View.VISIBLE

        val imgId = UUID.randomUUID().toString() + ".jpg"

        Log.i(TAG, "Image Post From Galeri : $fillPath")
        storageReference =
            firebaseStorage.reference.child("post_image/${auth.currentUser!!.email}/$imgId")
        storageReference.putFile(fillPath!!).addOnSuccessListener { taskSnap ->

            Log.i(TAG, "upload image post from galery: Successful")

            taskSnap.storage.downloadUrl.addOnSuccessListener { uri ->

                Log.i(TAG, "Uri image post from galery: $uri")
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
                            Log.i(TAG, "dataPostDate: $currentDateAndTime")
                            Log.i(TAG, "dataPostId: $randStr")

                            val hashMap: HashMap<String, String> = HashMap()
                            val user = auth.currentUser
                            hashMap["userId"] = uid
                            hashMap["username"] = user?.displayName.toString()
                            hashMap["photoProfile"] = user?.photoUrl.toString()
                            hashMap["photoPost"] = photoPostUriToDatabase
                            hashMap["postLocationCoordinate"] =
                                snapshot.child("locationCoordinate").value.toString()
                            hashMap["postLocationName"] =
                                snapshot.child("districtOrRegency").value.toString()
                            hashMap["postText"] = inputContentDescPost.text.toString()
                            hashMap["postDate"] =
                                "${SimpleDateFormat("dd MMM yyyy | HH:mm:ss zzz").format(Date(System.currentTimeMillis()))}"
                            hashMap["postId"] = randStr

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

    private fun recyclerViewListRandPost() {
        rvPostRandom = binding.rvPostRandom
        rvPostRandom.setHasFixedSize(true)
        rvPostRandom.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

        listRandomPost = arrayListOf<ModelItemRandomPost>()

        databaseReference = firebaseDatabase.getReference("postRandom")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    listRandomPost.clear()
                    for (listRandPost in snapshot.children.sortedByDescending { it.child("postDate").value.toString() }) {
                        val list = listRandPost.getValue(ModelItemRandomPost::class.java)
                        listRandomPost.add(list!!)

                        animationView.visibility = View.INVISIBLE
                        rvPostRandom.visibility = View.VISIBLE

                        Log.d(
                            TAG,
                            "=============================DATA POST ${list.username}==============================="
                        )
                        Log.i(TAG, "post id: ${list.postId}")
                        Log.i(TAG, "post list size: ${listRandomPost.size}")

                        getCountCommentPost(list.postId.toString())
                        getCountReactPost(list.postId.toString())
                    }
                    rvPostRandom.adapter = AdapterListRandPost(this@HomeFragment, listRandomPost)
                } else {
                    animationView.visibility = View.VISIBLE
                    rvPostRandom.visibility = View.GONE
                    Log.d(TAG, "Data List Random Post: ${listRandomPost.size}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun getCountCommentPost(postId: String) {
        databaseReference =
            firebaseDatabase.getReference("postRandom").child(postId).child("userReplyPost")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val size: String = snapshot.childrenCount.toString()
                Log.i("countComment", "count postId $postId: $size")

                databaseReference = firebaseDatabase.getReference("postRandom").child(postId)
                    .child("countCommentPostUser")
                databaseReference.setValue(size).addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        Log.i(TAG, "Post reply count updated")
                    } else {
                        Log.i(TAG, "Post reply count not updated")
                    }
                }.addOnFailureListener { e ->
                    Log.i(TAG, "failure post reply result database: ${e.localizedMessage} ")
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("countCommentError", "count: ${error.message}")
            }

        })
    }

    private fun getCountReactPost(postId: String) {
        databaseReference =
            firebaseDatabase.getReference("postRandom").child(postId).child("userReact")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val size: String = snapshot.childrenCount.toString()
                Log.i("countReact", "count postId $postId: $size")

                databaseReference = firebaseDatabase.getReference("postRandom").child(postId)
                    .child("countReactPostUser")
                databaseReference.setValue(size).addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        Log.i(TAG, "Post react count updated")
                    } else {
                        Log.i(TAG, "Post react count not updated")
                    }
                }.addOnFailureListener { e ->
                    Log.i(TAG, "failure post react result database: ${e.localizedMessage} ")
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("countReactError", "count: ${error.message}")
            }

        })
    }


    @SuppressLint("InflateParams")
    private fun bottomSheetPostMore(postId: String) {
        val dialog = BottomSheetDialog(requireActivity())

        val view = layoutInflater.inflate(R.layout.modal_bottom_sheet_post_random, null)
        val editPostBottomSheet = view.findViewById<CardView>(R.id.bottom_sheet_item_edit)
        val deletePostBottomSheet = view.findViewById<CardView>(R.id.bottom_sheet_item_delete)
        val cancelPostBottomSheet = view.findViewById<Chip>(R.id.bottom_sheet_post_cancel)

        editPostBottomSheet.setOnClickListener {
            Toast.makeText(activity, "Edit clicked post in post id: $postId", Toast.LENGTH_SHORT)
                .show()
        }

        deletePostBottomSheet.setOnClickListener {
            dialog.dismiss()
            Log.i(TAG, "delete post in post id : $postId")
            databaseReference =
                firebaseDatabase.getReference("postRandom").child(postId).child("userReplyPost")
            databaseReference.removeValue().addOnCompleteListener { removeSubChildPostId ->
                if (removeSubChildPostId.isSuccessful) {
                    databaseReference = firebaseDatabase.getReference("postRandom").child(postId)
                    databaseReference.removeValue().addOnCompleteListener { removeChildPostId ->
                        if (removeChildPostId.isSuccessful) {
                            Toast.makeText(activity, "Post Deleted", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            databaseReference =
                firebaseDatabase.getReference("postRandom").child(postId).child("userReact")
            databaseReference.removeValue().addOnCompleteListener { removeSubChildPostId ->
                if (removeSubChildPostId.isSuccessful) {
                    databaseReference = firebaseDatabase.getReference("postRandom").child(postId)
                    databaseReference.removeValue().addOnCompleteListener { removeChildPostId ->
                        if (removeChildPostId.isSuccessful) {
                            Toast.makeText(activity, "Post Deleted", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        cancelPostBottomSheet.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.setCancelable(false)
        dialog.show()

    }


    override fun onClickListenerCardView(data: ModelItemRandomPost) {
        Log.i(TAG, "username: ${data.username}")
        Log.i(TAG, "User ID: ${data.userId}")
        Log.i(TAG, "Photo Profile: ${data.photoProfile}")
        Log.i(TAG, "Text posting: ${data.postText}")
        Log.i(TAG, "Post ID: ${data.postId}")

        if (data.photoPost != "" || data.photoPost != null) {
            Log.i(TAG, "Photo Posting: ${data.photoPost}")
        }

        Log.i(TAG, "Date Posting: ${data.postDate}")
        Log.i(TAG, "City name: ${data.postLocationName}")
        Log.i(TAG, "Coordinate location: ${data.postLocationCoordinate}")
    }

    override fun onClickListenerPostMore(data: ModelItemRandomPost) {
        Log.i(TAG, "Clicked this post ${data.username}")
        bottomSheetPostMore(data.postId.toString())
    }

    override fun onClickListenerImageView(data: ModelItemRandomPost) {
        Log.i(TAG, "Photo Posting: ${data.photoPost}")
    }

    override fun onClickListenerPostReply(data: ModelItemRandomPost) {

        auth.currentUser.let {
            val bundle = Bundle()
            // Data CurrentUser
            bundle.putString("photoProfileCurrentUser", it?.photoUrl.toString())
            bundle.putString("usernameCurrentUser", it?.displayName)

            bundle.putString("username", "${data.username}")
            bundle.putString("userId", "${data.userId}")
            bundle.putString("photoProfile", "${data.photoProfile}")
            bundle.putString("textPost", "${data.postText}")
            bundle.putString("postId", "${data.postId}")
            if (data.photoPost != "" || data.photoPost != null) {
                bundle.putString("photoPosting", "${data.photoPost}")
            }
            bundle.putString("datePosting", "${data.postDate}")

            val intent = Intent(requireActivity(), CommentPostRandomActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)

        }
    }

    override fun onClickListenerPostReact(data: ModelItemRandomPost) {
        val currentUser = auth.currentUser
        dialogReactions(
            layoutInflater,
            requireActivity(),
            data.postId!!,
            currentUser?.uid.toString(),
            currentUser?.displayName.toString(),
            currentUser?.photoUrl.toString()
        )
    }

    override fun onClickListenerPostShare(data: ModelItemRandomPost) {
        TODO("Not yet implemented")
    }

    override fun onLongClickListener(data: ModelItemRandomPost) {
        TODO("Not yet implemented")
    }

    override fun onClickRemove(data: ModelItemRandomPost) {
        TODO("Not yet implemented")
    }

    private fun randomString(len: Int): String {
        val random = SecureRandom()
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray()
        return (1..len).map { chars[random.nextInt(chars.size)] }.joinToString("")
    }
}
















