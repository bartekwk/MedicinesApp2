package com.example.medicinesapp.notYet.firestore

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.medicinesapp.databinding.MainFirestoreBinding
import com.example.medicinesapp.notYet.firestore.utils.MainViewEvent
import com.example.medicinesapp.notYet.firestore.utils.MainViewState
import com.example.medicinesapp.utill.Helper
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable


class MainFragment() :Fragment(){

    private lateinit var binding:MainFirestoreBinding
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(Helper.provideRepository(requireContext()))
    }

    @SuppressLint("CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = MainFirestoreBinding.inflate(inflater,container,false)


        val clickEvents = Observable.merge(
            //initialEvent(),
            binding.a.clicks().map{MainViewEvent.OneClick},
            binding.c.clicks().map{MainViewEvent.ThreeClick},
            binding.b.clicks().map{MainViewEvent.TwoClick})

        viewModel.handleViewIntents(clickEvents)

        viewModel.states().observe(viewLifecycleOwner, Observer {
            if(it!=null){
                Log.d("1", "GATCHA")
                //render(it)
            }
        })


        return binding.root
    }


    private fun render(state:MainViewState){
        with(state){
            Log.d("1", "render: $list ")
        }
    }

    private fun initialEvent(): Observable<MainViewEvent.ThreeClick> {
        return Observable.just(MainViewEvent.ThreeClick)
    }



}