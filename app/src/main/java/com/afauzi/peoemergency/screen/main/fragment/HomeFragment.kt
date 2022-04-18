package com.afauzi.peoemergency.screen.main.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import com.afauzi.peoemergency.R
import com.afauzi.peoemergency.databinding.FragmentHomeBinding
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.auth
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.databaseReference
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.firebaseDatabase
import com.afauzi.peoemergency.utils.FirebaseServiceInstance.user
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class HomeFragment : Fragment(){

    private lateinit var binding: FragmentHomeBinding
    private lateinit var fabAddPost: ExtendedFloatingActionButton

    private fun initView() {
        fabAddPost = binding.fabAddPostRandom
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initView()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabAddPost.setOnClickListener {
            Toast.makeText(activity, "Fab Clicked", Toast.LENGTH_SHORT).show()
            showBottomDialogPost()
        }

    }

    @SuppressLint("InflateParams")
    private fun showBottomDialogPost(){
        val dialog =  BottomSheetDialog(requireActivity())
        val view = layoutInflater.inflate(R.layout.dialog_post, null)
        val photoProfile = view.findViewById<ImageView>(R.id.image_profile_dialog)
        val username = view.findViewById<TextView>(R.id.username_in_dialog)
        val btnPost = view.findViewById<Button>(R.id.button_post_dialog)
        val inputContentDescPost = view.findViewById<EditText>(R.id.et_content_desc_dialog)
        val chooseImagePost = view.findViewById<ImageView>(R.id.input_photo_dialog)
        val attachFile = view.findViewById<ImageView>(R.id.attach_file_dialog)
        val moreMenu = view.findViewById<ImageView>(R.id.more_menu_dialog)

        dialog.setContentView(view)
        dialog.setCancelable(false)
        dialog.show()

    }

}
