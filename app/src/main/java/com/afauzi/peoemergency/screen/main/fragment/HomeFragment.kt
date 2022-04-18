package com.afauzi.peoemergency.screen.main.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.databinding.FragmentHomeBinding
import com.afauzi.peoemergency.screen.LandingActivity
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.auth
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.databaseReference
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.firebaseDatabase
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.user
import com.afauzi.peoemergency.utils.Library
import com.afauzi.peoemergency.utils.Library.currentDate
import com.afauzi.peoemergency.utils.Library.currentDateTime
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*

class HomeFragment : Fragment(){

    companion object {
        const val TAG = "HomeFragment"
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Code get username from database
        auth.currentUser.let {
            if (it != null) {
                databaseReference = firebaseDatabase.getReference("users").child(it.uid)
                databaseReference.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            username.text = snapshot.child("username").value.toString()
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

        // Get Location coordinate
        val mFusedLocation = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
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


        /**
         * Menerima inputan description
         */
        val receiveInputDesc = inputContentDescPost.text

        // Btn Choose Image on Camera
        chooseImagePost.setOnClickListener {
            onClickRequestPermission(binding.root)
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

            mFusedLocation.lastLocation.addOnSuccessListener(requireActivity()
            ) { location ->
                val latitude = location?.latitude
                val longitude = location?.longitude
                val locationCoordinate = "$latitude, $longitude"
                Log.i(TAG, "location: $locationCoordinate") // get Data location coordinate done
            }

            Log.i(TAG, "username: ${username.text}") // data username done
            Log.i(TAG, "content desc: $receiveInputDesc") // data description done
            Log.i(TAG, "date post: $currentDateTime") // current Date Post Done

            Library.clearText(inputContentDescPost)
        }
    }

    fun View.showSnackbar(
        view: View,
        msg: String,
        length: Int,
        actionMessage: CharSequence?,
        acttion: (View) -> Unit
    ) {
        val snackbar = Snackbar.make(view, msg, length)
        if (actionMessage != null) {
            snackbar.setAction(actionMessage) {
                acttion(this)
            }.show()
        } else {
            snackbar.show()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            Log.i(TAG, "Permission: Granted")
        } else {
            Log.i(TAG, "Permission: Denied")
        }
    }

    fun onClickRequestPermission(view: View) {
        when {
            ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
//                layout.showSnackbar(view, getString(R.string.permission_granted), Snackbar.LENGTH_INDEFINITE, null
//                ) {}
                capturePhoto()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA) -> {
                layout.showSnackbar(view, getString(R.string.permission_required), Snackbar.LENGTH_INDEFINITE, getString(R.string.ok)
                ) {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun capturePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && data != null) {
            imageReceiverCapture.setImageBitmap(data.extras?.get("data") as Bitmap)
        }
    }

}
