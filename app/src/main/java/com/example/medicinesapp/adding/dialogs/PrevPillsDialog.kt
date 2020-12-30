package com.example.medicinesapp.adding.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicinesapp.adapter.PrevPillsAdapter
import com.example.medicinesapp.adding.dialogs.vm.PrevPillsViewModel
import com.example.medicinesapp.adding.dialogs.vm.PrevPillsViewModelFactory
import com.example.medicinesapp.data.PrevPill
import com.example.medicinesapp.databinding.PrevPillsDialogBinding
import com.example.medicinesapp.rxBus.MyRxBus
import com.example.medicinesapp.rxBus.RxEvent
import com.example.medicinesapp.utill.Helper
import com.example.medicinesapp.utill.listeners.RecyclerViewItemClickListener
import java.text.SimpleDateFormat
import java.util.*

class PrevPillsDialog:DialogFragment() {

    private lateinit var binding : PrevPillsDialogBinding
    private var listPrevPills = mutableListOf<PrevPill>()
    private lateinit var adapter:PrevPillsAdapter

    private val viewModel: PrevPillsViewModel by viewModels {
        PrevPillsViewModelFactory(
            Helper.provideRepository(
                requireContext()
            ), requireActivity().application
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PrevPillsDialogBinding.inflate(inflater,container,false)
        adapter = PrevPillsAdapter(listPrevPills,object :RecyclerViewItemClickListener<PrevPill>{
            override fun onClickListener(item: PrevPill) {
                MyRxBus.publish(RxEvent.PrevPillChosen(item))
                dismiss()
            }
        })

        val linearLayoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        binding.recycler.layoutManager = linearLayoutManager

        binding.recycler.adapter = adapter

        viewModel.getPreviousPills()

        viewModel.pills.observe(viewLifecycleOwner, Observer {newList->


            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val newCalendar = Calendar.getInstance()
            val prevCalendar = Calendar.getInstance()

            newList.groupBy { pill -> pill.id }.forEach { (_, list) ->

                val pill = list.first()
                val output = calculateHours(list,newCalendar,prevCalendar,sdf)
                inputHours(output,pill)
                listPrevPills.add(pill)
                adapter.notifyDataSetChanged()
            }
        })

        return binding.root
    }


    private fun calculateHours(list:List<PrevPill>,newCalendar:Calendar,prevCalendar:Calendar,sdf:SimpleDateFormat): String {

        var firstTime = true
        var prevHour:Double?=null
        val listOfHours = mutableListOf<String>()
        var isDifferent = false
        var periodHour = 0.0
        var startHour = ""


        list.forEach { item ->
            newCalendar.time = sdf.parse("${item.date} ${item.time}")

            if(!listOfHours.contains(item.time)) {
                listOfHours.add(item.time)
            }

            if (!firstTime) {
                val diff: Long = newCalendar.timeInMillis - prevCalendar.timeInMillis
                val seconds = (diff / 1000).toDouble()
                val minutes = seconds / 60
                val hours = minutes/60
                periodHour = hours

                if(prevHour!=null){
                    val difference = prevHour!!-hours
                    if (difference!=0.0){
                        isDifferent = true
                    }
                }
                prevHour = hours
            }else{
                startHour = item.time
            }
            prevCalendar.time = sdf.parse("${item.date} ${item.time}")
            firstTime = false
        }


        var output = ""

        output = if(isDifferent){
            //Log.d("1", "DIFFERENT $startHour")
            val result = listOfHours.joinToString(separator = " NEXT ")
            "JEDEN $result"
        } else{
            //Log.d("1", " START $startHour CO $periodHour ")
            "DWA $startHour NEXT $periodHour "
        }
        return output
    }


    private fun inputHours(output:String,pill: PrevPill){

        if(output.startsWith("DWA")){
            val split = output.split(" NEXT ")
            val startHour = split.first().split(' ').last()
            val period = split.last()
            pill.startHour = startHour
            pill.period = period
        }
        if(output.startsWith("JEDEN")){
            val split = output.split(" NEXT ").toMutableList()
            split[0] = split[0].split(' ').last()
            pill.listHour = split.joinToString(separator = ",")
        }
    }

}