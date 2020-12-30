package com.example.medicinesapp.adding.steps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.medicinesapp.R
import com.example.medicinesapp.databinding.ThirdFragmentBinding
import com.example.medicinesapp.rxBus.MyRxBus
import com.example.medicinesapp.rxBus.RxEvent
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class ThirdStepFragment:Fragment() {

    private lateinit var binding:ThirdFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= ThirdFragmentBinding.inflate(inflater,container,false)

        binding.card1.setOnClickListener {
            startSingleTime()
        }

        binding.card4.setOnClickListener {

            startSecondDialog()
        }

        binding.card3.setOnClickListener {

            startThirdDialog()
        }

        return binding.root
    }


    private fun startSingleTime(){

        val materialTimePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .build()

        materialTimePicker.show(requireActivity().supportFragmentManager, materialTimePicker.toString())

        materialTimePicker.addOnPositiveButtonClickListener {
            val newHour: Int = materialTimePicker.hour
            val newMinute: Int = materialTimePicker.minute

            var newMinuteString = newMinute.toString()
            var newHourString = newHour.toString()

            if (newMinuteString.length == 1) {
                newMinuteString = "0$newMinuteString"
            }

            if (newHourString.length == 1) {
                newHourString = "0$newHourString"
            }

            val send = "$newHourString:$newMinuteString"

            MyRxBus.publish(RxEvent.EventHourOne(send))
        }
    }

    private fun startSecondDialog(){

        val materialTimePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .build()

        materialTimePicker.show(requireActivity().supportFragmentManager, materialTimePicker.toString())

        materialTimePicker.addOnPositiveButtonClickListener {
            val newHour: Int = materialTimePicker.hour
            val newMinute: Int = materialTimePicker.minute

            var newMinuteString = newMinute.toString()
            var newHourString = newHour.toString()

            if (newMinuteString.length == 1) {
                newMinuteString = "0$newMinuteString"
            }

            if (newHourString.length == 1) {
                newHourString = "0$newHourString"
            }

            val send = "$newHourString:$newMinuteString"


            MyRxBus.publish(RxEvent.EventHourTwoOne(send))

            val bundle = bundleOf("hour" to send)
            findNavController().navigate(R.id.action_mainAdd_to_dateDialog,bundle)
        }

    }

    private fun startThirdDialog(){
        findNavController().navigate(R.id.action_mainAdd_to_customDialog)
    }

}