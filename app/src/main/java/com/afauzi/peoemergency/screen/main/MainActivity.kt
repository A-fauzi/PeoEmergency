package com.afauzi.peoemergency.screen.main

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController : NavController

    private lateinit var layout: View

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.mainFragment)
        setupSmoothBottomMenu()

        layout = binding.mainLayout

        checkMultiplePermission()

    }

    private fun setupSmoothBottomMenu() {
        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.bottom_nav_menu)
        val menu = popupMenu.menu
        binding.bottomBar.setupWithNavController(menu, navController)
        binding.bottomBar.onItemSelected = {
           // Item nav clicked
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun checkMultiplePermission() {
        val applicationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permission ->
            when{
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
                    Log.i(TAG, "Access location not permission and is denied")
                }
            }

            if ( permission.getOrDefault(Manifest.permission.CAMERA, false)) {
                // Precise camera access granted
                Log.i(TAG, "Access camera is granted")
            } else {
                Log.i(TAG, "Access camera is denied")
            }
        }
        // Before you perform the actual permission request, check whether your app
        // already has the permissions, and whether your app needs to show a permission
        // rationale dialog. For more details, see Request permissions.
        applicationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
        ))
    }
}