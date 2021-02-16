package com.example.medicinesapp.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.medicinesapp.R
import com.example.medicinesapp.data.FromInternet
import com.example.medicinesapp.databinding.InternetPriceItem3Binding
import com.example.medicinesapp.utill.listeners.ViewPagerItemClickListener
import it.sephiroth.android.library.numberpicker.doOnProgressChanged

class InternetPriceAdapter3(private val recipeList:List<FromInternet>,
                            private val value:Double,
                            private val listener : ViewPagerItemClickListener<Bundle>
): RecyclerView.Adapter<InternetViewHolder3>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InternetViewHolder3 {

        return InternetViewHolder3(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.internet_price_item3, parent, false),listener)
    }


    override fun onBindViewHolder(holder: InternetViewHolder3, position: Int) {
        holder.bind(recipeList[position],value)
    }


    override fun getItemCount(): Int {
        return recipeList.size
    }
}

class InternetViewHolder3(private val binding: InternetPriceItem3Binding,private val listener : ViewPagerItemClickListener<Bundle>): RecyclerView.ViewHolder(binding.root){



    var info:FromInternet?=null

    init{

        binding.card.setOnClickListener {

            if(!info!!.clicked) {
                info!!.clicked = true
                binding.checked.alpha = 1.0f
                val bundleAdd = bundleOf("add" to binding.info)
                listener.onClickListener(bundleAdd)
                Log.d("1", "COJEST CLICK T ${binding.info} ")

            }else{
                Log.d("1", "COJEST CLICK N ${binding.info} ")
                info!!.clicked = false
                binding.checked.alpha = 0.0f
                val bundleRemove = bundleOf("remove" to binding.info)
                listener.onClickListener(bundleRemove)
            }
        }

        binding.numberPicker.doOnProgressChanged { _, progress, _ ->

            info!!.count = progress
            //info!!.amount = info!!.amount *progress.toDouble()
        }
    }


    fun bind(info:FromInternet,myValue:Double){


        binding.apply {
            this.info = info
            executePendingBindings()
        }

     if(info.clicked){
         binding.checked.alpha =1.0f
     }else{
         binding.checked.alpha =0.0f
     }

        this.info = info
        val amountSentence = info.body!!.split(",").last()
        val type = InternetViewHolder.decideWhatType(amountSentence)
        val amount = InternetViewHolder.calculateAmount(amountSentence)
        info.amount = amount

        var quantity=1

        while( (myValue/amount)>quantity){
            quantity ++
        }

        binding.amount.text = "$quantity sztuk"

        if(info.firstTime) {
            //binding.numberPicker.progress = quantity
            info.firstTime = false
            //info.amount = binding.numberPicker.progress.toDouble()
        }

    }

}


