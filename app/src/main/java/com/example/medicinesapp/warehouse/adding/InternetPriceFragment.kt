package com.example.medicinesapp.warehouse.adding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.medicinesapp.R
import com.example.medicinesapp.adapter.InternetPriceAdapter
import com.example.medicinesapp.adapter.InternetPriceAdapter2
import com.example.medicinesapp.adapter.InternetViewHolder
import com.example.medicinesapp.data.FromInternet
import com.example.medicinesapp.databinding.InternetPriceBinding
import com.example.medicinesapp.rxBus.MyRxBus
import com.example.medicinesapp.rxBus.RxEvent
import com.example.medicinesapp.utill.Helper
import com.example.medicinesapp.utill.listeners.RecyclerViewItemClickListener
import com.example.medicinesapp.warehouse.adding.vm.InternetPriceViewModel
import com.example.medicinesapp.warehouse.adding.vm.InternetPriceViewModelFactory
import com.robinhood.ticker.TickerUtils
import io.jmdg.rxbus.RxBus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class InternetPriceFragment: Fragment(){

    private var medicineList1 = mutableListOf<FromInternet>()
    private var medicineList2 = mutableListOf<FromInternet>()
    private var previousItem:FromInternet?=null
    private lateinit var disposable: Disposable
    private val viewModel: InternetPriceViewModel by viewModels { InternetPriceViewModelFactory(Helper.provideRepository(requireContext()), requireActivity().application) }

    private lateinit var binding: InternetPriceBinding
    private lateinit var adapter1:InternetPriceAdapter
    private lateinit var adapter2: InternetPriceAdapter2

    private var currentChooseItem:FromInternet?=null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        binding = InternetPriceBinding.inflate(inflater,container,false)

        binding.price.setCharacterLists(TickerUtils.provideNumberList())


        RxBus.subscribe<RxEvent.EventInternetList>(this) {
            val newList = it.list

            newList.forEach {info->
                val priceString = info.price
                val count = info.count
                val price =  InternetViewHolder.getReturnPrice(priceString,count.toDouble())
                info.price = price
            }

            medicineList2.addAll(newList)
            adapter2.notifyDataSetChanged()
            calculatePrice(medicineList2)
        }


        adapter2 = InternetPriceAdapter2(medicineList2,object:RecyclerViewItemClickListener<FromInternet>{

            override fun onClickListener(item: FromInternet) {

                val index = medicineList2.indexOf(item)
                medicineList2.remove(item)
                adapter2.notifyItemRemoved(index)
                calculatePrice(medicineList2)
            }
        },object:RecyclerViewItemClickListener<FromInternet>{
            override fun onClickListener(item: FromInternet) {

                currentChooseItem = item

                findNavController().navigate(R.id.action_priceFragment_to_prevPills)
            }
        })

        adapter1 = InternetPriceAdapter(medicineList1,object:RecyclerViewItemClickListener<FromInternet>{
            override fun onClickListener(item: FromInternet) {
                Log.d("1", "MAMY CIE $item ")
                if(medicineList2.isNotEmpty()) {
                    medicineList2.removeAt(medicineList2.size - 1)
                }
                medicineList2.add(item)
                adapter2.notifyDataSetChanged()
                calculatePrice(medicineList2)
            }
        })


        binding.viewPager.apply {
            offscreenPageLimit = 1
            val recyclerView = getChildAt(0) as RecyclerView
            val cardMargin = 10
            val peekOfSetMargin = 60
            recyclerView.apply {
                val padding = cardMargin + peekOfSetMargin
                setPadding(padding, 0, padding, 0)
                clipToPadding = false
            }
            adapter = adapter1
        }

        binding.recyclerView.adapter = adapter2

        val keys = arguments?.getStringArray("ids")?.toList()


        keys?.let {
            getPills(it)
        }

        binding.text.setEndIconOnClickListener {

            medicineList1.clear()
            var count = 0
            disposable = viewModel.getMedicine(binding.type.text.toString(),"null")!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val pill = it
                    pill.position = count
                    medicineList1.add(pill)
                    adapter1.notifyDataSetChanged()
                    count++
                    if(count ==1) {
                        val item = medicineList1[0]
                        val amountSentence = item.body!!.split(",").last()
                        item.amount = InternetViewHolder.calculateAmount(amountSentence)
                        previousItem = item
                        medicineList2.add(item)
                        adapter2.notifyDataSetChanged()
                    }
            }
        }

        binding.button.setOnClickListener {
            previousItem =null
        }


        binding.viewPager.registerOnPageChangeCallback(object:ViewPager2.OnPageChangeCallback(){

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
               previousItem?.let {
                   if(medicineList2.contains(it)){
                       medicineList2.remove(it)
                   }
               }
                val item = medicineList1[position]
                val amountSentence = item.body!!.split(",").last()
                item.amount = InternetViewHolder.calculateAmount(amountSentence)
                medicineList2.add(item)
                adapter2.notifyDataSetChanged()
                previousItem = item
                calculatePrice(medicineList2)
            }
    })

        disposable = MyRxBus.listen(RxEvent.PrevPillChosen::class.java).subscribe {
            val prevPill = it.pill

            currentChooseItem?.let {item->

                item.nameOfPill = prevPill.name
                item.connectedToPill = true
                item.idPill = prevPill.id

                val index = medicineList2.indexOf(item)
                medicineList2[index] = item
                adapter2.notifyDataSetChanged()
            }
        }

        binding.buttonOwn.setOnClickListener {
            findNavController().navigate(R.id.action_priceFragment_to_customOrganizer)
        }

        binding.button2.setOnClickListener {

            medicineList2.forEach {fromInternet->
                val organizerDb = viewModel.convertFromInternetToPillOrganizer(fromInternet)
                viewModel.insertPillOrganizer(organizerDb)
                findNavController().navigate(R.id.action_priceFragment_to_dashboard)
                Log.d("1", " KLIKŁO $organizerDb ")
            }
        }
        return binding.root
    }

    private fun calculatePrice(list:List<FromInternet>){

        var count = 0.0
        list.forEach{one->
            one.price?.let {price->
                var priceOut = price.split(' ')[0]
                priceOut = priceOut.replace(',','.')
                count+=priceOut.toDouble()
            }
        }
        val output = String.format("%.2f", count)
        binding.price.text = "$output zł"
    }


    private fun getPills(keys:List<String>){

        val myList2 = keys.toTypedArray()

        val bundle = bundleOf("myArgs" to myList2)

        findNavController().navigate(R.id.action_priceFragment_to_internetDialog,bundle)

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