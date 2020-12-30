package com.example.medicinesapp.adding.dialogs

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.medicinesapp.adding.steps.SecondStepFragment
import com.example.medicinesapp.databinding.NumberDaysDialogBinding
import com.example.medicinesapp.rxBus.MyRxBus
import com.example.medicinesapp.rxBus.RxEvent
import it.sephiroth.android.library.numberpicker.doOnProgressChanged
import java.text.SimpleDateFormat
import java.util.*

class NumberOfDaysDialog:DialogFragment() {

    private lateinit var binding:NumberDaysDialogBinding

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= NumberDaysDialogBinding.inflate(inflater,container,false)

        val sdf = SimpleDateFormat("yyyy-MM-dd")

        val startDate = arguments?.getLong("date")!!

        val dateStart = Date(startDate)
        val calendarStart = Calendar.getInstance()
        calendarStart.time = dateStart

        val a =calendarStart.get(Calendar.DATE)



        val stringStartOut = sdf.format(dateStart)
        binding.dateStart.text = stringStartOut


        var dateEnd:Date?=null

        binding.numberPicker.doOnProgressChanged { _, progress, _ ->

            val calendarEnd = Calendar.getInstance()
            calendarEnd.timeInMillis = calendarStart.timeInMillis
            calendarEnd.add(Calendar.DATE,progress)
            dateEnd = Date(calendarEnd.timeInMillis)
            val stringEndOut = sdf.format(dateEnd)
            binding.dateEnd.text = stringEndOut
        }

        binding.done.setOnClickListener {

            val listDates = SecondStepFragment.getDaysBetweenDates(dateStart,dateEnd)

            if (listDates != null) {
                MyRxBus.publish(RxEvent.EventDate(listDates))
            }

            this.dismiss()
        }


        return binding.root
    }




}