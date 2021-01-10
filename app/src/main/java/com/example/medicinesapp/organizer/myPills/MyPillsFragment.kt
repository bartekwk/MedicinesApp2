package com.example.medicinesapp.organizer.myPills

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.medicinesapp.R
import com.example.medicinesapp.adapter.MyPillsAdapter
import com.example.medicinesapp.databinding.CalendarMeineBinding
import com.example.medicinesapp.organizer.utills.RecyclerItem2
import com.example.medicinesapp.organizer.utills.SelectorDecorator
import com.example.medicinesapp.organizer.utills.TodayDecorator
import com.example.medicinesapp.utill.Helper
import com.prolificinteractive.materialcalendarview.CalendarDay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class MyPillsFragment: Fragment() {

    private lateinit var binding:CalendarMeineBinding
    private lateinit var disposable: Disposable
    private val viewModel: MyPillsViewModel by viewModels {
        MyPillsViewModelFactory(
            Helper.provideRepository(
                requireContext()
            )
        )
    }

    private val pills = mutableListOf<RecyclerItem2>()
    private lateinit var adapter:MyPillsAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CalendarMeineBinding.inflate(inflater,container,false)
        adapter = MyPillsAdapter(pills)
        binding.recycler.adapter = adapter

        val calendar = binding.calendarView
        calendar.addDecorators(SelectorDecorator(requireContext()),TodayDecorator(requireContext(),
            R.color.colorMyPrimaryDark))


        val today = CalendarDay.today()
        getPillsByDay(today)
        calendar.currentDate = today
        calendar.selectedDate = today


        calendar.setOnDateChangedListener { _, date, _ ->
            getPillsByDay(date)
        }

        return binding.root
    }


    private fun getPillsByDay(date:CalendarDay){

        val year = date.year.toString()
        var month = date.month.toString()
        var day = date.day.toString()

        if(month.length==1){
            month = "0$month"
        }
        if(day.length==1){
            day = "0$day"
        }
        val format = "$year-$month-$day"

        disposable =
            viewModel
                .getDailyPill(format)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { list->
                    val result = list
                        .groupBy { it.time }
                        .flatMap { (category,pills)->
                            listOf<RecyclerItem2>(RecyclerItem2.Section(category,false))+ pills.map{RecyclerItem2.Pill(it)} }

                    if(result.isNotEmpty()) {
                        when (val first = result.first()) {
                            is RecyclerItem2.Section -> {
                                first.isFirst = true
                            }
                            else -> Log.d("1", "Sth else ")
                        }
                    }

                    pills.clear()
                    Log.d("1", "ACOTO $result ")
                    pills.addAll(result)

                    adapter.notifyDataSetChanged()
                }
    }


    override fun onDestroy() {
        super.onDestroy()
        if(!disposable.isDisposed)disposable.dispose()
    }

    override fun onPause() {
        super.onPause()
        if(!disposable.isDisposed)disposable.dispose()
    }


}