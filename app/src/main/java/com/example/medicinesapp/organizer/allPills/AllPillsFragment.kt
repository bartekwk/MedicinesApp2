package com.example.medicinesapp.organizer.allPills

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dingmouren.layoutmanagergroup.skidright.SkidRightLayoutManager
import com.example.medicinesapp.R
import com.example.medicinesapp.adapter.AllPillsAdapter
import com.example.medicinesapp.data.AllPillsFragmentData
import com.example.medicinesapp.databinding.AllPillFragmentBinding
import com.example.medicinesapp.organizer.allPills.detail.DetailDirection
import com.example.medicinesapp.rxBus.MyRxBus
import com.example.medicinesapp.rxBus.RxEvent
import com.example.medicinesapp.utill.Helper
import com.example.medicinesapp.utill.listeners.TransitionClickItemListener
import com.google.android.material.transition.MaterialElevationScale
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.calendar_meine.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AllPillsFragment: Fragment() {

    private var pillsList = mutableListOf<AllPillsFragmentData>()
    private var pillsListNow = mutableListOf<AllPillsFragmentData>()
    private var firstTime= true
    private lateinit var disposable: Disposable
    private lateinit var binding:AllPillFragmentBinding
    private lateinit var adapter: AllPillsAdapter
    private lateinit var layoutManager:SkidRightLayoutManager
    private val viewModel: AllPillsViewModel by viewModels { AllPillsViewModelFactory(Helper.provideRepository(requireContext())) }
    private var position:Int?=null
    private var friendID:String?=null

    @SuppressLint("CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        friendID = arguments?.getString("friendID")


        binding = AllPillFragmentBinding.inflate(inflater,container,false)

        findNavController().
        currentBackStackEntry?.
        savedStateHandle?.
        getLiveData<AllPillsFragmentData>("pill")?.
        observe(viewLifecycleOwner) { pill ->

            val index = pillsListNow.indexOf(pill)
            pillsListNow.removeAt(index)
            pillsList.clear()
            pillsList.addAll(pillsListNow)
            adapter.notifyDataSetChanged()
        }

        adapter = AllPillsAdapter(pillsList,object :TransitionClickItemListener<View>{
            override fun onClickListener(item: View,pillDB: AllPillsFragmentData,color:Int) {

                exitTransition = MaterialElevationScale(false).apply {
                    duration = 500L
                }
                reenterTransition = MaterialElevationScale(true).apply {
                    duration = 500L
                }

                val fromFirebase: Boolean = friendID!=null

                val transitionName = "card_detail"
                val extras = FragmentNavigatorExtras(item to transitionName)
                val friendIdOut = friendID?:""
                val direction = DetailDirection.actionStartFragmentToDetailFragment(pillDB,color,fromFirebase,friendIdOut)

                position = pillsList.indexOf(pillDB)
                findNavController().navigate(direction, extras)
            }
        })

        layoutManager = SkidRightLayoutManager(1.5f, 0.75f)
        binding.recycler.layoutManager = layoutManager
        binding.recycler.adapter = adapter


        position?.let {prevPosition->
            binding.recycler.post {
                layoutManager.scrollToPosition(prevPosition)
            }
        }


        binding.image.setImageBitmap(BitmapFactory.decodeResource(requireContext().resources,R.drawable.allpills))

        disposable = MyRxBus.listen(RxEvent.BottomSheetEvent::class.java).subscribe{

            when(it.type){
                1 -> {
                    val notOk = pillsListNow.filter {pill-> pill.doseLeftNow!!<=0 }
                    if(notOk.isNotEmpty()) {
                        pillsList.clear()
                        pillsList.addAll(notOk)
                        adapter.notifyDataSetChanged()
                        binding.recycler.alpha = 1.0f
                        binding.recycler.isNestedScrollingEnabled = true
                    }else{
                        binding.recycler.alpha = 0.0f
                        binding.recycler.isNestedScrollingEnabled = false
                    }
                }
                0 -> {
                    if(pillsListNow.isNotEmpty()){
                        pillsList.clear()
                        pillsList.addAll(pillsListNow)
                        adapter.notifyDataSetChanged()
                        binding.recycler.alpha = 1.0f
                        binding.recycler.isNestedScrollingEnabled = true
                    }else{
                        binding.recycler.alpha = 0.0f
                        binding.recycler.isNestedScrollingEnabled = false
                    }
                }
                2 -> {
                    val ok = pillsListNow.filter {pill-> pill.doseLeftNow!! >0 }
                    if(ok.isNotEmpty()) {
                        pillsList.clear()
                        pillsList.addAll(ok)
                        adapter.notifyDataSetChanged()
                        binding.recycler.alpha = 1.0f
                        binding.recycler.isNestedScrollingEnabled = true
                    }else{
                        binding.recycler.alpha = 0.0f
                        binding.recycler.isNestedScrollingEnabled = false
                    }
                }
            }
        }

        val calendar  = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")


        if(firstTime) {
            pillsList.clear()
            pillsListNow.clear()

            if(friendID!=null) {
                disposable = viewModel
                    .getFirebasePills(friendID!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {pillFirebase->

                    pillFirebase?.let {pill->

                        val daysAndHours = pill.listDays!!
                        var counter = 0

                        daysAndHours.forEach {

                            val daysAndHoursString = it.replace("&AND&"," ")
                            val daysAndHoursDate = sdf.parse(daysAndHoursString)!!

                            if(daysAndHoursDate.before(calendar.time)){
                                counter++
                            }
                        }
                        val leftNow = pill.doseLeft!! - counter

                        val amount = pill.amount!!.toInt()

                        val newPill =
                            AllPillsFragmentData(pill.id,
                            pill.name,
                            pill.description,
                            pill.type,
                            pill.doseLeft*amount,
                            leftNow*amount,
                            pill.dayStart,
                            pill.dayEnd,
                            pill.patient,
                            pill.amount!!,
                            pill.doctor,
                            null,
                            null)

                        pillsListNow.add(newPill)

                        if(newPill.doseLeftNow!!>0) {
                            pillsList.add(newPill)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }else {

                disposable = viewModel
                    .getPillsToAllPillsFragment()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .throttleLatest(600, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { pills ->

                        pills.forEach {

                            if(it.doseLeftNow!=null){
                                it.doseLeftNow = it.doseLeftNow!! * (it.amount.toInt())
                            }
                            if(it.doseLeft!=null){
                                it.doseLeft = it.doseLeft!! * (it.amount.toInt())
                            }

                            it.leftNowOrganizer?.let{left->
                                if(left<0){
                                    var result= left
                                    while(result<0){
                                        result+=1000
                                    }
                                    it.leftNowOrganizer = result
                                }
                            }
                        }
                        pillsListNow.clear()
                        pillsList.clear()
                        pillsListNow.addAll(pills)

                        val ok = pills.filter{it.doseLeftNow!=null}.filter { it.doseLeftNow!! >0 }
                        pillsList.addAll(ok)
                        adapter.notifyDataSetChanged()


                        if(pills.isEmpty()){
                            recycler.layoutManager = LinearLayoutManager(requireContext())
                        }
                    }
            }
        }

        binding.first.setOnClickListener {
            findNavController().navigate(R.id.action_allPills_to_bottomSheet)
        }


        postponeEnterTransition()
        binding.root.doOnPreDraw { startPostponedEnterTransition() }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        if(!disposable.isDisposed)disposable.dispose()
    }

    override fun onPause() {
        super.onPause()
        if(!disposable.isDisposed)disposable.dispose()
    }

    override fun onResume() {
        super.onResume()
        firstTime = false
    }
}