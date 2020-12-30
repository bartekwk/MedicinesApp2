package com.example.medicinesapp.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.medicinesapp.adding.steps.FirstStepFragment
import com.example.medicinesapp.adding.steps.FourthStepFragment
import com.example.medicinesapp.adding.steps.SecondStepFragment
import com.example.medicinesapp.adding.steps.ThirdStepFragment
import io.reactivex.subjects.PublishSubject


const val FIRST_STEP_INDEX = 0
const val SECOND_STEP_INDEX = 1
const val THIRD_STEP_INDEX = 2
const val FOURTH_STEP_INDEX = 3


class AddingFragmentAdapter(fragment: Fragment, mySubject: PublishSubject<String>):FragmentStateAdapter(fragment) {


    private val createFragments: Map<Int, () -> Fragment> = mapOf(
        FIRST_STEP_INDEX to { FirstStepFragment() },
        SECOND_STEP_INDEX to { SecondStepFragment() },
        THIRD_STEP_INDEX to { ThirdStepFragment() },
        FOURTH_STEP_INDEX to{FourthStepFragment()}
    )


    override fun createFragment(position: Int): Fragment {
        return createFragments[position]?.invoke() ?:throw IndexOutOfBoundsException()
    }

    override fun getItemCount(): Int {
        return createFragments.size
    }


}