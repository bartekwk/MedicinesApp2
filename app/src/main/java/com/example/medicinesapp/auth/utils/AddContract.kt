package com.example.medicinesapp.auth.utils

sealed class ViewIntent{
    data class EmailChanged(val email:String?):ViewIntent()
    data class PasswordChanged(val password: String?) : ViewIntent()
    data class CheckedChanged(val checked: Boolean) : ViewIntent()
    object Submit : ViewIntent()
    object Register : ViewIntent()
}

sealed class PartialChange{
    data class ValidationErrors(val error: Set<ValidationError>):PartialChange()
    data class Success(val success:Login):PartialChange()
    data class Error(val error:Login):PartialChange()
    object Register:PartialChange()
}

data class ViewState(
    val isLoading: Boolean?,
    val emailOk: Boolean?,
    val passwordOk: Boolean?,
    val validationError: Set<ValidationError>?,
    val goRegister:Boolean
){
    companion object {
        fun initial() = ViewState(
            isLoading = false,
            emailOk= false,
            passwordOk = false,
            validationError = null,
            goRegister = false
        )
    }
}


enum class ValidationError {
    INVALID_EMAIL_ADDRESS,
    TOO_SHORT_PASSWORD,
}

data class Login(val mail:String?,val password: String?,val errors: Set<ValidationError>,val checked:Boolean)

