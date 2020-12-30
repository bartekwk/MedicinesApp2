package com.example.medicinesapp.organizer.allPills.detail

import android.os.Bundle
import androidx.navigation.NavDirections
import com.example.medicinesapp.R
import com.example.medicinesapp.data.AllPillsFragmentData
import com.example.medicinesapp.data.PillDB

class DetailDirection (){

    private data class ActionMasterDetail(val pill: AllPillsFragmentData,val color:Int,val fromFirebase:Boolean,val friendID:String) :
        NavDirections {

        override fun getActionId(): Int = R.id.action_allPills_to_allPillsDetail

        override fun getArguments(): Bundle {
            val result = Bundle()
            result.putParcelable("pill", this.pill)
            result.putInt("color",color)
            result.putBoolean("fromFirebase",fromFirebase)
            result.putString("friendID",friendID)
            return result
        }
    }

    companion object {
        fun actionStartFragmentToDetailFragment(pill: AllPillsFragmentData,color:Int,fromFirebase: Boolean,friendID: String): NavDirections = ActionMasterDetail(pill,color,fromFirebase,friendID)
    }
}
