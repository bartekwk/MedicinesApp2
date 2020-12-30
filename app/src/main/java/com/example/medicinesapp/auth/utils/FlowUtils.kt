package com.example.medicinesapp.auth.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import androidx.annotation.CheckResult
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.util.concurrent.atomic.AtomicReference

@ExperimentalCoroutinesApi
@CheckResult
fun EditText.textChanges(): Flow<String> {
    return callbackFlow<String> {
        val listener = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                offer(s.toString())
            }
        }
        addTextChangedListener(listener)
        awaitClose { removeTextChangedListener(listener) }
    }.onStart { emit(text.toString()) }
}

@ExperimentalCoroutinesApi
@CheckResult
fun CheckBox.checkedChanges():Flow<Boolean>{
    return callbackFlow {
        setOnCheckedChangeListener { _, b ->
            offer(b)
        }
        awaitClose{setOnCheckedChangeListener(null)}
    }
}

@ExperimentalCoroutinesApi
@CheckResult
fun View.clicks(): Flow<View> {
    return callbackFlow {
        setOnClickListener { offer(it) }
        awaitClose { setOnClickListener(null) }
    }
}

// Taken from https://github.com/Kotlin/kotlinx.coroutines/issues/1498
private object UNINITIALIZED

fun <A, B, R> Flow<A>.withLatestFrom(other: Flow<B>, transform: suspend (A, B) -> R): Flow<R> {
    return flow {
        coroutineScope {
            val latestB = AtomicReference<Any>(UNINITIALIZED)
            val outerScope = this

            launch {
                try {
                    other.collect { latestB.set(it) }
                } catch (e: CancellationException) {
                    outerScope.cancel(e) // cancel outer scope on cancellation exception, too
                }
            }

            collect { a ->
                val b = latestB.get()
                if (b != UNINITIALIZED) {
                    @Suppress("UNCHECKED_CAST")
                    emit(transform(a, b as B))
                }
            }
        }
    }
}
