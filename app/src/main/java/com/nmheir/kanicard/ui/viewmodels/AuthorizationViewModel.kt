package com.nmheir.kanicard.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthorizationViewModel @Inject constructor(

) : ViewModel() {

    val authState = MutableStateFlow<AuthorizationState>(AuthorizationState.Loading)


    init {
        checkAuthorization()
    }

    private fun checkAuthorization() {
        viewModelScope.launch {

            delay(2000)

            authState.value = AuthorizationState.Unauthorized
        }
    }
}

sealed interface AuthorizationState {
    data object Authorized : AuthorizationState
    data object Unauthorized : AuthorizationState
    data object Loading : AuthorizationState
    data class Error(val message: String) : AuthorizationState
}