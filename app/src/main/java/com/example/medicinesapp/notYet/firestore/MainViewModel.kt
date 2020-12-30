package com.example.medicinesapp.notYet.firestore


import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medicinesapp.notYet.firestore.utils.LoadResult
import com.example.medicinesapp.notYet.firestore.utils.MainPartialChange
import com.example.medicinesapp.notYet.firestore.utils.MainViewEvent
import com.example.medicinesapp.notYet.firestore.utils.MainViewState
import com.example.medicinesapp.repository.AppRepository
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

@Suppress("NAME_SHADOWING")
@SuppressLint("CheckResult")
class MainViewModel(private val repository: AppRepository): ViewModel() {

    private val intentSubject:PublishSubject<MainViewEvent> = PublishSubject.create()

    private val statesLiveData: MutableLiveData<MainViewState> = MutableLiveData()

    private val eventFilter : ObservableTransformer<MainViewEvent,MainViewEvent>
    get() = ObservableTransformer { event->
        event.publish {next->
            Observable.merge<MainViewEvent>(
                next.ofType(MainViewEvent.InitialEvent::class.java).take(1),
                next.filter{it != MainViewEvent.InitialEvent}
            )//Here filter or map
        }
    }

    init{
        val disposable =intentSubject
            //.compose(eventFilter)
            .map(this::actionFromEvent)
            .doOnNext { Log.d("1", "INSIDE!! $it ")}
            .compose(performAction())
            .scan(MainViewState.initial(),reducer)
            .subscribe {
                Log.d("1", "KURR $it ")
                statesLiveData.value = it
            }
    }


    fun states(): MutableLiveData<MainViewState> {
        return statesLiveData
    }


    fun handleViewIntents(observable : Observable<MainViewEvent>) {
        observable.subscribe(intentSubject)
    }

    private fun actionFromEvent(event: MainViewEvent): MainPartialChange {
        return when(event){
            is MainViewEvent.OneClick-> MainPartialChange.Load
            is MainViewEvent.TwoClick -> MainPartialChange.Load2
            is MainViewEvent.ThreeClick -> MainPartialChange.Load3
            is MainViewEvent.InitialEvent -> MainPartialChange.Load
        }
    }

    private fun performAction(): ObservableTransformer<MainPartialChange, LoadResult> {
        return ObservableTransformer{ action->
            action.publish {next->
                Observable.merge(
                next.ofType(MainPartialChange.Load::class.java).doOnNext { Log.d("1", "performAction: !! ")  }.compose(repository.getMyRecipes()),
                next.ofType(MainPartialChange.Load3::class.java).map { LoadResult.Success3 as LoadResult })
            }
        }
    }


companion object {
    private val reducer = BiFunction { prevState: MainViewState, result: LoadResult ->
        when (result) {
            is LoadResult.SuccessLoad1 -> {
                prevState.list.add(result.result)
                prevState.copy(isLoading = false, list = prevState.list)
            }
            is LoadResult.Success3 ->{
                val size = prevState.list2.size+1
                prevState.list2.add(size+1)
                prevState.copy(false, list2 = prevState.list2)
            }
        }
    }
}


}

