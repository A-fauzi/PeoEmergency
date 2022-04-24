package com.afauzi.peoemergency.screen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.afauzi.peoemergency.databinding.ActivityLandingBinding
import com.afauzi.peoemergency.screen.auth.SignInActivity
import com.afauzi.peoemergency.screen.auth.SignUpActivity
import com.afauzi.peoemergency.screen.main.MainActivity
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.auth
import com.afauzi.peoemergency.utils.Library.TAG

class LandingActivity : AppCompatActivity() {

    /**
     * Declaration viewBinding
     */
    private lateinit var binding: ActivityLandingBinding
    private lateinit var btnToSignInActivity: Button
    private lateinit var btnToSignUpActivity: Button

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

        initView()

    }

    override fun onResume() {
        super.onResume()

        auth.currentUser.let {
            if (it != null) {
                Log.i(TAG, "user logged in")
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Log.i(TAG, "user not login")
            }
        }

        onClickBtn(btnToSignInActivity, SignInActivity::class.java)
        onClickBtn(btnToSignUpActivity, SignUpActivity::class.java)

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