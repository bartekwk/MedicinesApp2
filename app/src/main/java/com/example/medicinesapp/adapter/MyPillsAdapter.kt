package com.example.medicinesapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.medicinesapp.R
import com.example.medicinesapp.databinding.CategoryItemBinding
import com.example.medicinesapp.databinding.PillItemBinding
import com.example.medicinesapp.organizer.utills.RecyclerItem2


const val TYPE_SECTION = 0
const val TYPE_PILL = 1

class MyPillsAdapter(private val sectionedPills:List<RecyclerItem2>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return when(viewType){
           TYPE_SECTION ->SectionViewHolder(DataBindingUtil.inflate(
               LayoutInflater.from(parent.context),
               R.layout.category_item, parent, false
           ))
           TYPE_PILL->PillViewHolder( DataBindingUtil.inflate(
               LayoutInflater.from(parent.context),
               R.layout.pill_item, parent, false
           ))
           else -> throw IllegalStateException()
       }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = sectionedPills[position]){
            is RecyclerItem2.Pill -> (holder as PillViewHolder).bind(item)
            is RecyclerItem2.Section -> (holder as SectionViewHolder).bind(item)
        }
    }

    override fun getItemViewType(position: Int) = when (sectionedPills[position]){
        is RecyclerItem2.Pill -> TYPE_PILL
        is RecyclerItem2.Section -> TYPE_SECTION
    }

    override fun getItemCount() = sectionedPills.size
}


class SectionViewHolder(private val binding:CategoryItemBinding):RecyclerView.ViewHolder(binding.root){

    fun bind(section:RecyclerItem2.Section){

        if(section.isFirst){
            binding.line.translationY = 100.0f
        }

        binding.apply {
            this.section = section
            executePendingBindings()
        }
    }
}

class PillViewHolder(private val binding:PillItemBinding):RecyclerView.ViewHolder(binding.root){


    fun bind(pill:RecyclerItem2.Pill) {

        
        binding.apply {
            this.pill = pill
            executePendingBindings()
        }
    }
}