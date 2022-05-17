package com.afauzi.peoemergency.screen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.afauzi.peoemergency.databinding.ActivityLandingBinding
import com.afauzi.peoemergency.screen.auth.SignInActivity
import com.afauzi.peoemergency.screen.auth.SignUpActivity
import com.afauzi.peoemergency.screen.main.MainActivity
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.user

class LandingActivity : AppCompatActivity() {

    companion object {
        const val TAG = "LandingActivity"
    }

    /**
     * Declaration viewBinding
     */
    private lateinit var binding: ActivityLandingBinding

    /**
     * Declaration btn Sign in
     */

    private lateinit var btnToSignInActivity: Button

    /**
     * Declaration btn Sign up
     */
    private lateinit var btnToSignUpActivity: Button

    /**
     * Initials views
     */
    private fun initView() {
        btnToSignInActivity = binding.btnToSignInActivity
        btnToSignUpActivity = binding.btnToSignUpActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Todo : Splashscreen Api Wajib dipanggil untuk dijalankan dan ditampilkan sebelum activity launcher di tampilkan
        installSplashScreen()

        binding = ActivityLandingBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // init views
        initView()

        // Current User
        currentUser()

    }

    override fun onResume() {
        super.onResume()
        // onClick to second activity
        onClickBtn(btnToSignInActivity, SignInActivity::class.java)
        onClickBtn(btnToSignUpActivity, SignUpActivity::class.java)

    }

    /**
     * Handle current user
     */
    private fun currentUser() {
        user.let {
            if (it != null) {
                Log.i(TAG, "user logged in")
                Log.i(TAG, "name: ${it.displayName}")
                Log.i(TAG, "email: ${it.email}")
                Log.i(TAG, "photo profile: ${it.photoUrl}")
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Log.i(TAG, "user not login")
            }
        }
    }

    /**
     * Handle onClick Button tanpa behaviour apapun
     */
    private fun onClickBtn(btnView: View, actionClass: Class<*>) {
        btnView.setOnClickListener {
            Log.i(TAG, "button clicked")
            startActivity(Intent(this, actionClass))
        }
    }

}