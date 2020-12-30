package com.example.medicinesapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.example.medicinesapp.R
import com.example.medicinesapp.data.UserBothChecked
import com.example.medicinesapp.databinding.SpinnerUserItemBinding

class SpinnerAdapterUser(context: Context, var list: MutableList<UserBothChecked>) : ArrayAdapter<UserBothChecked>(context, 0, list) {


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {

        return  returnView(position,parent)
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        return  returnView(position,parent)
    }



    private fun returnView(position: Int, parent: ViewGroup):View {
        val binding: SpinnerUserItemBinding = DataBindingUtil
            .inflate(LayoutInflater.from(context), R.layout.spinner_user_item, parent, false)

        binding.run {
            this.user = list[position]
            executePendingBindings()
        }

        return binding.root
    }


}


