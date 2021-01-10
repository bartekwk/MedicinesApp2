package com.example.medicinesapp.adapter

import android.util.Log
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


        var amountStart = 0
        var amountNow = 0

        pillOrganizer.listPill.forEach {pill->
            if(pill.bought) {
                amountStart+=pill.left!!
                if (pill.inside != "used") {
                    amountNow+=pill.leftNow!!
                }
            }
        }

        val progress = ((amountNow.toDouble() / amountStart.toDouble()) * 100).toInt()

        Log.d("1", "KURWA $progress $amountNow $amountStart ${pillOrganizer.listPill[0].name}")

        binding.progressTwo.progress =  progress
        binding.organizerText.text = "${amountNow}/${amountStart}"


        binding.apply {
            this.item = pillOrganizer
            executePendingBindings()
        }
    }

}