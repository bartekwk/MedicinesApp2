package com.example.medicinesapp.mainActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.medicinesapp.R
import com.example.medicinesapp.data.PillDB
import com.example.medicinesapp.db.AppPreferences
import com.example.medicinesapp.utill.Helper
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(){

    private lateinit var navController: NavController
    private  var disposable: Disposable?=null

    private var oneEnd:Int? = null
    private var twoEnd:Int? = null

    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModelFactory(
            Helper.provideRepository(this),this.application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        navController = navHostFragment.navController
        bottom_nav.setupWithNavController(navController)
        AppPreferences.init(this)
        viewModel.setDailyAlarmSetterWorker()

        if (!AppPreferences.firstRun) {
            AppPreferences.firstRun = true
            viewModel.insertInitialPill(PillDB("null","null","null","null",null,null,"null","null","null",0.0,null))
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id){
                R.id.mainAdd -> bottom_nav.visibility = GONE
                R.id.prevPills ->bottom_nav.visibility = GONE
                R.id.scannAdd -> bottom_nav.visibility = GONE


                R.id.priceFragment -> bottom_nav.visibility = GONE
                R.id.customDialog -> bottom_nav.visibility = GONE
                R.id.dateDialog -> bottom_nav.visibility = GONE
                R.id.noDays -> bottom_nav.visibility = GONE
                R.id.login -> bottom_nav.visibility = GONE
                R.id.login2 -> bottom_nav.visibility = GONE
                R.id.register ->bottom_nav.visibility = GONE
                R.id.barcode_reader -> bottom_nav.visibility = GONE
                else -> bottom_nav.visibility = View.VISIBLE
            }
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val calendar = Calendar.getInstance()
        val currentDate = sdf.format(calendar.time)
        val split = currentDate.split(' ')
        val date = split.first()
        val hour = split.last()


        handleInitialUpdates(date,hour)

    }


    private fun handleInitialUpdates(day:String,time:String){

        viewModel.updateDoneOne.observe(this, androidx.lifecycle.Observer {
            oneEnd = it
            if(twoEnd!=null){
                getAllPillsByDay(day,time)
                checkNegativeAmount()
            }
        })

        viewModel.updateDoneTwo.observe(this, androidx.lifecycle.Observer {
            twoEnd = it
            if(oneEnd!=null){
                getAllPillsByDay(day,time)
                checkNegativeAmount()
            }
        })
    }

    private fun getAllPillsByDay(day:String,time:String){

        Log.d("1", "COTO $day $time")

        disposable = viewModel.getAllLeftDoseByIDS(day, time)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe { listDose ->
                listDose.forEach { oneDose ->
                    val dose = oneDose.dose.toInt()
                    oneDose.amount?.let { amountDoseLeft ->
                        viewModel.updatePillDoseLeftNow(oneDose.id, amountDoseLeft)
                        oneDose.doseLeft?.let { amountDoseAll ->
                            viewModel.updateOrganizerPillDoseLeftNow(oneDose.id, amountDoseLeft*dose, amountDoseAll*dose)
                        }
                    }
                }
            }
    }

    private fun checkNegativeAmount(){

        viewModel.checkIfNegativeDoseLeftNow()

        viewModel.list.observe(this, androidx.lifecycle.Observer {listNegativeAmount->

            listNegativeAmount.forEach {oneOrganizer->
                val id = oneOrganizer.id!!
                val idPill = oneOrganizer.pillId
                val amount = kotlin.math.abs(oneOrganizer.leftNow!!)

                viewModel.updateOrganizerPillDoseLeftNowNegativeInOther(idPill,amount)
                viewModel.markAsUsed(id)
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

    override fun onPause() {
        super.onPause()
        viewModel.logOut()
        viewModel.unsubscribeFromTopic()
        disposable?.dispose()
        Log.d("1", "ABCD OUT")
    }

    override fun onResume() {
        super.onResume()
        Log.d("1", "ABCD IN")
    }

}