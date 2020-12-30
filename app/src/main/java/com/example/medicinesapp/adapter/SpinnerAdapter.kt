package com.example.medicinesapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.example.medicinesapp.R
import com.example.medicinesapp.databinding.SpinnerItemBinding

class SpinnerAdapter(context: Context, var list: MutableList<String>) :
    ArrayAdapter<String>(context, 0, list) {


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {


       return  returnView(position,parent)
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        return  returnView(position,parent)
    }



    private fun returnView(position: Int, parent: ViewGroup):View {
        val binding: SpinnerItemBinding = DataBindingUtil
            .inflate(LayoutInflater.from(context), R.layout.spinner_item, parent, false)

        binding.run {
            this.item = list[position]
            executePendingBindings()
        }

        return binding.root
    }


}


