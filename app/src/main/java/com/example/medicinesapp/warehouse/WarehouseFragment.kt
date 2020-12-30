package com.example.medicinesapp.warehouse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.medicinesapp.R
import com.example.medicinesapp.adapter.WarehouseAdapter
import com.example.medicinesapp.data.PillOrganizerManager
import com.example.medicinesapp.databinding.WarehouseFragmentBinding
import com.example.medicinesapp.utill.Helper
import com.example.medicinesapp.utill.listeners.TransitionClickItemListener2
import com.google.android.material.transition.MaterialSharedAxis
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*


class WarehouseFragment:Fragment() {


    private val viewModel: WarehouseViewModel by viewModels { WarehouseViewModelFactory(Helper.provideRepository(requireContext()), requireActivity().application) }
    private lateinit var binding: WarehouseFragmentBinding
    private var listOrganizer = mutableListOf<PillOrganizerManager>()
    private lateinit var adapter:WarehouseAdapter
    private lateinit var disposable: Disposable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = WarehouseFragmentBinding.inflate(inflater,container,false)


        adapter = WarehouseAdapter(listOrganizer,object:TransitionClickItemListener2{

            override fun onClickListener( pillOrganizer: PillOrganizerManager) {

                exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
                    .apply { duration = 1000L }

                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
                    .apply { duration = 1000L }

                val bundle = bundleOf("organizer" to pillOrganizer)

                findNavController().navigate(R.id.action_warehouse_to_warehouseDetail,bundle)
            }
        })
        binding.recycler.adapter = adapter

        viewModel.checkIfNegativeDoseLeftNow()


        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val currentDate = sdf.format(Date())
        val split = currentDate.split(' ')
        val date = split.first()
        val time = split.last()
        

        disposable = viewModel.getAllLeftDoseByIDS(date,time)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe { listDose ->
                listDose.forEach { oneDose ->
                    val dose = oneDose.dose.toInt()
                    oneDose.amount?.let { amountDoseLeft ->
                        viewModel.updatePillDoseLeftNow(oneDose.id, amountDoseLeft)
                        oneDose.doseLeft?.let {amountDoseAll->
                            viewModel.updateOrganizerPillDoseLeftNow(oneDose.id,amountDoseLeft*dose,amountDoseAll*dose)
                        }
                    }

                    disposable = viewModel
                        .getPillsOrganizer()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            listOrganizer.clear()
                            val grouped = it.groupBy { item->item.pillId }

                            grouped.forEach { (key, value) ->
                                listOrganizer.add(PillOrganizerManager(key,value))
                            }
                            adapter.notifyDataSetChanged()
                        }
                }
            }


        viewModel.list.observe(viewLifecycleOwner, androidx.lifecycle.Observer {listNegativeAmount->

            listNegativeAmount.forEach {oneOrganizer->

                val id = oneOrganizer.id!!
                val idPill = oneOrganizer.pillId
                val amount = kotlin.math.abs(oneOrganizer.leftNow!!)

                viewModel.updateOrganizerPillDoseLeftNowNegativeInOther(idPill,amount)
                viewModel.markAsUsed(id)
            }
        })

        binding.floating.setOnClickListener {
            findNavController().navigate(R.id.action_warehouse_to_priceFragment)
        }

        return binding.root
    }


    override fun onPause() {
        super.onPause()
        if(!disposable.isDisposed)disposable.dispose()
    }

}