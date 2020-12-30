package com.example.medicinesapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.medicinesapp.R
import com.example.medicinesapp.data.DailyPill
import com.example.medicinesapp.data.PillFriendFirebase
import com.example.medicinesapp.databinding.PillsDetailItemBinding


class PillsDetailAdapter(private val recipeList:List<DailyPill>): RecyclerView.Adapter<PillsDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PillsDetailViewHolder {

        return PillsDetailViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.pills_detail_item, parent, false))
    }


    override fun onBindViewHolder(holder: PillsDetailViewHolder, position: Int) {
        holder.bind(recipeList[position])
    }


    override fun getItemCount(): Int {
        return recipeList.size
    }

}

class PillsDetailViewHolder(private val binding: PillsDetailItemBinding): RecyclerView.ViewHolder(binding.root){

    fun bind(pill: DailyPill){

        binding.apply {
            this.pill = pill
            executePendingBindings()
        }
    }
}