package com.example.medicinesapp.adding.scanner

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.medicinesapp.adapter.InternetViewHolder
import com.example.medicinesapp.adapter.ScanRecipeAdapter
import com.example.medicinesapp.data.FromScan
import com.example.medicinesapp.databinding.ScanAddBinding
import com.example.medicinesapp.utill.listeners.RecyclerViewItemClickListener
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition

class ScanAdd:Fragment() {

    lateinit var binding:ScanAddBinding
    private lateinit var adapter: ScanRecipeAdapter

    private val permissions = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val requestCode2 = 101
    private val requestCodeImage = 100
    private var fromScanList = mutableListOf<FromScan>()
    private var currentItem:FromScan?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ScanAddBinding.inflate(inflater,container,false)
        binding.button.setOnClickListener {
            fromDisk()
        }

        binding.go.setOnClickListener {
            currentItem?.let {item->
            findNavController().previousBackStackEntry?.savedStateHandle?.set("key", item)
            findNavController().popBackStack()
            }
        }

        adapter = ScanRecipeAdapter(fromScanList,object:RecyclerViewItemClickListener<FromScan>{
            override fun onClickListener(item: FromScan) {
                currentItem = item
                fromScanList.filter { it!=item }.onEach { it.isClicked = false }
                adapter.notifyDataSetChanged()
            }
        })

        binding.pager.adapter = adapter

        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                binding.textNow.text = "${position+1} z ${fromScanList.size}"
            }
        })

        return binding.root
    }


    private fun fromDisk(){

        requestPermissionsIfNecessary()
        val choosePhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(choosePhoto,requestCodeImage)
    }


    private fun analyze(image:InputImage){

        val recognizer = TextRecognition.getClient()
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                processTextRecognitionResult(visionText.text)
            }
            .addOnFailureListener { e ->
                Log.d("1", "analyze BAD")
            }
    }

    private fun processTextRecognitionResult(result : String){

        val pattern = """Recepta[\d\D]+"""
        val regex = pattern.toRegex()
        val matchResult = regex.findAll(result)

        var words:String?=null
        matchResult.forEach{
            words = it.value
        }

        var myResult = mutableListOf<String>()
        words?.let{
            myResult = it.split("Recepta").toMutableList()
            myResult.removeAt(0)
            Log.d("1", "processTextRecognitionResult: $myResult ")
        }

        myResult.forEach {
            val fromScan = furtherAnalysis(it)
            fromScanList.add(fromScan)
            adapter.notifyDataSetChanged()
        }

        binding.textNow.text = "${binding.pager.currentItem+1} z ${fromScanList.size}"
    }


    private fun furtherAnalysis(text:String): FromScan {

        var dosageStringOut = ""
        var nameStringOut = ""
        var organizerStringOut = ""


        val deleteSpace = text.split('\n')
        val filtered = deleteSpace.filter { it.length > 1 }.toMutableList()
        val textFirst = filtered.joinToString(separator = "\n")

        var nameOut:String=""
        var name:String?=null
        val nameList = filtered.filter { it.length>12}.filter { it.contains("Przepisano") }
        if(nameList.isNotEmpty()){
            name = nameList.first()
            filtered.remove(name)
            name = name.replace("Przepisano ","")
        }
        nameOut = if(name.isNullOrEmpty()){
            filtered[3]
            filtered.removeAt(3)
        }else{
            name
        }
        var second=""

        val info = filtered.filter { ((it.contains("op") || it.contains("but") || it.contains("ml")|| it.contains("szt") || it.contains("tabl")) && !it.contains("opłat")) && it.length > 5}
        if(info.isNotEmpty()){
            val first =  info.first()
            filtered.remove(first)
            second = first
        }

        val dosage = filtered.filter { it.contains("Dawkowanie") }
        if(dosage.isNotEmpty()){
            var dosageOut = dosage.first()
            dosageOut = dosageOut.replace("Dawkowanie: ","")
            dosageOut = dosageOut.replace("Dawkowanie ","")
            dosageStringOut = dosageOut
        }

        if(second==""){
            var prev = nameOut
            val pattern = """(\d+|\s)(\s*)(but|op)[\d\D]+"""
            val regex = pattern.toRegex()
            val matchResult = regex.findAll(prev)


            var result:String = ""
            matchResult.forEach {
                result=it.value
            }

            prev = prev.replace(result,"")
           nameStringOut = prev
            organizerStringOut = result
        }else{
           organizerStringOut = second
           nameStringOut = nameOut
        }


        val split = dosageStringOut.replace("x "," x ").replace(" x"," x ").split(" x ")
        val howMuch = getNumberFromString(split.first())
        var amount = getNumberFromString(split.last())
        var organizerHowMuch = 1.0
        var organizerAmount = 1.0
        var type = ""

        when {
            organizerStringOut.contains(" x ") -> {

                val splitOne = organizerStringOut.split(" x ")
                organizerHowMuch = getNumberFromString(splitOne.first())
                organizerAmount = getNumberFromString(splitOne.last())
                type= InternetViewHolder.decideWhatType(splitOne.last())
            }
            organizerStringOut.contains(" po ") -> {

                val splitTwo = organizerStringOut.split(" po ")
                organizerHowMuch = getNumberFromString(splitTwo.first())
                organizerAmount = getNumberFromString(splitTwo.last())
                type= InternetViewHolder.decideWhatType(splitTwo.last())

            }
        }

        if(organizerHowMuch>20.0){
            organizerHowMuch = 2.0
        }

        val dose = organizerAmount * organizerHowMuch


        if(type=="ml" && dose>100.0 && amount<5.0){
            amount = 10.0
        }


        return FromScan(text,nameStringOut,organizerStringOut,dosageStringOut,amount,howMuch,dose,type,false)
    }


    private fun getNumberFromString(text:String): Double {

        val pattern = "(\\d+)"
        val regex = pattern.toRegex()
        val matchResult = regex.findAll(text)
        var result = 1.0

        matchResult.forEach {
          result*=(it.value.toDouble())
        }
        return result
    }


    //-

    private fun checkAllPermissions(): Boolean {
        var hasPermissions = true
        for (permission in permissions) {
            hasPermissions = hasPermissions and isPermissionGranted(permission)
        }
        return hasPermissions
    }


    private fun requestPermissionsIfNecessary() {
        if (!checkAllPermissions()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissions.toTypedArray(),
                requestCode2
            )
        }
    }

    private fun isPermissionGranted(permission: String) =
        ContextCompat.checkSelfPermission(requireContext(), permission) ==
                PackageManager.PERMISSION_GRANTED


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (this.requestCode2 == requestCode) {
            requestPermissionsIfNecessary()
        }
    }

    //-

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode== Activity.RESULT_OK){
            if(requestCode==requestCodeImage){
                val imageUri: Uri? = data?.clipData?.let {
                    it.getItemAt(0).uri
                } ?: data?.data
                val image = InputImage.fromFilePath(requireContext(), imageUri!!)
                analyze(image)
                Log.d("1", "onActivityResult:  $imageUri")
            }
        }
    }

}