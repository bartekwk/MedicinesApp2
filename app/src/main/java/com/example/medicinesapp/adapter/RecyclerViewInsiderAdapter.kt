package com.example.medicinesapp.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.medicinesapp.data.FromInternetWrapper
import com.example.medicinesapp.databinding.ViewPagerItemBinding
import com.example.medicinesapp.utill.listeners.ViewPagerItemClickListener


class RecyclerViewInsiderAdapter(private val recipeList:List<FromInternetWrapper>, private val listener: ViewPagerItemClickListener<Bundle>): RecyclerView.Adapter<CustomViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = ViewPagerItemBinding.inflate(inflater, parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

        val currentItem = recipeList[position]
        val itemBinding = holder.binding as ViewPagerItemBinding
        itemBinding.wrapper = currentItem
        itemBinding.doseLeft = currentItem.doseLeft
        itemBinding.clickListener = listener
        itemBinding.recyclerView.setRecycledViewPool(viewPool)
        itemBinding.executePendingBindings()

    }


    override fun getItemCount(): Int {
        return recipeList.size
    }

}

class CustomViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)



