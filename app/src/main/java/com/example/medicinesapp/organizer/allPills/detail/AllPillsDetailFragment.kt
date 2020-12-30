package com.example.medicinesapp.organizer.allPills.detail

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.medicinesapp.R
import com.example.medicinesapp.adapter.PillsDetailAdapter
import com.example.medicinesapp.data.DailyPill
import com.example.medicinesapp.databinding.AllPillsDetailBinding
import com.example.medicinesapp.databinding.CalendarDayBinding
import com.example.medicinesapp.databinding.CalendarHeaderBinding
import com.example.medicinesapp.utill.Helper
import com.google.android.material.transition.MaterialContainerTransform
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.previous
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class AllPillsDetailFragment:Fragment() {

    private lateinit var binding:AllPillsDetailBinding
    private lateinit var adapter: PillsDetailAdapter

    private var listAdapterPills = mutableListOf<DailyPill>()
    private var listPills = mutableListOf<DailyPill>()
    private var selectedDate: LocalDate? = null
    private var disposable: Disposable?=null
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")
    private val viewModel: AllPillsDetailViewModel by viewModels { AllPillsDetailViewModelFactory(Helper.provideRepository(requireContext())) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AllPillsDetailBinding.inflate(inflater,container,false)

        binding.pill = arguments?.getParcelable("pill")
        val color = arguments?.getInt("color")
        val fromFirebase = arguments?.getBoolean("fromFirebase")!!
        val friendID = arguments?.getString("friendID")!!

        color?.let {
            binding.color = it
        }

        binding.delete.setOnClickListener {
            val pill = binding.pill!!
            viewModel.deleteOnePill(pill.id)
            viewModel.deletePillFirebase(pill.id)
            findNavController().previousBackStackEntry?.savedStateHandle?.set("pill", pill)
            findNavController().popBackStack()

        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })

        if(!fromFirebase) {
            disposable = viewModel
                .getAllPillsByID(binding.pill!!.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                listPills.addAll(it)
            }
        }else{
            viewModel.getOneUserPrescription(friendID,binding.pill!!.id)
        }

        adapter = PillsDetailAdapter(listAdapterPills)
        binding.recycler.adapter = adapter


        viewModel.pill.observe(viewLifecycleOwner, androidx.lifecycle.Observer {pill->


            pill.listDays!!.forEach { days->

                val split = days.split("&AND&")
                val day = split.first()
                val time = split.last()

                val description = pill.description?:""
                val dailyPill = DailyPill(pill.name,description,pill.doctor,day,time,pill.amount!!,pill.type)

                listPills.add(dailyPill)
            }
        })
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {

            delay(610)

            binding.linearLayout.alpha = 1.0f

            val daysOfWeek =
                daysOfWeekFromLocale()
            val currentMonth = YearMonth.now()
            binding.exFiveCalendar.setup(
                currentMonth.minusMonths(10),
                currentMonth.plusMonths(10),
                daysOfWeek.first()
            )
            binding.exFiveCalendar.scrollToMonth(currentMonth)


            class DayViewContainer(view: View) : ViewContainer(view) {
                lateinit var day: CalendarDay
                val binding = CalendarDayBinding.bind(view)

                init {
                    view.setOnClickListener {
                        if (day.owner == DayOwner.THIS_MONTH) {
                            if (selectedDate != day.date) {
                                val oldDate = selectedDate
                                selectedDate = day.date
                                val binding = this@AllPillsDetailFragment.binding
                                binding.exFiveCalendar.notifyDateChanged(day.date)
                                oldDate?.let { binding.exFiveCalendar.notifyDateChanged(it) }

                                selectedDate?.let {
                                    val pillsDay = getPillsByDay(it)

                                    if (pillsDay.isNotEmpty()) {
                                        listAdapterPills.clear()
                                        listAdapterPills.addAll(pillsDay)
                                        adapter.notifyDataSetChanged()
                                    } else {
                                        listAdapterPills.clear()
                                        adapter.notifyDataSetChanged()
                                    }
                                }
                            }
                        }
                    }
                }
            }


            binding.exFiveCalendar.dayBinder = object : DayBinder<DayViewContainer> {
                override fun create(view: View) = DayViewContainer(view)
                override fun bind(container: DayViewContainer, day: CalendarDay) {
                    container.day = day
                    val textView = container.binding.exFiveDayText
                    val layout = container.binding.exFiveDayLayout
                    textView.text = day.date.dayOfMonth.toString()

                    val one = container.binding.one
                    val two = container.binding.two

                    one.alpha = 0f

                    if (day.owner == DayOwner.THIS_MONTH) {
                        textView.setTextColorRes(R.color.example_5_text_grey)
                        layout.setBackgroundResource(if (selectedDate == day.date) R.drawable.example_5_selected_bg else 0)

                        val pillsDay = getPillsByDay(day.date)

                        if (pillsDay.isNotEmpty()) {
                            one.alpha = 1f
                        }
                    } else {
                        textView.setTextColorRes(R.color.example_5_text_grey_light)
                        one.alpha = 0f
                        layout.background = null
                    }
                }
            }

            class MonthViewContainer(view: View) : ViewContainer(view) {
                val legendLayout = CalendarHeaderBinding.bind(view).legendLayout
            }


            binding.exFiveCalendar.monthHeaderBinder =
                object : MonthHeaderFooterBinder<MonthViewContainer> {
                    override fun create(view: View) = MonthViewContainer(view)
                    override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                        if (container.legendLayout.tag == null) {
                            container.legendLayout.tag = month.yearMonth
                            container.legendLayout.children.map { it as TextView }
                                .forEachIndexed { index, tv ->
                                    tv.text = daysOfWeek[index].getDisplayName(
                                        TextStyle.SHORT,
                                        Locale.ENGLISH
                                    )
                                        .toUpperCase(Locale.ENGLISH)
                                    tv.setTextColorRes(R.color.example_5_text_grey)
                                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                                }
                            month.yearMonth
                        }
                    }
                }

            binding.exFiveCalendar.monthScrollListener = { month ->
                val title = "${monthTitleFormatter.format(month.yearMonth)} ${month.yearMonth.year}"
                binding.exFiveMonthYearText.text = title

                selectedDate?.let {
                    selectedDate = null
                    binding.exFiveCalendar.notifyDateChanged(it)
                    listAdapterPills.clear()
                    adapter.notifyDataSetChanged()
                }
            }

            binding.exFiveNextMonthImage.setOnClickListener {
                binding.exFiveCalendar.findFirstVisibleMonth()?.let {
                    binding.exFiveCalendar.smoothScrollToMonth(it.yearMonth.next)
                }
            }

            binding.exFivePreviousMonthImage.setOnClickListener {
                binding.exFiveCalendar.findFirstVisibleMonth()?.let {
                    binding.exFiveCalendar.smoothScrollToMonth(it.yearMonth.previous)
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host
            duration = 500.toLong()
            scrimColor = Color.TRANSPARENT
        }
    }

    private fun getPillsByDay(day:LocalDate): List<DailyPill> {
        var dayOfMonth = day.dayOfMonth.toString()
        var month = day.monthValue.toString()
        val year = day.year.toString()

        if(month.length==1){
            month = "0$month"
        }
        if(dayOfMonth.length==1){
            dayOfMonth = "0$dayOfMonth"
        }

        val all = "$year-$month-$dayOfMonth"

        return listPills.filter { pill->pill.date == all }
    }


    override fun onDestroy() {
        super.onDestroy()

        disposable?.let {
            if(it.isDisposed){
                it.dispose()
            }
        }
        disposable?.dispose()
    }


    override fun onPause() {
        super.onPause()
        disposable?.let {
            if(it.isDisposed){
                it.dispose()
            }
        }
    }


}