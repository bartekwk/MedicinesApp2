package com.example.medicinesapp.adapter

import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.medicinesapp.data.FromInternet
import com.example.medicinesapp.utill.listeners.ViewPagerItemClickListener



@BindingAdapter(value=["dateStart","dateEnd"])
fun setDate(view:TextView,dateStart:String,dateEnd:String){

    view.text = "$dateStart - $dateEnd"
}


@BindingAdapter("bindImage")
fun bindImage(view: ImageView, url:String?) {
    Glide.with(view.context)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(view)
}

@BindingAdapter("valueToString")
fun valueToString(view: TextView,value:Int?){

    value?.let {
        view.text = it.toString()
    }
}

@BindingAdapter("valueStringToString")
fun valueStringToString(view: TextView,value:String?){

    value?.let {
        view.text = it
    }
}

@BindingAdapter("valueDoubleToString")
fun valueDoubleToString(view: TextView,value:Double?){

    value?.let {
        view.text = it.toString()
    }
}

@BindingAdapter(value=["setItems","setItemListener","doseLeft"])
fun setAllItems(view: RecyclerView, items:List<FromInternet>, listener: ViewPagerItemClickListener<Bundle>, doseLeft:Int){

    val adapter = InternetPriceAdapter3(items,doseLeft.toDouble(),listener)
    view.adapter = adapter
    adapter.notifyDataSetChanged()
}

@BindingAdapter(value=["all","left"])
fun calculateProgress(view:ProgressBar, all:Double?,left:Double?){

    if(all!=null && left!=null && all!=0.0) {
        val result = (left / all) * 100
        val result2 = result.toInt()
        view.progress = result2
    }
    else{
        view.progress = 0
    }
}

@BindingAdapter(value=["allTxt","leftTxt"])
fun calculateProgressText(view:TextView,allTxt:Double?,leftTxt:Double?){
    if(allTxt!=null && leftTxt!=null && allTxt!=0.0 && leftTxt!=0.0) {

        var leftText = leftTxt.toInt()

        if(leftText<0){
            leftText = 0
        }

        view.text = "$leftText/${allTxt.toInt()}"
    }else{
        view.text = ""
    }
}

@BindingAdapter(value=["allInt","leftInt"])
fun calculateProgressInt(view:ProgressBar, allInt:Int?,leftInt:Int?){

    if(allInt!=null && leftInt!=null) {
        val result = (leftInt.toDouble() / allInt.toDouble()) * 100
        val result2 = result.toInt()
        view.progress = result2
    }
}

@BindingAdapter(value=["allTxtInt","leftTxtInt"])
fun calculateProgressTextInt(view:TextView,allTxtInt:Int?,leftTxtInt:Int?){
    if(allTxtInt!=null && leftTxtInt!=null && allTxtInt!=0) {
        view.text = "${leftTxtInt}/${allTxtInt}"
    }
}



