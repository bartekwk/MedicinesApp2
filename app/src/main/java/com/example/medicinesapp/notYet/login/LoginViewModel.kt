package com.example.medicinesapp.notYet.login

import android.util.Log
import androidx.core.util.PatternsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicinesapp.auth.utils.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@FlowPreview
@ExperimentalCoroutinesApi
class LoginViewModel:ViewModel() {

    @ExperimentalCoroutinesApi
    private val _intentChannel = ConflatedBroadcastChannel<ViewIntent>()
    val viewState: StateFlow<ViewState>

    private val auth = FirebaseAuth.getInstance()

    init {
        viewState = MutableStateFlow(ViewState.initial())

        _intentChannel.asFlow()
            .handleViewIntents()
            .handlePartialChanges()
            .onEach { viewState.value = it }
            .launchIn(viewModelScope)
    }


    suspend fun processIntent(intent: ViewIntent) = _intentChannel.send(intent)


    private fun Flow<PartialChange>.handlePartialChanges(): Flow<ViewState> {

        return map { change ->
            val event = when (change) {
                is PartialChange.ValidationErrors -> ViewState(isLoading = false, emailOk = false, passwordOk = false, validationError = change.error,goRegister = false)
                is PartialChange.Success -> ViewState(isLoading = true, emailOk = true, passwordOk = true, validationError = null,goRegister = false)
                is PartialChange.Error -> ViewState(isLoading = false, emailOk = false, passwordOk = false, validationError = null,goRegister = false)
                is PartialChange.Register -> ViewState(isLoading = false, emailOk = true, passwordOk = true, validationError = null,goRegister = true)
            }
            //Log.d("1", "HAAAALO $event ")
            event
        }
    }


    private fun Flow<ViewIntent>.handleViewIntents(): Flow<PartialChange> {


        val email =
            this.filterIsInstance<ViewIntent.EmailChanged>()
                .map { it.email }
                //.onEach { Log.d("1", "RAZ ") }
                .map { validateEmail(it) to it }


        val password =
            this.filterIsInstance<ViewIntent.PasswordChanged>()
                .map { it.password }
                //.onEach { Log.d("1", "DWA ") }
                .map { validatePassword(it) to it }

        val checked = this.filterIsInstance<ViewIntent.CheckedChanged>().map { it.checked }


        val combine = combine(email, password,checked) { a, b,c ->
            //Log.d("1", "SUMA $a , $b ")
            Login(a.second, b.second, a.first + b.first,c)
        }

        val submit = this.filterIsInstance<ViewIntent.Submit>()
            .withLatestFrom(combine) { _, combine -> combine }
            .filter { it.errors.isEmpty() }
            .onEach { Log.d("1", "END $it ") }
            .map { login(it) }

        val error = combine.map { PartialChange.ValidationErrors(it.errors) as PartialChange }

        val register = this.filterIsInstance<ViewIntent.Register>().map{PartialChange.Register as PartialChange}



        return merge(submit, error,register)
    }




   private suspend fun login(login: Login):PartialChange = suspendCoroutine {cont->

       Log.d("1", "ALAMAKOTA ${login.checked} ")

            auth.signInWithEmailAndPassword(login.mail!!, login.password!!)
                .addOnSuccessListener {
                    Log.d("1", "login: SUCCESS")
                    cont.resume(PartialChange.Success(login) as PartialChange)
                }.addOnFailureListener {
                    Log.d("1", "login: FAILURE")
                    cont.resume(PartialChange.Error(login) as PartialChange)
                }
    }


        private fun validateEmail(email: String?): Set<ValidationError> {
            val errors = mutableSetOf<ValidationError>()

            if (email == null || !PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
                errors += ValidationError.INVALID_EMAIL_ADDRESS
            }
            // more validation here
            return errors
        }

        private fun validatePassword(password: String?): Set<ValidationError> {
            val errors = mutableSetOf<ValidationError>()

            if (password == null || password.length < 6) {
                errors += ValidationError.TOO_SHORT_PASSWORD
            }
            // more validation here
            return errors
        }

}