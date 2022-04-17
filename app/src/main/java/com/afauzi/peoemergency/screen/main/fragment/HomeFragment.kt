package com.afauzi.peoemergency.screen.main.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.adapter.AdapterListRandPost
import com.afauzi.peoemergency.dataModel.ModelItemRandomPost
import com.afauzi.peoemergency.databinding.FragmentHomeBinding
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.auth
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.databaseReference
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.firebaseDatabase
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.user
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*

class HomeFragment : Fragment(){

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData()
    }

    private fun getData() {

        user = auth.currentUser!!
        val uid = user.uid

        databaseReference = firebaseDatabase.getReference("users").child(uid)
        databaseReference.keepSynced(true)
        databaseReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    binding.tvName.text = "Hello ${snapshot.child("username").value.toString()}"
                    if (snapshot.child("current_location").value.toString() == null){
                        Toast.makeText(activity, "Location not detected, please on to location", Toast.LENGTH_SHORT).show()
                    } else {
                        binding.tvLocation.text = snapshot.child("current_location").value.toString()
                    }
                } else {
                    // Handle if data null or problem network
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}
