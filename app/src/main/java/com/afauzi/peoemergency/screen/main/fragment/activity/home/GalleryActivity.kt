package com.afauzi.peoemergency.screen.main.fragment.activity.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.afauzi.peoemergency.adapter.AdapterListGallery
import com.afauzi.peoemergency.databinding.ActivityGalleryBinding
import java.io.File

class GalleryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGalleryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val directory = File(externalMediaDirs[0].absolutePath)
        val files = directory.listFiles() as Array<File>

        // Array is reversed to ensure last taken photo appears first
        val adapter = AdapterListGallery(files.reversedArray())
        binding.viewPager.adapter = adapter
    }
}