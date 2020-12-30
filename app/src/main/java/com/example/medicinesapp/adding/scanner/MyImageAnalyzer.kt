package com.example.medicinesapp.adding.scanner

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition

@SuppressLint("UnsafeExperimentalUsageError")
class MyImageAnalyzer:ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {

        val mediaImage = image.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
            val recognizer = TextRecognition.getClient()
            val result = recognizer.process(image)
                .addOnSuccessListener { visionText ->

                    Log.d("1", "analyze: $visionText")

                    processTextRecognitionResult(visionText)

                }
                .addOnFailureListener { e ->

                }
        }
        image.close()
    }

    private fun processTextRecognitionResult(result : Text) {

        for (block in result.textBlocks) {
            val blockText = block.text
            val blockCornerPoints = block.cornerPoints
            val blockFrame = block.boundingBox
            for (line in block.lines) {
                val lineText = line.text
                val lineCornerPoints = line.cornerPoints
                val lineFrame = line.boundingBox
                for (element in line.elements) {
                    val elementText = element.text
                    val elementCornerPoints = element.cornerPoints
                    val elementFrame = element.boundingBox
                }
            }
        }
    }


}