package com.afauzi.peoemergency.screen.main

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController : NavController

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.mainFragment)
        setupSmoothBottomMenu()


        checkLocationPermission()

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
    private fun checkLocationPermission() {
        val locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permission ->
            when{
                permission.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted
                    Toast.makeText(this, "Than", Toast.LENGTH_SHORT).show()
                }
                permission.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted
                    Toast.makeText(this, "Only approximate location access granted", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // No Location access granted
                    Toast.makeText(this, "Lokasi tidak diizinkan", Toast.LENGTH_SHORT).show()
                }
            }
        }
        // Before you perform the actual permission request, check whether your app
        // already has the permissions, and whether your app needs to show a permission
        // rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION))
    }



}