package com.nmheir.kanicard.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthorizationViewModel @Inject constructor(

) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    val authState = MutableStateFlow<AuthorizationState>(AuthorizationState.Loading)


    init {
        checkAuthorization()
    }

    private fun checkAuthorization() {
        viewModelScope.launch {
            if (auth.currentUser != null) {
                authState.value = AuthorizationState.Authorized
            } else {
                authState.value = AuthorizationState.Unauthorized
            }
        }
    }
}

sealed interface AuthorizationState {
    data object Authorized : AuthorizationState
    data object Unauthorized : AuthorizationState
    data object Loading : AuthorizationState
    data class Error(val message: String) : AuthorizationState
}