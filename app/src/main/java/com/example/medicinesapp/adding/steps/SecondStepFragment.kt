package com.example.medicinesapp.adding.steps

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import io.reactivex.Observable
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.medicinesapp.R
import com.example.medicinesapp.databinding.SecondFragmentBinding
import com.example.medicinesapp.rxBus.MyRxBus
import com.example.medicinesapp.rxBus.RxEvent
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.*
import java.util.concurrent.TimeUnit


@SuppressLint("SetTextI18n")
class SecondStepFragment:Fragment() {

    private lateinit var binding:SecondFragmentBinding
    private lateinit var disposable: Disposable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = SecondFragmentBinding.inflate(inflater, container, false)


        binding.card1.setOnClickListener {
            startSingleCalendar()
        }


        binding.card3.setOnClickListener {
            startRangeCalendar()
        }

        disposable = MyRxBus.listen(RxEvent.EventQuery::class.java).subscribe {
            binding.inside1.setText(it.query)
        }


        return binding.root
    }


    override fun onStart() {
        super.onStart()

        val queryTextFlowable = textWatcherObservable()
            .toFlowable(BackpressureStrategy.BUFFER)

        disposable = queryTextFlowable
            .subscribeOn(AndroidSchedulers.mainThread()).
            observeOn(AndroidSchedulers.mainThread()).subscribe{
                MyRxBus.publish(RxEvent.EventQuery(it))
            }

    }

    override fun onStop() {
        super.onStop()
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }


    private fun textWatcherObservable():Observable<String>{

        val textChangeObservable = Observable.create<String> { emitter ->

            val textWatcher = object : TextWatcher {

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    p0?.toString()?.let { emitter.onNext(it) }
                }

                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

            }

            binding.inside1.addTextChangedListener(textWatcher)

            emitter.setCancellable {
                binding.inside1.removeTextChangedListener(textWatcher)
            }

        }

        return textChangeObservable
            .filter { it.length >= 2 }
            .debounce(1000, TimeUnit.MILLISECONDS)
    }


    private fun startRangeCalendar(){

        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setTitleText("Ustaw zakres przyjmowania lekarstwa")

        val constraintsBuilder = CalendarConstraints.Builder()
        val calendar = Calendar.getInstance()
        val dateValidator = DateValidatorPointForward.from(calendar.timeInMillis)
        constraintsBuilder.setValidator(dateValidator)
        builder.setCalendarConstraints(constraintsBuilder.build())
        builder.setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)

        builder.setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
        val picker = builder.build()
        picker.show(requireActivity().supportFragmentManager, picker.toString())

        picker.addOnPositiveButtonClickListener {

            val timeZone = TimeZone.getDefault()
            val zoneOffset = timeZone.getOffset(Date().time) * -1

            val startDate = it.first?.let { it1 -> Date(it1 + zoneOffset) }
            val endDate = it.second?.let { it1 -> Date(it1 + zoneOffset) }

            val listDates = getDaysBetweenDates(startDate,endDate)

            if (listDates != null) {
                MyRxBus.publish(RxEvent.EventDate(listDates))
            }
        }
    }


    private fun startSingleCalendar(){

        val builder = MaterialDatePicker.Builder.datePicker()
        builder.setTitleText("Ustaw początkową datę przyjmowania lekarstwa")

        val constraintsBuilder = CalendarConstraints.Builder()
        val calendar = Calendar.getInstance()
        val dateValidator = DateValidatorPointForward.from(calendar.timeInMillis)
        constraintsBuilder.setValidator(dateValidator)
        builder.setCalendarConstraints(constraintsBuilder.build())

        val picker = builder.build()
        picker.show(requireActivity().supportFragmentManager, picker.toString())

        picker.addOnPositiveButtonClickListener {

            val timeZone = TimeZone.getDefault()
            val zoneOffset = timeZone.getOffset(Date().time) * -1
            val date = Date(it + zoneOffset)

            val timestamp = date.time

            val bundle = bundleOf("date" to timestamp)

            findNavController().navigate(R.id.action_mainAdd_to_noDays,bundle)

        }
    }



    companion object {
         fun getDaysBetweenDates(startDate: Date?, endDate: Date?): List<String>? {

            val calendar: Calendar = GregorianCalendar()
            val calendarOut: Calendar = GregorianCalendar()
            val datesOut = mutableListOf<String>()

            calendar.time = startDate!!
            while (calendar.time.before(endDate)) {
                val result = calendar.time
                calendarOut.time = result
                val all = returnDateString(calendarOut)
                datesOut.add(all)
                calendar.add(Calendar.DATE, 1)
            }

            calendarOut.time = endDate!!

            val allEnd = returnDateString(calendarOut)
            datesOut.add(allEnd)

            return datesOut
        }


        private fun returnDateString(calendarOut: Calendar): String {

            val year = calendarOut.get(Calendar.YEAR)
            val resultMonth = calendarOut.get(Calendar.MONTH)
            var month = (resultMonth + 1).toString()
            var day = calendarOut.get(Calendar.DAY_OF_MONTH).toString()

            if (day.length == 1) {
                day = "0$day"
            }
            if(month.length ==1){
                month="0$month"
            }

            return "$year-$month-$day"
        }
    }

}