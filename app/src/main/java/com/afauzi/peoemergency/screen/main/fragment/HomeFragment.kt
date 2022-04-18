package com.afauzi.peoemergency.screen.main.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
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
    private lateinit var fabAddPost: ExtendedFloatingActionButton

    private fun initView() {
        fabAddPost = binding.fabAddPostRandom
        layout = binding.mainLayout
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

        fabAddPost.setOnClickListener {
            showBottomDialogPost()
        }

    }

    @SuppressLint("InflateParams")
    private fun showBottomDialogPost(){
        val dialog =  BottomSheetDialog(requireActivity())
        val view = layoutInflater.inflate(R.layout.dialog_post, null)
        val photoProfile = view.findViewById<ImageView>(R.id.image_profile_dialog)
        val username = view.findViewById<TextView>(R.id.username_in_dialog)
        val btnPost = view.findViewById<Button>(R.id.button_post_dialog)
        val inputContentDescPost = view.findViewById<EditText>(R.id.et_content_desc_dialog)
        val chooseImagePost = view.findViewById<ImageView>(R.id.input_photo_dialog)
        val attachFile = view.findViewById<ImageView>(R.id.attach_file_dialog)
        val moreMenu = view.findViewById<ImageView>(R.id.more_menu_dialog)

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

            dialog.dismiss()

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


        dialog.setContentView(view)
        dialog.setCancelable(true)
        dialog.show()

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

    fun capturePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_CODE)
    }

}
