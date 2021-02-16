package com.example.medicinesapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.medicinesapp.R
import com.example.medicinesapp.data.PillOrganizerDB
import com.example.medicinesapp.databinding.WarehouseDetailItem2Binding
import com.example.medicinesapp.utill.listeners.RecyclerViewItemClickListener

class WarehouseDetailAdapter2(private val pillsList:List<PillOrganizerDB>,private val listener: RecyclerViewItemClickListener<PillOrganizerDB>): RecyclerView.Adapter<WarehouseDetailViewHolder2>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WarehouseDetailViewHolder2 {

        return WarehouseDetailViewHolder2(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.warehouse_detail_item_2, parent, false),listener)
    }

    override fun onBindViewHolder(holder: WarehouseDetailViewHolder2, position: Int) {
        holder.bind(pillsList[position])
    }

    override fun getItemCount(): Int {
        return pillsList.size
    }
}

class WarehouseDetailViewHolder2(private val binding: WarehouseDetailItem2Binding,private val listener: RecyclerViewItemClickListener<PillOrganizerDB>): RecyclerView.ViewHolder(binding.root){

    var item:PillOrganizerDB?=null

    init{
        binding.button.setOnClickListener {

            item!!.bought = true
            listener.onClickListener(item!!)
        }
    }


    fun bind(pill:PillOrganizerDB){

        item = pill


        val howMany = (pill.left!! / pill.amount!!).toInt()
        val priceAllBefore = pill.price!! * pill.amount
        val priceAllAfter = String.format("%.2f", priceAllBefore).replace(",",".")

        binding.text12.text = "${pill.amount?.toInt()} op. po $howMany szt."
        binding.text23.text = "${pill.price} zł/szt. łącznie: $priceAllAfter zł"



        binding.apply {
            this.pill = pill
            executePendingBindings()
        }
    }


    private fun View.setAllAlpha(alpha: Float) {
        if (this is ViewGroup) children.forEach { child -> child.alpha = alpha }
    }

}