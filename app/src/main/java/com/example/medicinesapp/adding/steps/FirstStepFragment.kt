package com.example.medicinesapp.adding.steps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.medicinesapp.databinding.FirstFragmentBinding
import com.example.medicinesapp.rxBus.MyRxBus
import com.example.medicinesapp.rxBus.RxEvent
import com.google.android.material.card.MaterialCardView
import io.jmdg.rxbus.RxBus

class FirstStepFragment:Fragment() {


    private lateinit var binding:FirstFragmentBinding
    private lateinit var cardOne:MaterialCardView
    private lateinit var cardTwo:MaterialCardView
    private lateinit var cardThree:MaterialCardView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FirstFragmentBinding.inflate(inflater,container,false)


        cardOne = binding.card1
        cardTwo = binding.card4
        cardThree = binding.card3

       handleCheck()


        return binding.root
    }


    private fun handleCheck(){

        cardOne.isChecked = true

        cardOne.setOnClickListener {
            cardOne.isChecked = !cardOne.isChecked
            cardTwo.isChecked = false
            cardThree.isChecked = false
        }

        cardTwo.setOnClickListener {
            cardTwo.isChecked = !cardTwo.isChecked
            cardOne.isChecked = false
            cardThree.isChecked = false
        }
        cardThree.setOnClickListener {
            cardThree.isChecked = !cardThree.isChecked
            cardTwo.isChecked = false
            cardOne.isChecked = false
        }

        cardOne.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                RxBus.post(RxEvent.TypeChosenEvent(0))
            }
        }
        cardTwo.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                RxBus.post(RxEvent.TypeChosenEvent(1))
            }

        }
        cardThree.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                RxBus.post(RxEvent.TypeChosenEvent(2))
            }

        }
    }

}
