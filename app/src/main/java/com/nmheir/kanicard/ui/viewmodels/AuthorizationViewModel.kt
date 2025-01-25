package com.nmheir.kanicard.ui.viewmodels

import android.content.Context
import androidx.core.content.contentValuesOf
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.nmheir.kanicard.constants.RefreshTokenKey
import com.nmheir.kanicard.utils.dataStore
import com.nmheir.kanicard.utils.get
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthorizationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val client: SupabaseClient
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    val authState = MutableStateFlow<AuthorizationState>(AuthorizationState.Loading)


    init {
        viewModelScope.launch {
            checkAuthorization()
        }
    }

    private fun checkAuthorization() {

        if (context.dataStore[RefreshTokenKey].isNullOrEmpty()) {
            Timber.d(context.dataStore[RefreshTokenKey])
            authState.value = AuthorizationState.Unauthorized
        } else {
            authState.value = AuthorizationState.Authorized
        }
    }
}

sealed interface AuthorizationState {
    data object Authorized : AuthorizationState
    data object Unauthorized : AuthorizationState
    data object Loading : AuthorizationState
    data class Error(val message: String) : AuthorizationState
}