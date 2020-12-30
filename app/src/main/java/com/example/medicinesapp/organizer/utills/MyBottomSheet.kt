package com.example.medicinesapp.organizer.utills

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.medicinesapp.databinding.MyBottomSheetBinding
import com.example.medicinesapp.rxBus.MyRxBus
import com.example.medicinesapp.rxBus.RxEvent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class MyBottomSheet(): BottomSheetDialogFragment() {

    private lateinit var binding:MyBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = MyBottomSheetBinding.inflate(inflater,container,false)

        binding.one.setOnClickListener {

            MyRxBus.publish(RxEvent.BottomSheetEvent(0))
            dismiss()
        }

        binding.two.setOnClickListener {

            MyRxBus.publish(RxEvent.BottomSheetEvent(1))
            dismiss()
        }

        binding.three.setOnClickListener {

            MyRxBus.publish(RxEvent.BottomSheetEvent(2))
            dismiss()
        }

        return binding.root
    }


}