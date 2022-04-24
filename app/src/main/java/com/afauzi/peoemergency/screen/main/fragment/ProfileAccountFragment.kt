package com.afauzi.peoemergency.screen.main.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.databinding.FragmentProfileAccountBinding
import com.afauzi.peoemergency.screen.auth.SignInActivity
import com.afauzi.peoemergency.screen.main.fragment.activity.accountProfile.EditProfileActivity
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.auth
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.databaseReference
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.firebaseDatabase
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.user
import com.google.firebase.database.*

class ProfileAccountFragment : Fragment() {

    private lateinit var binding: FragmentProfileAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData()

        binding.fabMoreAccountProfile.setOnClickListener {
            popupMenu()
        }

        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(activity, EditProfileActivity::class.java))
        }

    }

    private fun getData() {

        user = auth.currentUser!!
        val uid = user.uid

        databaseReference = firebaseDatabase.getReference("users").child(uid)
        databaseReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    binding.usernameProfile.text = snapshot.child("username").value.toString()
                    binding.emailProfile.text = snapshot.child("email").value.toString()
                    binding.dateJoinUser.text = "User joined ${snapshot.child("date_join").value.toString()}"
                } else {
                    // Handle if data null or problem network
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    @SuppressLint("RtlHardcoded")
    private fun popupMenu() {
        val popupMenu = PopupMenu(activity, binding.fabMoreAccountProfile, Gravity.RIGHT)
        popupMenu.menuInflater.inflate(R.menu.account_more_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout_account -> {
                    user = auth.currentUser!!
                    user.let {
                        auth.signOut()
                        startActivity(Intent(activity, SignInActivity::class.java))
                        activity?.finish()
                    }
                }
            }

            true
        }
        popupMenu.show()
    }
}
