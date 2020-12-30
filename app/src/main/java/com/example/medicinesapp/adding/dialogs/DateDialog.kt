package com.example.medicinesapp.adding.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.medicinesapp.databinding.DialogViewBinding
import com.example.medicinesapp.rxBus.MyRxBus
import com.example.medicinesapp.rxBus.RxEvent
import it.sephiroth.android.library.numberpicker.doOnProgressChanged


class DateDialog:DialogFragment() {

    private lateinit var binding:DialogViewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= DialogViewBinding.inflate(inflater,container,false)

        val startHour = arguments?.getString("hour")!!

        binding.hourStart.text = startHour


        var period:Int?=null
        binding.numberPicker.doOnProgressChanged { _, progress, _ ->
            period = progress
            binding.hourStart2.text = "$progress godz."
        }


        binding.end.setOnClickListener {

            period?.let {
                MyRxBus.publish(RxEvent.EventHourTwoTwo(it.toString()))
            }
            this.dismiss()
        }

        return binding.root
    }






}

