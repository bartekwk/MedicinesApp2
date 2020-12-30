package com.example.medicinesapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.medicinesapp.R
import com.example.medicinesapp.data.PillDB
import com.example.medicinesapp.databinding.InternetDialogItemBinding

class InternetDialogAdapter(private val recipeList:List<PillDB>): RecyclerView.Adapter<InternetDialogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InternetDialogViewHolder {

        return InternetDialogViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.internet_dialog_item, parent, false))
    }


    override fun onBindViewHolder(holder: InternetDialogViewHolder, position: Int) {
        holder.bind(recipeList[position])
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }
}

class InternetDialogViewHolder(private val binding: InternetDialogItemBinding): RecyclerView.ViewHolder(binding.root){

    fun bind(info: PillDB){

        val sum = info.amount *info.doseLeft!!.toDouble()

        binding.sum.text = "$sum ${info.type}"

        binding.run {
            this.pill = info
            executePendingBindings()
        }
    }

}