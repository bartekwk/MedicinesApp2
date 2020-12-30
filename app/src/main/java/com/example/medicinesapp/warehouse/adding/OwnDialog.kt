package com.example.medicinesapp.warehouse.adding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.medicinesapp.R
import com.example.medicinesapp.data.FromInternet
import com.example.medicinesapp.databinding.CustomOrganizerBinding
import com.example.medicinesapp.rxBus.RxEvent
import io.jmdg.rxbus.RxBus
import www.sanju.motiontoast.MotionToast

class OwnDialog: DialogFragment() {


    private lateinit var binding:CustomOrganizerBinding
    private val customImage = "https://img.aws.la-croix.com/2014/03/05/1115806/En-2012-chaque-Francais-achete-moyenne-48-boites-medicaments-pour-somme-525_1_730_600.jpg"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CustomOrganizerBinding.inflate(inflater,container,false)

        Glide.with(requireContext())
            .load(customImage)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.image)


        binding.go.setOnClickListener {
            val text = binding.type.text!!
            if(text.isNotEmpty()){
                val fromInternet = FromInternet(false,
                    customImage,text.toString(),
                    "0,0 zł",null,(binding.numberPicker2.progress).toDouble(),
                    binding.numberPicker.progress,false,"null","null",false,true)
                Log.d("1", "COTO $fromInternet ")
                RxBus.post(RxEvent.EventInternetList(listOf(fromInternet)))
                dismiss()
            }else{
                MotionToast.createToast(requireActivity(),"BŁĄD DODAWANIA","Spróbuj jeszcze raz",
                    MotionToast.TOAST_ERROR,
                    MotionToast.GRAVITY_CENTER,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(requireContext(), R.font.helvetica_regular))
            }
        }

        return binding.root
    }


}