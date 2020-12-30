package com.example.medicinesapp.social.barcode

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.medicinesapp.utill.listeners.CameraXListener
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class BarcodeAnalyser(private var listener:CameraXListener<String>):ImageAnalysis.Analyzer {


    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(image: ImageProxy) {
        val mediaImage = image.image
        Log.d("1", "ANALYZE1")
        if (mediaImage != null) {
            Log.d("1", "ANALYZE2")
            val newImage = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
            val scanner = BarcodeScanning.getClient()
            scanner.process(newImage)
                .addOnSuccessListener {
                    for(barcode in it){
                        barcode.rawValue?.let {value->
                            listener.onDoneListener(value)
                        }
                    }
                }
                .addOnFailureListener {
                }.addOnCompleteListener {
                    image.close()
                    mediaImage.close()
                }
        }
    }
}