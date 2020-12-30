package com.example.medicinesapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.medicinesapp.R
import com.example.medicinesapp.data.DailyPill
import com.example.medicinesapp.data.PillFriendFirebase
import com.example.medicinesapp.databinding.FriendsPillItemBinding
import com.example.medicinesapp.databinding.PillsDetailItemBinding


class FirebasePillAdapter(private val pillsList:List<PillFriendFirebase>): RecyclerView.Adapter<FirebasePillViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FirebasePillViewHolder {

        return FirebasePillViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.friends_pill_item, parent, false))
    }


    override fun onBindViewHolder(holder: FirebasePillViewHolder, position: Int) {
        holder.bind(pillsList[position])
    }


    override fun getItemCount(): Int {
        return pillsList.size
    }

}

class FirebasePillViewHolder(private val binding: FriendsPillItemBinding): RecyclerView.ViewHolder(binding.root){

    fun bind(pill: PillFriendFirebase){

        binding.apply {
            this.pill = pill
            executePendingBindings()
        }
    }
}