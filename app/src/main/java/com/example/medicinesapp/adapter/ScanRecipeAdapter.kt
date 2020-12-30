package com.example.medicinesapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.medicinesapp.R
import com.example.medicinesapp.data.FromScan
import com.example.medicinesapp.databinding.ScanRecipeItemBinding
import com.example.medicinesapp.utill.listeners.RecyclerViewItemClickListener

class ScanRecipeAdapter(private val recipeList:List<FromScan>,private val clickListener:RecyclerViewItemClickListener<FromScan>):RecyclerView.Adapter<ScanViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanViewHolder {

        return ScanViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.scan_recipe_item, parent, false),clickListener)
    }


    override fun onBindViewHolder(holder: ScanViewHolder, position: Int) {
        holder.bind(recipeList[position])
    }


    override fun getItemCount(): Int {
        return recipeList.size
    }
}


class ScanViewHolder(private val binding: ScanRecipeItemBinding,private val clickListener:RecyclerViewItemClickListener<FromScan>):RecyclerView.ViewHolder(binding.root){


    init{
       binding!!.card.setOnClickListener {
           binding.card.toggle()
           binding.scan!!.isClicked = true
           clickListener.onClickListener(binding.scan!!)
       }
    }

    fun bind(scan:FromScan){
        binding.apply {
            this.scan = scan
            executePendingBindings()
        }
        binding.card.isChecked = scan.isClicked
    }
}