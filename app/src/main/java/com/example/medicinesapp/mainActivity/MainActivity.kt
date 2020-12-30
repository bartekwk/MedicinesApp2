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

import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(){
    private lateinit var navController: NavController

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
            Log.d("1", "COTO JEST ${AppPreferences.firstRun}")
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

    }

    override fun onPause() {
        super.onPause()
        viewModel.unsubscribeFromTopic()
        viewModel.logOut()
        Log.d("1", "ABCD OUT")
    }

    override fun onResume() {
        super.onResume()
        Log.d("1", "ABCD IN")
    }

}