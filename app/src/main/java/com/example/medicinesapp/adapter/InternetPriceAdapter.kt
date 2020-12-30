package com.example.medicinesapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.medicinesapp.R
import com.example.medicinesapp.data.FromInternet
import com.example.medicinesapp.databinding.InternetPriceItemBinding
import com.example.medicinesapp.utill.listeners.RecyclerViewItemClickListener
import it.sephiroth.android.library.numberpicker.doOnProgressChanged

class InternetPriceAdapter(private val recipeList:List<FromInternet>,private val listener: RecyclerViewItemClickListener<FromInternet>): RecyclerView.Adapter<InternetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InternetViewHolder {

        return InternetViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.internet_price_item, parent, false),listener)
    }


    override fun onBindViewHolder(holder: InternetViewHolder, position: Int) {
        holder.bind(recipeList[position])
    }


    override fun getItemCount(): Int {
        return recipeList.size
    }

}

class InternetViewHolder(private val binding: InternetPriceItemBinding,private val listener: RecyclerViewItemClickListener<FromInternet>): RecyclerView.ViewHolder(binding.root){

    var what=""
    var amountNumber=0.0
    var priceGlobal = 0.0
    var currentItem:FromInternet?=null


    init{

        binding.numberPicker.doOnProgressChanged { _, progress, _ ->

            val result = amountNumber*progress
            val result2 = priceGlobal*progress
            val outputResult = String.format("%.2f", result)
            val outputResult2 = String.format("%.2f", result2).replace(".",",")

            binding.amount.text = "Ilość : $outputResult $what"
            binding.text.text = "$outputResult2 zł"


            currentItem?.amount = outputResult.replace(",",".").toDouble()
            currentItem?.price = "$outputResult2 zł"
            currentItem?.count = progress

            listener.onClickListener(currentItem!!)
        }
    }

    fun bind(info:FromInternet){

        currentItem = info

        val amountSentence = info.body!!.split(",").last()

        amountNumber = calculateAmount(amountSentence)


        val price = info.price

        price?.let {
            val priceChanged = it.replace(",",".").split(' ').first()
            val isPriceNumeric  = priceChanged.matches("-?\\d+(\\.\\d+)?".toRegex())

            if(isPriceNumeric){
                priceGlobal = priceChanged.toDouble()
            }
        }


        what = decideWhatType(amountSentence)


        binding.amount.text = "Ilość : $amountNumber $what"

        
        binding.apply {
            this.info = info
            executePendingBindings()
        }
    }


    companion object{

        fun getReturnPrice(price:String?,number:Double):String{

            var priceDouble = 0.0

            price?.let {
                val priceChanged = it.replace(",",".").split(' ').first()
                val isPriceNumeric  = priceChanged.matches("-?\\d+(\\.\\d+)?".toRegex())

                if(isPriceNumeric){
                    priceDouble = priceChanged.toDouble()
                }
            }

            val count = priceDouble *number
            var output = String.format("%.2f", count)
            output = "$output zł"

            return output
        }


        fun decideWhatType(amountSentence:String):String{

            return when {
                amountSentence.contains("szt") -> {
                     "szt"
                }
                amountSentence.contains("tabl") ||amountSentence.contains("kapsu")-> {
                    "tabl"
                }
                amountSentence.contains(" ml ")||amountSentence.contains(" ml. ")||amountSentence.contains("mili")||amountSentence.contains(" ml")||amountSentence.contains(" ml.")->{
                    "ml"
                }
                amountSentence.contains(" l ")||amountSentence.contains(" l. ")||amountSentence.contains("litr")||amountSentence.contains(" l")||amountSentence.contains(" l.")->{
                     "l"
                }
                amountSentence.contains("sasz")->{
                    "sasz"
                }
                else -> "szt"
            }
        }

        fun calculateAmount(amountSentence: String):Double{
            var amountNumber = 1.0
            val amount = amountSentence.split(' ')[1]
            val isNumeric  = amount.matches("-?\\d+(\\.\\d+)?".toRegex())

            if(isNumeric){
                amountNumber = amount.toDouble()
            }

            return amountNumber
        }



    }


}