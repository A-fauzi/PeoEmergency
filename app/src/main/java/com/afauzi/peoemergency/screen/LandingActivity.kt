package com.afauzi.peoemergency.screen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.afauzi.peoemergency.databinding.ActivityLandingBinding
import com.afauzi.peoemergency.screen.auth.SignInActivity
import com.afauzi.peoemergency.screen.auth.SignUpActivity
import com.afauzi.peoemergency.screen.main.MainActivity
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.auth
import com.afauzi.peoemergency.utils.MyLog
import com.google.firebase.auth.FirebaseAuth

class LandingActivity : AppCompatActivity() {

    private lateinit var log: MyLog

    /**
     * Declaration viewBinding
     */
    private lateinit var binding: ActivityLandingBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log = MyLog

        // Todo : Splashscreen Api Wajib dipanggil untuk dijalankan dan ditampilkan sebelum activity launcher di tampilkan
        installSplashScreen()

        // Initials viewBinding inflate layout landing_activity.xml
        binding = ActivityLandingBinding.inflate(layoutInflater)

        // set content layout root
        setContentView(binding.root)

    }

    override fun onResume() {
        super.onResume()

        currentUser()
        onClickBtn(binding.btnToSignInActivity, SignInActivity::class.java)
        onClickBtn(binding.btnToSignUpActivity, SignUpActivity::class.java)

    }

    /**
     * Methode handle current user with firebase auth user
     */
    private fun currentUser() {

        // Todo: Check conditional user, if user not null or user is login before, user access MainActivity Class
        if (auth.currentUser != null) {
            Log.i(log.TAG, "user logged in")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        // Todo: If user is not login or null, user not access MainActivity Class before login
        else {
            Log.i(log.TAG, "user not login")
        }
    }

    /**
     * Handle onClick Button tanpa behaviour apapun
     */
    private fun onClickBtn(btnView: View, actionClass: Class<*>) {
        btnView.setOnClickListener {
            Log.i(log.TAG, "button clicked")
            startActivity(Intent(this, actionClass))
        }
    }

}