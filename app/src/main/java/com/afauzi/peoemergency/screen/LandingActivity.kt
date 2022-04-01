package com.afauzi.peoemergency.screen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.databinding.ActivityLandingBinding
import com.afauzi.peoemergency.screen.auth.SignInActivity
import com.afauzi.peoemergency.screen.auth.SignUpActivity
import com.afauzi.peoemergency.screen.main.MainActivity
import com.google.firebase.auth.FirebaseAuth

class LandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Splashscreen Api Wajib dipanggil untuk dijalankan dan ditampilkan sebelum activity launcher di tampilkan
        installSplashScreen()

        binding = ActivityLandingBinding.inflate(layoutInflater)
        // Lalu konten base akan ditampilkan setelah splashcreen api berakhir
        setContentView(binding.root)

    }

    override fun onResume() {
        super.onResume()

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        else Toast.makeText(this, "please login to access dashboard", Toast.LENGTH_SHORT).show()


        binding.btnToSignInActivity.setOnClickListener { startActivity(Intent(this, SignInActivity::class.java)) }
        binding.btnToSignUpActivity.setOnClickListener { startActivity(Intent(this, SignUpActivity::class.java)) }

    }

}