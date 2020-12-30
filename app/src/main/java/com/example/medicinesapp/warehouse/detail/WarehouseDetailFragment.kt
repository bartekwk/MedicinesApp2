package com.example.medicinesapp.warehouse.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicinesapp.R
import com.example.medicinesapp.adapter.WarehouseDetailAdapter
import com.example.medicinesapp.adapter.WarehouseDetailAdapter2
import com.example.medicinesapp.data.PillOrganizerDB
import com.example.medicinesapp.data.PillOrganizerManager
import com.example.medicinesapp.databinding.WarehouseDetailBinding
import com.example.medicinesapp.utill.Helper
import com.example.medicinesapp.utill.listeners.RecyclerViewItemClickListener
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.android.synthetic.main.prev_pills_item.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class WarehouseDetailFragment:Fragment() {

    private lateinit var binding: WarehouseDetailBinding
    private val viewModel: WarehouseDetailViewModel by viewModels { WarehouseDetailViewModelFactory(Helper.provideRepository(requireContext()), requireActivity().application) }
    private lateinit var adapter1:WarehouseDetailAdapter
    private lateinit var adapter2: WarehouseDetailAdapter2
    private var list1 = mutableListOf<PillOrganizerDB>()
    private var list2 = mutableListOf<PillOrganizerDB>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        viewModel.markAsBoughtOrNot(6,false)

        binding = WarehouseDetailBinding.inflate(inflater,container,false)

        val pillOrganizer:PillOrganizerManager = requireArguments().getParcelable("organizer")!!

        binding.item = pillOrganizer


        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val currentDate = sdf.format(Date())
        val split = currentDate.split(' ')
        //val date = split.first()
        val date = "2019-12-12"
        val time = split.last()

        adapter1 = WarehouseDetailAdapter(list1)
        adapter2 = WarehouseDetailAdapter2(list2,object: RecyclerViewItemClickListener<PillOrganizerDB>{
            override fun onClickListener(item: PillOrganizerDB) {

                lifecycleScope.launch {
                    viewModel.markAsBoughtOrNot(item.id!!, true)
                    viewModel.getPillsToChart(pillOrganizer.id, date, time)
                    val index = pillOrganizer.listPill.indexOf(item)
                    pillOrganizer.listPill[index].bought = true
                }
            }
        })

        binding.recycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
        binding.recycler.adapter = adapter1
        binding.recycler2.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
        binding.recycler2.adapter = adapter2



        viewModel.getPillsToChart(pillOrganizer.id,date,time)

        viewModel.listChart.observe(viewLifecycleOwner, androidx.lifecycle.Observer {pills->


            list1.clear()
            list2.clear()

            val notExpired = pillOrganizer.listPill.filter {it.bought }
            val expired = pillOrganizer.listPill.filter { !it.bought }



            Log.d("1", "KUURWA $notExpired ")


            if(notExpired.isEmpty()){
                val pillEmpty = PillOrganizerDB(-1,"","",0.0,0.0,0,0,null,false,null)
                list1.add(pillEmpty)
                binding.recycler.alpha = 0.0f
                binding.noOne.alpha = 1.0f
            }else{
                binding.recycler.alpha = 1.0f
                binding.noOne.alpha = 0.0f
                list1.addAll(notExpired)
            }

            if(expired.isEmpty()){
                val pillEmpty = PillOrganizerDB(-1,"","",0.0,0.0,0,0,null,false,null)
                list2.add(pillEmpty)
                binding.recycler2.alpha = 0.0f
                binding.noTwo.alpha = 1.0f
            }else{
                binding.recycler2.alpha = 1.0f
                binding.noTwo.alpha = 0.0f
                list2.addAll(expired)
            }

            adapter1.notifyDataSetChanged()
            adapter2.notifyDataSetChanged()

            val seriesList = mutableListOf<AASeriesElement>()
            var zeroApproach = false


            var sumLeftNow = notExpired.map {

                /*
                val number:Int = if(it.leftNow!=null) {
                    it.leftNow!!
                }else{
                    it.left!!
                }
                number
                 */
                it.left!!

            }.sum().toDouble()

            val days = mutableListOf<String>()
            val used = mutableListOf<Double>()
            val usedOrganizer = mutableListOf<Double>()

            val left = pills.first().doseLeftNow?.toDouble()
            val amount = pills.first().amount
            var counter = left!!*amount

            Log.d("1", "KUURWA $sumLeftNow")

            var daysBetweenEnds  = 0

            pills.forEach {pill->

                val count = pill.count *amount

                days.add(pill.date)
                counter-=count
                sumLeftNow-=count
                used.add(counter)
                if(sumLeftNow<0 && !zeroApproach){
                    seriesList.add( AASeriesElement().name("leki w zapasie").data(usedOrganizer.toTypedArray()))
                    zeroApproach=true
                }
                if(zeroApproach){
                    daysBetweenEnds++
                }
                usedOrganizer.add(sumLeftNow)
            }

            seriesList.add(AASeriesElement().name("brane leki").borderWidth(0.7f).data(used.toTypedArray()))

            if(!zeroApproach) {
                seriesList.add(AASeriesElement().name("leki w zapasie").data(usedOrganizer.toTypedArray()))
                binding.imageChart.setBackgroundResource(R.drawable.ic_jes)
                binding.text2.text = "Leków w zapasie wystarczy"
                binding.text3.text = "zapas $sumLeftNow dawek"
            }else{
                binding.imageChart.setBackgroundResource(R.drawable.ic_nol)
                binding.text2.text = "Leków w zapasie nie wystarczy"
                binding.text3.text = "zabraknie ${Math.abs(sumLeftNow)} dawek na $daysBetweenEnds dni"
            }


            val seriesArray = seriesList.toTypedArray()

            val aaChartModel : AAChartModel = AAChartModel()
                .chartType(AAChartType.Spline)
                .title("Zużycie lekarstw")
                .backgroundColor("#ffffffff").yAxisTitle("pozostało dawek").yAxisMin(0.0f)
                .dataLabelsEnabled(false).xAxisGridLineWidth(0f).yAxisGridLineWidth(0f)
                .markerSymbol(AAChartSymbolType.Circle)
                .markerSymbolStyle(AAChartSymbolStyleType.Normal)
                .markerRadius(2.8f)
                .dataLabelsEnabled(false)
                .categories(days.toTypedArray())
                .series(seriesArray)

            binding.chart.aa_drawChartWithChartModel(aaChartModel)

        })

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
            .apply { duration = 1000L }

        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
            .apply { duration = 1000L }
    }

}