package com.example.medicinesapp.notYet.firestore.utils


import com.example.medicinesapp.auth.utils.ValidationError
import com.example.medicinesapp.data.Pill


sealed class MainViewEvent {

    object InitialEvent:MainViewEvent()
    object OneClick:MainViewEvent()
    object TwoClick:MainViewEvent()
    object ThreeClick:MainViewEvent()

}

sealed class MainPartialChange{
    data class ValidationErrors(val error: Set<ValidationError>):MainPartialChange()
    data class Success(val success: String):MainPartialChange()
    data class Error(val error: String):MainPartialChange()
    object Load:MainPartialChange()
    object Load2:MainPartialChange()
    object Load3:MainPartialChange()
}


sealed class LoadResult {

    //if needed more sealed classes
    data class SuccessLoad1(val result: Pill) : LoadResult()

    //Fake object
    object Success3 : LoadResult()
}

data class MainViewState(
    val isLoading: Boolean,
    val list2:MutableList<Int>,
    val list: MutableList<Pill>){
    companion object {
        fun initial() = MainViewState(
            isLoading = false,
            list2 = mutableListOf(),
            list = mutableListOf()
        )
    }
}