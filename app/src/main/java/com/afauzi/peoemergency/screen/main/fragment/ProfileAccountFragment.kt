package com.afauzi.peoemergency.screen.main.fragment

import android.content.Intent
import android.graphics.Color
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
import com.afauzi.peoemergency.utils.FirebaseServiceInstance
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.auth
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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
                FirebaseServiceInstance.databaseReference = FirebaseServiceInstance.firebaseDatabase.getReference("users").child(it.uid)
                FirebaseServiceInstance.databaseReference.addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val gender = snapshot.child("gender").value.toString()

                            username.text = snapshot.child("username").value.toString()
                            Log.i(TAG, "username: ${username.text}") // data username done

                            if (gender == "Laki-Laki") {
                                Picasso
                                    .get()
                                    .load(snapshot.child("photoProfile").value.toString())
                                    .placeholder(com.afauzi.peoemergency.R.drawable.boy_image_place_holder)
                                    .into(photoProfile)
                            } else {
                                Picasso
                                    .get()
                                    .load(snapshot.child("photoProfile").value.toString())
                                    .placeholder(com.afauzi.peoemergency.R.drawable.girl_image_place_holder)
                                    .into(photoProfile)
                            }
                            Log.i(TAG, "Photo Profile Uri: ${snapshot.child("photoProfile").value.toString()}") // data username done
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
