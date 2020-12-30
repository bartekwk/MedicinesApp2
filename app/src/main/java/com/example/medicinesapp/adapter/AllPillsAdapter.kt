package com.example.medicinesapp.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.medicinesapp.R
import com.example.medicinesapp.data.AllPillsFragmentData
import com.example.medicinesapp.data.PillDB
import com.example.medicinesapp.databinding.AllPillItemBinding
import com.example.medicinesapp.utill.listeners.TransitionClickItemListener

class AllPillsAdapter(private val recipeList:List<AllPillsFragmentData>, private val listener: TransitionClickItemListener<View>): RecyclerView.Adapter<AllViewHolder>() {

    private val mColors = arrayOf(
        "#3F51B5", "#FF9800", "#009688", "#673AB7", "#999999", "#454545", "#00FF00",
        "#FF0000", "#0000FF", "#800000", "#808000", "#00FF00", "#008000", "#00FFFF",
        "#000080", "#800080", "#f40059", "#0080b8", "#350040", "#650040", "#750040",
        "#45ddc0", "#dea42d", "#b83800", "#dd0244", "#c90000", "#465400",
        "#ff004d", "#ff6700", "#5d6eff", "#3955ff", "#0a24ff", "#004380", "#6b2e53",
        "#a5c996", "#f94fad", "#ff85bc", "#ff906b", "#b6bc68", "#296139"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllViewHolder {

        return AllViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.all_pill_item, parent, false),listener)
    }

    override fun onBindViewHolder(holder: AllViewHolder, position: Int) {


        val color = Color.parseColor(mColors[position % 40])
        holder.bind(recipeList[position],color)
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }
}


class AllViewHolder(private val binding: AllPillItemBinding,private val listener: TransitionClickItemListener<View>): RecyclerView.ViewHolder(binding.root){



    private var color:Int?=null

    init{

        binding.setClickListener{

            listener.onClickListener(binding.mcv,binding.pill!!,binding.color!!)
        }
    }

    fun bind(pill:AllPillsFragmentData,color: Int){


        this.color = color


        binding.apply {
            this.pill=pill
            this.color = color
            executePendingBindings()
        }

        if(pill.leftOrganizer ==null){
            pill.leftOrganizer = 0.0
            binding.organizerText.text = ""
        }

        if(pill.leftNowOrganizer ==null){
            pill.leftOrganizer = 0.0
        }

    }


}