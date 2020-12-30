package com.example.medicinesapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.medicinesapp.R
import com.example.medicinesapp.data.UserBothChecked
import com.example.medicinesapp.databinding.FriendItemBinding
import com.example.medicinesapp.utill.listeners.RecyclerViewItemClickListener
import com.example.medicinesapp.utill.listeners.RecyclerViewItemSwipeListener
import java.text.SimpleDateFormat
import java.util.*

class FriendsAdapter(private val listenerClick:RecyclerViewItemClickListener<UserBothChecked>,
                     private val listenerSwipe: RecyclerViewItemSwipeListener<UserBothChecked>,
                     private val listenerClick2 : RecyclerViewItemClickListener<UserBothChecked>): ListAdapter<UserBothChecked, FriendsAdapter.ViewHolder>(FriendsCallback()){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.friend_item,parent,false),listenerClick,listenerSwipe,listenerClick2)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class ViewHolder(private val binding: FriendItemBinding,
                     private val listenerClick:RecyclerViewItemClickListener<UserBothChecked>,
                     private val listenerSwipe:RecyclerViewItemSwipeListener<UserBothChecked>,
                     private val listenerClick2 : RecyclerViewItemClickListener<UserBothChecked>): RecyclerView.ViewHolder(binding.root){


        init {
            binding.call.setOnClickListener {
                listenerClick.onClickListener(binding.friend!!)
            }
            
            binding.switcher.setOnCheckedChangeListener { _, b ->
                listenerSwipe.onSwipeListener(binding.friend!!,b)
            }
            binding.setClickListener {
                listenerClick2.onClickListener(binding.friend!!)
            }
        }

        fun bind(friend: UserBothChecked){
            with(binding){

                var isWoman:Boolean

                if(friend.isDoctor){
                    binding.accountType.text = "Konto lekarza"
                    if (friend.name.toLowerCase(Locale.ROOT).endsWith("a") && friend.name.toLowerCase(Locale.ROOT)!="kuba"){
                        binding.image.setImageResource(R.drawable.ic_doctorw)
                        isWoman = true
                    }else{
                        binding.image.setImageResource(R.drawable.ic_doctorm)
                        isWoman = false
                    }
                }else{
                    binding.accountType.text = "Konto pacjenta"
                    if (friend.name.toLowerCase(Locale.ROOT).endsWith("a") && friend.name.toLowerCase(Locale.ROOT)!="kuba"){
                        binding.image.setImageResource(R.drawable.ic_woman)
                    isWoman = true}
                    else{
                        binding.image.setImageResource(R.drawable.ic_man)
                        isWoman = false}
                }

                if(friend.isOnline){
                    binding.call.alpha = 1.0f
                    binding.call.isEnabled = true
                    binding.image2.setImageResource(R.drawable.ic_record2)
                    if(isWoman) {
                        binding.online.text = "Dostępna"
                    }else{
                        binding.online.text = "Dostępny"
                    }
                }else{
                    binding.call.alpha = 0.0f
                    binding.call.isEnabled = false
                    binding.image2.setImageResource(R.drawable.ic_record)
                    val lastSeen = friend.lastActive
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm ")
                    val netDate = Date(lastSeen)
                    val date =sdf.format(netDate)
                    binding.online.text = "Ostatnio: $date"
                }
                if(friend.isCheckedTheir==true){
                    binding.visibility.alpha=1.0f
                }else{
                    binding.visibility.alpha=0.0f
                }
                this.friend=friend
                executePendingBindings()
            }
        }
    }
}


class FriendsCallback: DiffUtil.ItemCallback<UserBothChecked>(){
    override fun areItemsTheSame(oldItem: UserBothChecked, newItem: UserBothChecked): Boolean {
        return oldItem.uid==newItem.uid
    }

    override fun areContentsTheSame(oldItem: UserBothChecked, newItem: UserBothChecked): Boolean {
        return oldItem==newItem
    }

}