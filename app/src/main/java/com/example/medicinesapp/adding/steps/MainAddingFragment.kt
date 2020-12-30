package com.example.medicinesapp.adding.steps


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.medicinesapp.R
import com.example.medicinesapp.adapter.AddingFragmentAdapter
import com.example.medicinesapp.data.*
import com.example.medicinesapp.databinding.MainAddingFragmentBinding
import com.example.medicinesapp.rxBus.MyRxBus
import com.example.medicinesapp.rxBus.RxEvent
import com.example.medicinesapp.utill.Helper
import io.jmdg.rxbus.RxBus
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.main_adding_fragment.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainAddingFragment: Fragment() {

    private lateinit var binding:MainAddingFragmentBinding
    private lateinit var viewPager: ViewPager2
    private val mySubject = PublishSubject.create<String>()
    private  var disposable: Disposable?=null

    private var name:String?=null
    private var days = mutableListOf<String>()
    private var hours = mutableListOf<String>()
    private var hourStart:String?=null
    private var period:String?=null
    private var info:String?=null
    private var dose:Double=1.0
    private var choice:String = "sztuk"
    private var start:Int?=null
    private var patient = ""

    private val dataInserted = mutableListOf<MainAddingSingleData>()

    private var currentItem = 0

    private var listID = mutableListOf<String>()
    private var daysFirebase = mutableListOf<String>()

    private var addingOk = false
    private var editPreviousMode = false


    private val viewModel: AddingViewModel by viewModels {
        AddingViewModelFactory(
            Helper.provideRepository(requireContext()),
            requireActivity().application
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= MainAddingFragmentBinding.inflate(inflater, container, false)
        handlePrevForw()

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_mainAdd_to_dashboard)
            }
        })


        binding.namePill.setOnClickListener {
            startPrevPillsChooser()
        }

        viewModel.currentUser.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            patient = it.name
        })


        enableButtons(false)

        viewPager = binding.viewPager
        val adapter = AddingFragmentAdapter(this, mySubject)
        viewPager.offscreenPageLimit=4
        viewPager.adapter = adapter
        binding.dotsIndicator.setViewPager2(binding.viewPager)


        binding.cardView4.setOnClickListener {
            binding.motion.setTransition(R.id.end2,R.id.end)
            binding.motion.transitionToEnd()
            editPreviousMode = true
        }

        binding.cardVie4.setOnClickListener {

            binding.motion.setTransition(R.id.end2,R.id.end)
            binding.motion.transitionToEnd()
            editPreviousMode = true
        }

        binding.plus.setOnClickListener {

            editPreviousMode = false

            name = null
            days.clear()
            hours.clear()
            hourStart = null
            period=null
            info=null
            dose=1.0
            choice = "sztuk"
            start=null

            val adapter = AddingFragmentAdapter(this, mySubject)
            binding.viewPager.adapter = adapter

            binding.motion.setTransition(R.id.end2,R.id.end)
            binding.motion.transitionToEnd()

            binding.nameOfPill.text = ""
            binding.nameOfPill2.text =""
            binding.nameOfPill3.text=""
        }

        binding.cardView3.setOnClickListener {
            saveAllDataToDB()
        }

        binding.cardVie3.setOnClickListener {
            saveAllDataToDB()
        }

        handleRxEvents()
        handlePrescriptionPhoto()

        return binding.root
    }



    private fun saveSingleDataLocally(){

        val doctor:String?=null
        var nameOut:String?=null
        var dayStartOut:String?=null
        var dayEndOut:String?=null

        var periodOut:String?=null
        var hourStartOut:String?=null
        val hoursOut = mutableListOf<String>()
        val daysOut = mutableListOf<String>()
        var hourChosen = false


        if(!name.isNullOrEmpty()){
            nameOut = name
        }else{
            Log.d("1", "CUOO 1 BRAK IMIENIA ")
        }

        if(days.size>0){
            dayStartOut = days.first()
            dayEndOut = days.last()
            daysOut.addAll(days)
            Log.d("1", "CUOO $daysOut ")
        }else{
            Log.d("1", "CUOO 1 BRAK DNI ")
        }


        if(!period.isNullOrEmpty() && !hourStart.isNullOrEmpty()){
            periodOut = period
            hourStartOut = hourStart
            hourChosen = true
        }

        if(hours.size>0){
            hoursOut.addAll(hours)
            hourChosen = true
        }

        if(!hourChosen){
            Log.d("1", "CUOO 1 BRAK GODZIN ")
        }


        if(!nameOut.isNullOrEmpty() && !dayStartOut.isNullOrEmpty() && !dayEndOut.isNullOrEmpty() && hourChosen ){

            val id = "bartek-${System.currentTimeMillis()}"
            listID.add(id)
            val singleData = MainAddingSingleData(id, nameOut, dayStartOut, dayEndOut, daysOut, patient, doctor, dose, info, choice, periodOut, hourStartOut, hoursOut)

            if(!editPreviousMode) {
                dataInserted.add(singleData)
                Log.d("1", "CUOO 1 DODANO $singleData ")
            }else{
                dataInserted[currentItem] = singleData
            }
            addingOk = true
        }
    }



    private fun saveAllDataToDB(){

        dataInserted.forEach {data->


           val info = if(data.info==null || data.info ==""){
                "brak"
            }else{
                data.info!!
            }

            val pill = PillDB(data.id,data.name,info,data.type,null,null,data.dayStart,data.lastDay,data.patient,data.dose,data.doctor)
            viewModel.insertPill(pill)

            var doseLeft = 0
            val period = data.period
            val hourStart = data.hourStart
            val days = data.days
            val id = data.id
            val hours = data.hours


            if (period != null && hourStart != null ) {

                val first = days.first()
                val last = days.last()

                val sdf = SimpleDateFormat("yyyy-MM-dd + HH:mm")

                val cal2: Calendar = Calendar.getInstance()
                cal2.time = sdf.parse("$last + 23:59")


                val cal1: Calendar = Calendar.getInstance()
                cal1.time = sdf.parse("$first + $hourStart")

                while (cal2.compareTo(cal1) == 1) {

                    val date = cal1.time
                    val dateString = sdf.format(date)

                    val dateList = dateString.split(" + ")

                    val dateOut = dateList.first()
                    val hourOut = dateList.last()

                    doseLeft ++
                    val pillTime = PillTimeDB(null,id,dateOut,hourOut,false)
                    daysFirebase.add("$dateOut&AND&$hourOut")
                    viewModel.insertPillDate(pillTime)

                    cal1.add(Calendar.HOUR, period.toDouble().toInt())
                }
            }
            if (days.isNotEmpty() && hours.isNotEmpty()) {

                days.forEach { day ->

                    hours.forEach { hour ->

                        val pillTime = PillTimeDB(null,id,day,hour,false)
                        daysFirebase.add("$day&AND&$hour")
                        viewModel.insertPillDate(pillTime)
                        doseLeft++

                    }
                }
            }

            viewModel.updatePillDoseLeft(id,doseLeft)

            val pillFirebase = PillFirebase(id,data.name,data.info,data.type,doseLeft,data.dayStart,data.lastDay,data.patient,data.doctor,data.dose,daysFirebase)

            disposable = viewModel.insertPillFirebase(pillFirebase,id).subscribe {
                when(it){
                    "a" -> Log.d("1", "SUCCESS ")
                    "b" -> Log.d("2", "FAILURE ")
                }
                daysFirebase.clear()
            }
        }

        lifecycleScope.launch {
            delay(300)
            if (toggleGroup.isChecked) {
                val myArray = listID.toTypedArray()
                val bundle = bundleOf("ids" to myArray)
                findNavController().navigate(R.id.action_mainAdd_to_priceFragment, bundle)
            } else {
                findNavController().popBackStack()
            }
        }
    }

    private fun startTransition(){

            binding.motion.setTransition(R.id.start, R.id.end)
            binding.motion.transitionToEnd()

            binding.motion.setTransitionListener(object : TransitionAdapter() {

                override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int
                ) {

                    start = startId

                    if(startId==R.id.end && dataInserted.size>1 ) {
                        binding.redo.alpha = 1f
                    }

                    if(endId==R.id.start){
                        binding.redo.alpha = 0f
                        binding.undo.alpha = 0f
                    }

                    if(startId==R.id.start && endId==R.id.end){
                        currentItem = 0
                        val itemCurrent = dataInserted[currentItem]
                        binding.abcd.animateText(itemCurrent.name)
                        binding.abcde.animateText("${itemCurrent.dose} ${itemCurrent.type}")
                        binding.summaryDays.text ="${itemCurrent.dayStart} - ${itemCurrent.lastDay}"

                        if(itemCurrent.hourStart!=null){
                           binding.summaryHours.text =  "OD ${itemCurrent.hourStart} CO ${itemCurrent.period} GODZINY"
                        }else{
                            showListOfHours(itemCurrent.hours,binding.summaryHours)
                        }
                    }
                }


                override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {

                    if (currentId == R.id.end && start == R.id.start) {
                        enableButtons(true)
                        binding.motion.setTransition(R.id.end, R.id.end2)
                        binding.motion.transitionToEnd()
                    }

                    if (currentId == R.id.end && start == R.id.end2) {
                        enableButtons(false)
                        binding.motion.setTransition(R.id.end, R.id.start)
                        binding.motion.transitionToEnd()
                    }

                    if(currentId == R.id.temp && start == R.id.end2){
                        val itemCurrent = dataInserted[currentItem]
                        binding.abcde.animateText("${itemCurrent.dose} ${itemCurrent.type}")
                        binding.abcd.animateText(itemCurrent.name)
                        binding.summaryDays.text ="${itemCurrent.dayStart} - ${itemCurrent.lastDay}"
                        if(itemCurrent.hourStart!=null){
                            binding.summaryHours.text =  "OD ${itemCurrent.hourStart} CO ${itemCurrent.period} GODZINY"
                        }else{
                            showListOfHours(itemCurrent.hours,binding.summaryHours)
                        }
                        binding.motion.setTransition(R.id.temp, R.id.end2)
                        binding.motion.transitionToEnd()
                    }
                }
            })
    }


    private fun handlePrevForw(){

        binding.redo.setOnClickListener {

            currentItem++

            if(currentItem-1>=0){
                binding.undo.alpha = 1f
                binding.undo.setAllEnabled(true)
            }
            if(currentItem == dataInserted.size-1){
                binding.redo.alpha = 0f
                binding.redo.setAllEnabled(false)
            }

            binding.motion.setTransition(R.id.end2,R.id.temp)
            binding.motion.transitionToEnd()

        }

        binding.undo.setOnClickListener {

            currentItem--

            if(currentItem-1<0){
                binding.undo.alpha = 0f
                binding.undo.setAllEnabled(false) }

            if(currentItem < dataInserted.size-1){
                binding.redo.alpha = 1f
                binding.redo.setAllEnabled(true)
            }

            binding.motion.setTransition(R.id.end2,R.id.temp)
            binding.motion.transitionToEnd()
        }
    }


    private fun handleRxEvents(){


        //Bug podwojnego clicku dlatego korzystam z biblioteki zew
        RxBus.subscribe<RxEvent.TypeChosenEvent>(this) {
            when(it.type){
                0-> Log.d("1", "BAJO 0")
                1-> {
                    findNavController().navigate(R.id.action_mainAdd_to_prevPills)
                }
                2-> {
                    findNavController().navigate(R.id.action_mainAdd_to_scannAdd)
                }
            }

        }

        disposable = MyRxBus.listen(RxEvent.EventQuery::class.java).subscribe {
            val query = it.query
            binding.nameOfPill.text = query
            name=query
        }

        disposable = MyRxBus.listen(RxEvent.EventDate::class.java).subscribe {
            days.clear()
            val daysList = it.daysList

            val firstString = daysList.first().replace('-','.')
            val lastString = daysList.last().replace('-','.')
            binding.nameOfPill2.text= "$firstString - $lastString"
            days.addAll(daysList)
        }

        disposable = MyRxBus.listen(RxEvent.EventHourOne::class.java).subscribe {
            hourStart=null
            period = null
            val hour = it.hour
            hours.add(hour)
            showListOfHours(hours,binding.nameOfPill3)
        }

        disposable = MyRxBus.listen(RxEvent.EventHourTwoOne::class.java).subscribe {
            hourStart = null
            hours.clear()
            val hour = it.hour
            hourStart = hour
        }

        disposable = MyRxBus.listen(RxEvent.EventHourTwoTwo::class.java).subscribe {
            period = null
            period = it.period

            binding.nameOfPill3.text = "OD $hourStart CO $period GODZINY"
        }

        disposable = MyRxBus.listen(RxEvent.EventHourThree::class.java).subscribe {
            val eventList = it.eventList
            hours.addAll(eventList)
        }

        disposable = MyRxBus.listen(RxEvent.EventDose::class.java).subscribe {
            dose = it.dose.toDouble()
        }

        disposable = MyRxBus.listen(RxEvent.EventChoice::class.java).subscribe {
           choice = it.choice
        }

        disposable = MyRxBus.listen(RxEvent.EventInfo::class.java).subscribe {
             info = it.info
        }

        disposable = MyRxBus.listen(RxEvent.EventHourClean::class.java).subscribe{
            period = null
            hourStart = null
            hours.clear()
            showListOfHours(hours,binding.nameOfPill3)
        }

        RxBus.subscribe<RxEvent.EventClick>(this) {


            saveSingleDataLocally()
            if(addingOk) {
                editPreviousMode = false
                startTransition()
            }else{
                MotionToast.darkColorToast(requireActivity(),"BŁĄD DODAWANIA","Spróbuj jeszcze raz",
                    MotionToast.TOAST_ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(requireContext(),R.font.helvetica_regular))
            }

        }

        disposable = MyRxBus.listen(RxEvent.PrevPillChosen::class.java).subscribe {
            val prevPill = it.pill
            MyRxBus.publish(RxEvent.EventQuery(prevPill.name))
            MyRxBus.publish(RxEvent.EventInfo(prevPill.description))
            MyRxBus.publish(RxEvent.EventChoice(prevPill.type))
            MyRxBus.publish(RxEvent.EventDose(prevPill.amount.toString()))

            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val start =sdf.parse(prevPill.dayStart)
            val stop = sdf.parse(prevPill.dayEnd)

            val diff: Long = stop!!.time - start!!.time
            val seconds = (diff / 1000).toDouble()
            val minutes = seconds / 60
            val hoursBetween = minutes/60
            val daysBetween = (hoursBetween/24).toInt()

            val calStar = Calendar.getInstance()
            calStar.set(Calendar.HOUR_OF_DAY,0)
            val dateStart = calStar.time

            calStar.add(Calendar.DATE,daysBetween)
            val dateEnd = calStar.time

            val daysList = SecondStepFragment.getDaysBetweenDates(dateStart,dateEnd)
            daysList?.let {
                days.clear()
                val firstString = daysList.first().replace('-', '.')
                val lastString = daysList.last().replace('-', '.')
                binding.nameOfPill2.text = "$firstString - $lastString"
                days.addAll(daysList)
            }

            period = null
            hourStart = null
            hours.clear()

            if(prevPill.startHour!=null && prevPill.period!=null ){

                period = prevPill.period
                hourStart = prevPill.startHour
                binding.nameOfPill3.text = "OD $hourStart CO $period GODZINY"
            }

            if(prevPill.listHour!=null){
                val hoursPrev = prevPill.listHour!!.split(',')
                hours.addAll(hoursPrev)
                showListOfHours(hours,binding.nameOfPill3)
            }

        }
    }

    private fun handlePrescriptionPhoto() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<FromScan>("key")
            ?.observe(viewLifecycleOwner) { result ->
                hourStart = null
                period = null
                hours.clear()
                singleFromScan(result)
            }
    }


    private fun singleFromScan(inside:FromScan){

        MyRxBus.publish(RxEvent.EventQuery(inside.nameString))
        MyRxBus.publish(RxEvent.EventDose(inside.amount.toString()))
        MyRxBus.publish(RxEvent.EventChoice(inside.type))
        val daily = inside.howMuch * inside.amount
        val all = inside.dose
        val daysScan = all / daily
        val resultDays = if (daysScan % 1.0 == 0.0) {
            daysScan.toInt()
        } else {
            daysScan.toInt() + 1
        }
        val calStar = Calendar.getInstance()
        calStar.set(Calendar.HOUR_OF_DAY, 0)
        val dateStart = calStar.time

        calStar.add(Calendar.DATE, resultDays)
        val dateEnd = calStar.time

        val daysList = SecondStepFragment.getDaysBetweenDates(dateStart, dateEnd)
        daysList?.let {
            days.clear()
            val firstString = daysList.first().replace('-', '.')
            val lastString = daysList.last().replace('-', '.')
            binding.nameOfPill2.text = "$firstString - $lastString"
            days.addAll(daysList)
        }
        val df = SimpleDateFormat("HH:mm")
        when (inside.howMuch) {
            1.0 -> {
                hours.add("07:00")
            }
            2.0 -> {
                hours.add("07:00")
                hours.add("22:00")
            }
            else -> {
                val calendarStart = Calendar.getInstance()
                calendarStart.set(Calendar.HOUR_OF_DAY, 7)
                calendarStart.set(Calendar.MINUTE, 0)
                val start = 7.0 * 60.0
                val end = 22.0 * 60.0

                val difference = end - start
                val onePeriod = (difference / (inside.howMuch-1)).toInt()


                 hours.add("07:00")

                for (i in 1 until inside.howMuch.toInt()) {
                    calendarStart.add(Calendar.MINUTE, onePeriod)
                    val hour = df.format(calendarStart.time)
                    hours.add(hour)
                }
            }
        }
        showListOfHours(hours,binding.nameOfPill3)
    }



    private fun showListOfHours(listHours:List<String>,element: TextView){

        element.text = ""
        if(listHours.isNotEmpty()) {
            var firstElement = true
            listHours.forEach {
                if(firstElement){
                element.append("$it ")}
                else{
                    element.append(",$it ")
                }
                firstElement = false
            }
        }else{
            element.text = ""
        }
    }


    private fun startPrevPillsChooser(){

        findNavController().navigate(R.id.action_mainAdd_to_prevPills)

    }


    private fun View.setAllEnabled(enabled: Boolean) {
        isEnabled = enabled
        if (this is ViewGroup) children.forEach { child -> child.setAllEnabled(enabled) }
    }


    private fun enableButtons(enabled: Boolean){
        binding.cardView3.setAllEnabled(enabled)
        binding.cardVie3.setAllEnabled(enabled)
        binding.cardView4.setAllEnabled(enabled)
        binding.cardVie4.setAllEnabled(enabled)
        binding.redo.setAllEnabled(enabled)
        binding.undo.setAllEnabled(enabled)

    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
        RxBus.unsubscribe(this)
    }

    override fun onPause() {
        super.onPause()
        RxBus.unsubscribe(this)
    }

    override fun onResume() {
        super.onResume()
        Log.d("1", "KURWA IN $name ")
    }
}


