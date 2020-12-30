package com.example.medicinesapp.notYet.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.medicinesapp.R
import com.example.medicinesapp.auth.utils.*
import com.example.medicinesapp.databinding.AuthLoginBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*


class LoginFragment():Fragment(){

    private lateinit var binding: AuthLoginBinding
    private lateinit var viewModel : LoginViewModel

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel= ViewModelProviders.of(this).get(LoginViewModel::class.java)

        binding = AuthLoginBinding.inflate(inflater,container,false)


        intents().onEach { viewModel.processIntent(it)  }.launchIn(lifecycleScope)

        viewModel.viewState
            .onEach { Log.d("1", "COLLECTED $it")
                states(it)
            }
            .launchIn(lifecycleScope)

        return binding.root

    }


    @ExperimentalCoroutinesApi
    private fun intents(): Flow<ViewIntent> {
        return merge(binding.username.textChanges().map { ViewIntent.EmailChanged(it)},
            binding.password.textChanges().map { ViewIntent.PasswordChanged(it)},
            binding.sign.clicks().map { ViewIntent.Submit },
            binding.register.clicks().map { ViewIntent.Register },
            binding.checkbox.checkedChanges().map { ViewIntent.CheckedChanged(it)}
        )
    }

    private fun states(state:ViewState){

        state.isLoading?.let {
            if(it) {
                findNavController().navigate(R.id.action_login_to_dashboard)
            }
        }
        if(state.goRegister){
            findNavController().navigate(R.id.action_login_to_register)
        }

    }



}