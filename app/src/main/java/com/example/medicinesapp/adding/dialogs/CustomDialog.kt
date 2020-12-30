package com.example.medicinesapp.adding.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.medicinesapp.databinding.CustomDialogBinding
import com.example.medicinesapp.rxBus.MyRxBus
import com.example.medicinesapp.rxBus.RxEvent
import com.google.android.material.chip.Chip

class CustomDialog:DialogFragment() {

    private lateinit var binding: CustomDialogBinding
    private var myList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = CustomDialogBinding.inflate(inflater,container,false)

        binding.exit.setOnClickListener {
            this.dismiss()
        }

        binding.end.setOnClickListener {

            val ids = binding.chips.checkedChipIds

            ids.forEach {
                val chip = binding.root.findViewById<Chip>(it)
                myList.add(chip.text.toString())
            }
            //MyRxBus.publish(RxEvent.EventHourThree(myList))

            this.dismiss()
        }

        return binding.root
    }

}