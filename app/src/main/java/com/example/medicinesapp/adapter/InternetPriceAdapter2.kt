package com.example.medicinesapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.medicinesapp.R
import com.example.medicinesapp.data.FromInternet
import com.example.medicinesapp.databinding.InternetPriceItem2Binding
import com.example.medicinesapp.utill.listeners.RecyclerViewItemClickListener

class InternetPriceAdapter2(private val recipeList:List<FromInternet>,
                            private val listenerRemove: RecyclerViewItemClickListener<FromInternet>,
                            private val listenerAdd: RecyclerViewItemClickListener<FromInternet>): RecyclerView.Adapter<InternetViewHolder2>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InternetViewHolder2 {

        return InternetViewHolder2(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.internet_price_item2, parent, false),listenerRemove,listenerAdd)
    }


    override fun onBindViewHolder(holder: InternetViewHolder2, position: Int) {
        holder.bind(recipeList[position])
    }


    override fun getItemCount(): Int {
        return recipeList.size
    }

}

class InternetViewHolder2(private val binding: InternetPriceItem2Binding,
                          private val listenerRemove: RecyclerViewItemClickListener<FromInternet>,
                          private val listenerAdd: RecyclerViewItemClickListener<FromInternet>): RecyclerView.ViewHolder(binding.root){

    var item:FromInternet?=null

    init{

        binding.mcv.setOnClickListener {
            item?.let {myItem->
                listenerAdd.onClickListener(myItem)
            }
        }

        binding.bin.setOnClickListener {
            item?.let{myItem->
                listenerRemove.onClickListener(myItem)
            }
        }
    }


    fun bind(info:FromInternet){

        Log.d("1", "MAMY ${info.nameOfPill} ")

        if(info.connectedToPill){
            binding.pill.text = "Połączono z lekarstwem : ${info.nameOfPill}"
        }

        if(info.fromPrev){
            val count = info.count
            val amount = info.amount * count

            info.amount = amount
            info.fromPrev = false

            val amountSentence = info.body!!.split(",").last()
            val type = InternetViewHolder.decideWhatType(amountSentence)
            binding.amount.text = " ${info.amount} $type"

        }else {

            item = info
            val amountSentence = info.body!!.split(",").last()
            val type = InternetViewHolder.decideWhatType(amountSentence)
            binding.amount.text = " ${info.amount} $type"
        }


        binding.apply {
            this.info = info
            executePendingBindings()
        }
    }

}