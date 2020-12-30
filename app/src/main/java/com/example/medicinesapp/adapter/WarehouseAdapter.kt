package com.example.medicinesapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.medicinesapp.R
import com.example.medicinesapp.data.PillOrganizerManager
import com.example.medicinesapp.databinding.WarehouseItemBinding
import com.example.medicinesapp.utill.listeners.TransitionClickItemListener2


class WarehouseAdapter(private val organizerList:List<PillOrganizerManager>,private val listener: TransitionClickItemListener2): RecyclerView.Adapter<WarehouseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WarehouseViewHolder {

        return WarehouseViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.warehouse_item, parent, false),listener)
    }


    override fun onBindViewHolder(holder: WarehouseViewHolder, position: Int) {
        holder.bind(organizerList[position])
    }


    override fun getItemCount(): Int {
        return organizerList.size
    }

}

class WarehouseViewHolder(private val binding: WarehouseItemBinding,private val listener: TransitionClickItemListener2): RecyclerView.ViewHolder(binding.root){


    init{

        binding.setClickListener{

            listener.onClickListener(binding.item!!)
        }
    }


    fun bind(pillOrganizer: PillOrganizerManager){


        val notExpired = pillOrganizer.listPill.filter { it.inside !="used" && it.bought }
        val toBuy = pillOrganizer.listPill.filter { !it.bought }

        var amountStart = 0
        var amountNow = 0

        notExpired.forEach {pill->

            amountStart+=pill.left!!
            amountNow+=pill.leftNow!!
        }

        binding.organizerText.text = "${amountNow}/${amountStart}"

        if(amountStart!=0 && amountNow!=0 ) {
            val result = (amountNow / amountStart) * 100
            binding.progressTwo.progress = result
        }
        else{
            binding.progressTwo.progress = 0
        }


        binding.apply {
            this.item = pillOrganizer
            executePendingBindings()
        }
    }

}