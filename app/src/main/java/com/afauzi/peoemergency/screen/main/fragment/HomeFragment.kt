package com.afauzi.peoemergency.screen.main.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class HomeFragment : Fragment(), AdapterListRandPost.CallClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var randomPostArrayList: ArrayList<ModelItemRandomPost>

    private lateinit var fabAdd: FloatingActionButton
    private lateinit var fabRandomPost: FloatingActionButton
    private lateinit var fabEmergencyPost: FloatingActionButton
    private var rotate = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        randomPostArrayList = arrayListOf()
        listPostRandom()

        fabAction()

    }

    private fun listPostRandom() {

        fun recyclerViewPostSetup() {
            recyclerView = requireView().findViewById(R.id.rv_list_rand_post)
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        }
        recyclerViewPostSetup()

        fun getListPost() {

            user = auth.currentUser!!

            databaseReference = firebaseDatabase.getReference("random_post")
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        randomPostArrayList.clear()

                        for (listPost in snapshot.children) {
                            val listItem = listPost.getValue(ModelItemRandomPost::class.java)
                            randomPostArrayList.add(listItem!!)
                        }
                        recyclerView.adapter = AdapterListRandPost(this@HomeFragment, randomPostArrayList)

                    } else {

                        // View implement if data not exist or null

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Snackbar.make(binding.root, error.message, Snackbar.LENGTH_SHORT).show()
                }

            })
        }
        getListPost()

    }

    private fun fabAction() {

        fun initShowOut(v: View) {
            v.visibility = View.GONE
            v.translationY = v.height.toFloat()
            v.alpha = 0f
        }

        fun rotateFab(v: View, rotate: Boolean): Boolean {
            v.animate().setDuration(200).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                }
            }).rotation(if (rotate) 135f else 0f)
            return rotate
        }

        fun showIn(v: View) {
            v.visibility = View.VISIBLE
            v.alpha = 0f
            v.translationY = v.height.toFloat()
            v.animate()
                .setDuration(200)
                .translationY(0f)
                .setListener(object: AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                    }
                }).alpha(1f).start()
        }

        fun showOut(v: View) {
            v.visibility = View.VISIBLE
            v.alpha = 0f
            v.translationY = 0f
            v.animate()
                .setDuration(200)
                .translationY(v.height.toFloat())
                .setListener(object: AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                    }
                })
                .alpha(0f)
                .start()
        }
        fun setUpfabAction() {

            fabAdd = binding.fabAdd
            fabRandomPost = binding.fabRandomPost
            fabEmergencyPost = binding.fabEmergencyPost
            initShowOut(fabRandomPost)
            initShowOut(fabEmergencyPost)

            fabAdd.setOnClickListener {
                rotate = rotateFab(it, !rotate)
                if (rotate) {
                    showIn(fabRandomPost)
                    showIn(fabEmergencyPost)
                } else {
                    showOut(fabRandomPost)
                    showOut(fabEmergencyPost)
                }
            }

            fabRandomPost.setOnClickListener {  Toast.makeText(activity, "Random clicked", Toast.LENGTH_SHORT).show() }
            fabEmergencyPost.setOnClickListener { Toast.makeText(activity, "Emergency clicked", Toast.LENGTH_SHORT).show() }

        }
        setUpfabAction()
    }

    override fun onClickListener(data: ModelItemRandomPost) {
        Toast.makeText(activity, data.desc, Toast.LENGTH_SHORT).show()
    }

    override fun onLongClickListener(data: ModelItemRandomPost) {
        TODO("Not yet implemented")
    }

    override fun onClickRemove(data: ModelItemRandomPost) {
        TODO("Not yet implemented")
    }
}
