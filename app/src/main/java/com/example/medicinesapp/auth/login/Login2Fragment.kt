package com.example.medicinesapp.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.medicinesapp.R
import com.example.medicinesapp.databinding.AuthLoginBinding
import com.example.medicinesapp.utill.Helper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch


class Login2Fragment(): Fragment(){

    private lateinit var binding: AuthLoginBinding
    private val viewModel: Login2ViewModel by viewModels {
        Login2ViewModelFactory(Helper.provideRepository(requireContext()),requireActivity().application)
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AuthLoginBinding.inflate(inflater, container, false)

        viewModel.userLogin.observe(viewLifecycleOwner, Observer {
            val username = it.mail
            val password = it.password
            val checked = it.isChecked
            if(checked){
                binding.username.setText(username)
                binding.password.setText(password)
                binding.checkbox.isChecked = checked
            }
        })

        binding.sign.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()
            val checked = binding.checkbox.isChecked

            lifecycleScope.launch {

                val ok = viewModel.login(username,password,checked)
                if(ok){
                    findNavController().navigate(R.id.action_login_to_dashboard)
                    viewModel.updateUser(username,password,checked)
                }
            }
        }

        binding.register.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        return binding.root

    }
}

