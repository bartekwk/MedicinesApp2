package com.example.medicinesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.medicinesapp.R
import com.example.medicinesapp.data.PillOrganizerDB
import com.example.medicinesapp.databinding.WarehouseDetailItemBinding

class WarehouseDetailAdapter(private val pillsList:List<PillOrganizerDB>): RecyclerView.Adapter<WarehouseDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WarehouseDetailViewHolder {

        return WarehouseDetailViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.warehouse_detail_item, parent, false))
    }

    override fun onBindViewHolder(holder: WarehouseDetailViewHolder, position: Int) {
        holder.bind(pillsList[position])
    }

    override fun getItemCount(): Int {
        return pillsList.size
    }
}


class WarehouseDetailViewHolder(private val binding: WarehouseDetailItemBinding): RecyclerView.ViewHolder(binding.root){

    fun bind(pill:PillOrganizerDB){



        val howMany = (pill.left!! / pill.amount!!).toInt()
        val priceAllBefore = pill.price!! * pill.amount
        val priceAllAfter = String.format("%.2f", priceAllBefore).replace(",",".")
        var left = pill.leftNow

        pill.leftNow?.let {
            if(it == -1000 ){
                left = 0
            }
        }

        binding.text12.text = "${pill.amount?.toInt()} op. po $howMany szt."
        binding.text22.text = "$left sztuk"
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