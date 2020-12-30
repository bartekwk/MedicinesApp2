package com.example.medicinesapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.medicinesapp.CurrentUser
import com.example.medicinesapp.R
import com.example.medicinesapp.data.User
import com.example.medicinesapp.databinding.DashboardBinding
import com.example.medicinesapp.utill.Helper
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DashboardFragment: Fragment() {

    lateinit var binding:DashboardBinding

    private val viewModel: DashboardViewModel by viewModels {
        DashboardViewModelFactory(
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
        binding = DashboardBinding.inflate(inflater,container,false)

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
               findNavController().navigate(R.id.action_dashboard_to_login)
            }
        })

        viewModel.markAsOnline()

        getClosestPill()


        viewModel.currentPill.observe(viewLifecycleOwner,Observer{


            binding.name.text = it.name
            binding.timeSingle.text = "${it.date} ${it.time}"
            binding.single.text = it.amount.toString()
            binding.additional.text = it.description


            val formatString = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val fromDataBaseString = "${it.date} ${it.time}:00"
            val fromDataBaseStringDate = formatString.parse(fromDataBaseString)!!.time

            val now = Calendar.getInstance()
            val nowDate = now.time.time

            val difference = fromDataBaseStringDate - nowDate
            startTimer(difference)
        })


        viewModel.currentUser.observe(viewLifecycleOwner, Observer {
            if(it!=null) {
                Log.d("1", "END ${it.email}")
            }
        })

        binding.mcv1.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_allPills)
        }

        binding.mcv2.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_myPills)
        }

        binding.mcv3.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_warehouse)
        }
        binding.mcv4.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_friendsFragment)
        }
        binding.mcv5.setOnClickListener {
            findNavController().navigate(R.id.mainAdd)
        }

        viewModel.subscribeToTopic()


        return binding.root
    }

    private fun getClosestPill(){
        val formatString = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val calendar = Calendar.getInstance()
        val now = formatString.format(calendar.time)
        viewModel.getClosestPill(now)
    }


    private fun startTimer(difference:Long){

        val countDownTimer = object: CountDownTimer(difference,1000){
            override fun onFinish() {
                getClosestPill()
            }
            override fun onTick(p0: Long) {
               val time  = convertTime(p0)
                binding.time.text = time
            }
        }
        countDownTimer.start()
    }
   @SuppressLint("DefaultLocale")
   private fun convertTime(milliSeconds:Long):String {

       return String.format("%02d:%02d:%02d",
           TimeUnit.MILLISECONDS.toHours(milliSeconds),
           TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
           TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)))
   }


    companion object {
        private fun currentUserToUser(currentUser: CurrentUser): User {

            return User(
                uid = currentUser.uid,
                name = currentUser.name,
                email = currentUser.email,
                isDoctor = currentUser.isDoctor,
                lastActive = currentUser.lastActive,
                isOnline = currentUser.isOnline,
                photoPath = null,
                isChecked = null
            )
        }
    }

}