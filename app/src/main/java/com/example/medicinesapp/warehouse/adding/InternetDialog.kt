package com.example.medicinesapp.warehouse.adding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.medicinesapp.adapter.InternetDialogAdapter
import com.example.medicinesapp.adapter.RecyclerViewInsiderAdapter
import com.example.medicinesapp.warehouse.adding.vm.InternetDialogViewModel
import com.example.medicinesapp.warehouse.adding.vm.InternetDialogViewModelFactory
import com.example.medicinesapp.data.FromInternet
import com.example.medicinesapp.data.FromInternetWrapper
import com.example.medicinesapp.data.PillDB
import com.example.medicinesapp.databinding.InternetDialogBinding
import com.example.medicinesapp.rxBus.MyRxBus
import com.example.medicinesapp.rxBus.RxEvent
import com.example.medicinesapp.utill.Helper
import com.example.medicinesapp.utill.listeners.ViewPagerItemClickListener
import io.jmdg.rxbus.RxBus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch


class InternetDialog:DialogFragment() {

    private lateinit var binding: InternetDialogBinding
    private var subscribe: Disposable? = null
    private lateinit var adapter2: InternetDialogAdapter
    private var pills = mutableListOf<PillDB>()
    private var clicked = mutableListOf<FromInternet>()
    private var pillsOutside = mutableListOf<FromInternetWrapper>()
    private lateinit var outsideAdapter:RecyclerViewInsiderAdapter

    private val viewModel: InternetDialogViewModel by viewModels { InternetDialogViewModelFactory(Helper.provideRepository(requireContext()), requireActivity().application) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= InternetDialogBinding.inflate(inflater,container,false)

        val list = arguments?.getStringArray("myArgs")?.toList()

        binding.end.setOnClickListener {
            RxBus.post(RxEvent.EventInternetList(clicked))
            dismiss()
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                RxBus.post(RxEvent.EventInternetList(clicked))
                dismiss()
            }
        })

        adapter2 = InternetDialogAdapter(pills)
        binding.card.adapter = adapter2


        binding.header.text=" z ${list?.size} lekarstw "


        outsideAdapter = RecyclerViewInsiderAdapter(pillsOutside, object :
            ViewPagerItemClickListener<Bundle> {

            override fun onClickListener(item: Bundle) {

                val addItem = item.getParcelable<FromInternet>("add")
                val removeItem = item.getParcelable<FromInternet>("remove")


                addItem?.let {newItem->
                    clicked.add(newItem)
                }

                removeItem?.let { newItem->
                    clicked.remove(newItem)
                }

                binding.header2.text = " ${clicked.size} lekarstw"
            }
        })


        binding.viewPager2.adapter = outsideAdapter

       list?.let {
           getDataFromInternet(it)
       }

        var previousPosition:Int?=null

        binding.card.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (previousPosition != position) {
                    binding.current.text = (position + 1).toString()
                    binding.viewPager2.currentItem = position
                }
                previousPosition = position
            }
        })

        var previousPosition2:Int?=null
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (previousPosition2 != position) {
                    binding.current.text = (position + 1).toString()
                    binding.card.currentItem = position
                }
                previousPosition2 = position
            }
        })


        binding.button2.setOnClickListener {

            if(binding.card.currentItem!=0){
                binding.card.currentItem = binding.card.currentItem-1
            }
        }

        binding.button3.setOnClickListener {
            if(binding.card.currentItem!=pills.size){
                binding.card.currentItem = binding.card.currentItem+1
            }
        }
        return binding.root
    }


    private fun getDataFromInternet(ids:List<String>){

        var id=0

        val selectedPills = viewModel.getSelectedPills(ids)

        subscribe = selectedPills
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { list->

            pills.addAll(list)
            adapter2.notifyDataSetChanged()

           list.forEach { pill ->

               lifecycleScope.launch {

                   val amount = (pill.doseLeft!!.toDouble() *pill.amount).toInt()
                   val fromInternetList = mutableListOf<FromInternet>()
                   val pillOutside = FromInternetWrapper(id, fromInternetList,amount)
                   pillsOutside.add(pillOutside)

                   viewModel.getMedicine(pill.name,pill.id)?.subscribe {
                       fromInternetList.add(it)
                       outsideAdapter.notifyDataSetChanged()
                   }
                   id++
               }
           }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        subscribe?.dispose()
    }

}