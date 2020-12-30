package com.example.medicinesapp.adding.steps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.example.medicinesapp.R
import com.example.medicinesapp.databinding.FourthFragmentBinding
import com.example.medicinesapp.rxBus.MyRxBus
import com.example.medicinesapp.rxBus.RxEvent
import io.jmdg.rxbus.RxBus
import io.reactivex.disposables.Disposable

class FourthStepFragment: Fragment() {


    private lateinit var binding: FourthFragmentBinding
    private lateinit var disposable: Disposable


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FourthFragmentBinding.inflate(inflater, container, false)

        val items = listOf("sztuk", "gramów", "miligramów", "opakowań")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        (binding.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        //binding.editText.setText("sztuk")

        binding.hourClean.setOnClickListener {

            MyRxBus.publish(RxEvent.EventHourClean)
        }

        binding.end.setOnClickListener {


            val dose = binding.inside1.text.toString()
            val additionalInfo = binding.inside2.text.toString()
            val choice = binding.editText.text.toString()

            if(dose.isNotEmpty()){
                MyRxBus.publish(RxEvent.EventDose(dose))
            }
            if(additionalInfo.isNotEmpty()){
                MyRxBus.publish(RxEvent.EventInfo(additionalInfo))
            }
            if(choice.isNotEmpty()){
                MyRxBus.publish(RxEvent.EventChoice(choice))
            }
            
            RxBus.post(RxEvent.EventClick)
        }

        disposable = MyRxBus.listen(RxEvent.EventDose::class.java).subscribe {
            binding.inside1.setText(it.dose)
        }

        disposable = MyRxBus.listen(RxEvent.EventChoice::class.java).subscribe {
            binding.editText.setText(it.choice)
            binding.autoComplete.autofillValue

        }

        disposable = MyRxBus.listen(RxEvent.EventInfo::class.java).subscribe {
            binding.inside2.setText(it.info)
        }

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        if(!disposable.isDisposed)disposable.dispose()
    }
}