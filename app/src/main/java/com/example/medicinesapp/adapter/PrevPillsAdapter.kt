package com.example.medicinesapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.medicinesapp.R
import com.example.medicinesapp.data.PrevPill
import com.example.medicinesapp.databinding.PrevPillsItemBinding
import com.example.medicinesapp.utill.listeners.RecyclerViewItemClickListener

class PrevPillsAdapter(private val recipeList:List<PrevPill>,private val listener:RecyclerViewItemClickListener<PrevPill>): RecyclerView.Adapter<PrevPillsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrevPillsViewHolder {

        return PrevPillsViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.prev_pills_item, parent, false),listener)
    }


    override fun onBindViewHolder(holder: PrevPillsViewHolder, position: Int) {
        holder.bind(recipeList[position])
    }


    override fun getItemCount(): Int {
        return recipeList.size
    }

}

class PrevPillsViewHolder(private val binding: PrevPillsItemBinding,private val listener:RecyclerViewItemClickListener<PrevPill>): RecyclerView.ViewHolder(binding.root){

    init{
        binding.card.setOnClickListener {
            listener.onClickListener(binding.pill!!)
        }
    }

    fun bind(pill: PrevPill){

        binding.amount.text = "${pill.amount} ${pill.type}"

        if(pill.doctor == null){
            binding.person.text = "brak"
        }else{
            binding.person.text = pill.doctor
        }

        if(pill.listHour!=null){
           binding.hour.text = pill.listHour
        }else{
            binding.hour.text = "od ${pill.startHour} co ${pill.period} h"
        }

            if (pill.description.length < 3) {
                binding.additional.text = "brak dodatkowych informacji"
            } else {
                binding.additional.text = pill.description
            }

        binding.apply {
            this.pill = pill
            executePendingBindings()
        }
    }

}