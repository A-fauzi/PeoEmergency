package com.afauzi.peoemergency.screen.main.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.databinding.FragmentProfileAccountBinding
import com.afauzi.peoemergency.screen.LandingActivity
import com.afauzi.peoemergency.screen.auth.SignInActivity
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.auth
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ProfileAccountFragment : Fragment() {

    companion object {
        private const val TAG = "ProfileAccountFragment"
    }

    private lateinit var binding: FragmentProfileAccountBinding

    private lateinit var btnMore: ImageView
    private lateinit var btnNotif: ImageView
    private lateinit var photoProfile: CircleImageView
    private lateinit var username: TextView

    private fun initView() {
        btnMore = binding.ivBtnMore
        btnNotif = binding.ivBtnNotification
        photoProfile = binding.ivPhotoProfile
        username = binding.username
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileAccountBinding.inflate(inflater, container, false)

        initView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnMore.setOnClickListener { Log.i(TAG, "More Clicked") }
        btnNotif.setOnClickListener { Log.i(TAG, "Notification Clicked") }

        getUserData()

        photoProfile.setOnClickListener {
            popupMenu()
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
                    .into(photoProfile)
                Log.i(TAG, "photo Uri: ${it.photoUrl}") // data photo Uri done

            } else {
                Toast.makeText(activity, "User not authentication", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireActivity(), LandingActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun popupMenu() {
        val popupMenu = PopupMenu(activity, binding.ivPhotoProfile, Gravity.RIGHT)
        popupMenu.menuInflater.inflate(R.menu.account_more_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout_account -> {
                    auth.currentUser.let { currentUser ->
                        if (currentUser != null) {
                            auth.signOut()
                            startActivity(Intent(activity, SignInActivity::class.java))
                            activity?.finish()
                        }
                    }
                }
            }

            true
        }
        popupMenu.show()
    }

}
