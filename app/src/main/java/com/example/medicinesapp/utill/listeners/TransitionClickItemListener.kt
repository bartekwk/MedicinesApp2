package com.example.medicinesapp.utill.listeners

import com.example.medicinesapp.data.AllPillsFragmentData

interface TransitionClickItemListener<I> {
    fun onClickListener(item: I, pillDB: AllPillsFragmentData,color:Int)
}