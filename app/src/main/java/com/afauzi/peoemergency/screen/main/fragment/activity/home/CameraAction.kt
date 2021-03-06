package com.afauzi.peoemergency.screen.main.fragment.activity.home

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.afauzi.peoemergency.databinding.ActivityCameraActionBinding
import com.afauzi.peoemergency.screen.main.MainActivity
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraAction : AppCompatActivity() {

    private lateinit var binding: ActivityCameraActionBinding

    private lateinit var cameraProviderFeature: ListenableFuture<ProcessCameraProvider>

    private lateinit var cameraSelector: CameraSelector

    private var imageCapture: ImageCapture? = null
    private lateinit var imageCaptureExecutor: ExecutorService

    private var getImageUri: Uri? = null

    private fun initView() {
        cameraProviderFeature = ProcessCameraProvider.getInstance(this)
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        imageCaptureExecutor = Executors.newSingleThreadExecutor()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraActionBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initView()
    }

    override fun onResume() {
        super.onResume()

        startCamera()

        binding.btnCaptureImage.setOnClickListener {
            takePhoto()
            animateFlash()
        }

//        binding.btnGallery.setOnClickListener {
//            val intent = Intent(this, GalleryActivity::class.java)
//            startActivity(intent)
//        }

        binding.btnSwitchCamera.setOnClickListener {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            startCamera()
        }

    }

    private fun startCamera() {
        cameraProviderFeature.addListener({
            val cameraProvider = cameraProviderFeature.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                Log.d(TAG, "Use case binding failed")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun uploadImageToFirestore(file: File) {
        getImageUri = Uri.fromFile(file)
        Log.i("getImgUri", "dataImage: $getImageUri")
    }

    private fun takePhoto() {
        imageCapture?.let {
            val fileName = "JPEG_${System.currentTimeMillis()}"
            val file = File(externalMediaDirs[0], fileName)

            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()

            // Mendapatkan data file
            uploadImageToFirestore(file)

            it.takePicture(
                outputFileOptions,
                imageCaptureExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {

                        Log.i(TAG, "The image has been save in ${outputFileResults.savedUri}")
                        val bundle = Bundle()
                        bundle.putString(
                            "resultCapturePostRandom",
                            outputFileResults.savedUri.toString()
                        )

                        val intent = Intent(this@CameraAction, MainActivity::class.java)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Toast.makeText(
                            binding.root.context,
                            "Error Taking Photo",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(TAG, "Error Taking Photo: $exception")
                    }

                })
        }
    }

    private fun animateFlash() {
        binding.root.postDelayed({
            binding.root.foreground = ColorDrawable(Color.WHITE)
            binding.root.postDelayed({
                binding.root.foreground = null
            }, 50)
        }, 100)
    }

    companion object {
        const val TAG = "CameraActionActivity"
    }
}