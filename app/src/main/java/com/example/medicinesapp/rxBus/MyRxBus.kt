package com.example.medicinesapp.rxBus

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

object MyRxBus{

    private val publisher = PublishSubject.create<Any>()


    fun publish(event:Any){

        publisher.onNext(event)
    }


    fun<T>listen(eventType:Class<T>): Observable<T> {
        return publisher.ofType(eventType)
    }


}