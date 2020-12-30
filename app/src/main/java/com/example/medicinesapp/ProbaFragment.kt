package com.example.medicinesapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.medicinesapp.adding.scanner.MyImageAnalyzer
import com.example.medicinesapp.databinding.TakePhotoBinding
import kotlinx.android.synthetic.main.take_photo.*
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ProbaFragment: Fragment() {

    private lateinit var binding: TakePhotoBinding
    private lateinit var cameraExecutor: ExecutorService
    private val permission = Manifest.permission.CAMERA
    private val requestCode = 2

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TakePhotoBinding.inflate(inflater, container, false)

        if (allPermissionGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), requestCode)
        }

        /*
        binding.button2.setOnClickListener {
            takePhoto()
        }

         */

        outputDirectory = getOutputDirectory(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cameraExecutor = Executors.newSingleThreadExecutor()

    }

    private fun startCamera() {

        Log.d("1", "startCamera: ")

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {

            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.createSurfaceProvider())
            }

            imageCapture = ImageCapture.Builder()
                .build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { it.setAnalyzer(cameraExecutor,
                    MyImageAnalyzer()
                ) }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture,imageAnalyzer)

        }, ContextCompat.getMainExecutor(requireContext()))

    }

    private fun takePhoto() {

        val imageCapture = imageCapture ?: return

        val photoFile = File(
            outputDirectory, SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        /*
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {

                override fun onError(exc: ImageCaptureException) {
                    Log.d("1", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Log.d("1", "$msg")
                }
            })
         */

        imageCapture.takePicture(cameraExecutor,object:ImageCapture.OnImageCapturedCallback(){

            override fun onCaptureSuccess(image: ImageProxy) {
                val bitmap = imageProxyToBitmap(image)
                Log.d("1", "onCaptureSuccess: ")
                super.onCaptureSuccess(image)
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.d("1", "onCaptureNotSuccess: ")
            }
        })
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val planeProxy = image.planes[0]
        val buffer: ByteBuffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun getOutputDirectory(context: Context): File {
        val appContext = context.applicationContext
        val mediaDir = context.externalCacheDir.let {
            File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else appContext.filesDir
    }


    private fun allPermissionGranted() = ContextCompat.checkSelfPermission(
        requireContext(),
        permission
    ) == PackageManager.PERMISSION_GRANTED


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (this.requestCode == requestCode) {
            if (allPermissionGranted()) {
                //startCamera()
            }
            else{
                Toast.makeText(requireContext(),
                    "Permissions not granted",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()

    }

}