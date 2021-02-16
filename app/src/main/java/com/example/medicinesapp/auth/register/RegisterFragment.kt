package com.example.medicinesapp.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.medicinesapp.R
import com.example.medicinesapp.data.User
import com.example.medicinesapp.databinding.AuthRegisterBinding
import com.example.medicinesapp.utill.Helper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterFragment():Fragment() {


    private lateinit var binding: AuthRegisterBinding
    private var type:String?=null
    private val viewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(Helper.provideRepository(requireContext()),requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = AuthRegisterBinding.inflate(inflater,container,false)

        binding.type.setOnClickListener {
            handleTypeChange()
        }

        binding.type2.setOnClickListener {
            handleTypeChange()
        }

        binding.register.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }

        binding.sign.setOnClickListener {
            performRegister()
        }


        return binding.root
    }

    private fun handleTypeChange(){

        when(binding.type.text){
            "PACJENT" -> {
                binding.type.text = "LEKARZ"
                type = "LEKARZ"
                binding.imageView.setImageResource(R.drawable.ic_undraw_doctors_hwty)
            }
            "LEKARZ" -> {
                binding.type.text = "PACJENT"
                type="PACJENT"
                binding.imageView.setImageResource(R.drawable.ic_undraw_doctor_kw5l)
            }
        }
    }


    private fun saveUser(user: User){

        lifecycleScope.launch{
            viewModel.isRegisterOk.collect {
                if(it=="true"){
                    withContext(Dispatchers.IO){viewModel.addUserToDatabase(user)}
                    findNavController().navigate(R.id.action_register_to_dashboard)
                    viewModel._isRegisterOk.value="null"
                }
            }
        }
    }



    private fun performRegister() {

        val name = binding.name.text.toString()
        val username = binding.username.text.toString()
        val password = binding.password.text.toString()

        val isDoctor = type == "LEKARZ"

        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {

        } else {
            val user = User(null, name, username, isDoctor,0,true,null,null)
            viewModel.register(username, password)
            saveUser(user)
        }
    }


}